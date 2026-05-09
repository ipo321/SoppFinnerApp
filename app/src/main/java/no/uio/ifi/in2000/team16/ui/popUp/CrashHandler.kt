package no.uio.ifi.in2000.team16.ui.popUp

import android.content.Context
import android.content.Intent

class CrashHandler(private val context: Context) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try{
            val intent = Intent(context, CrashActivity::class.java).apply{
                putExtra("error_message", throwable.localizedMessage)
                putExtra("stack_trace", throwable.stackTraceToString())
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            context.startActivity(intent)

            //Killing the crashed process
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(1)

        } catch (_: Exception){
            //Fallback to normal handler if this fails
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}