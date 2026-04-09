package no.uio.ifi.in2000.team16.data.roomDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import no.uio.ifi.in2000.team16.data.roomDatabase.userDatabase.User
import no.uio.ifi.in2000.team16.data.roomDatabase.userDatabase.UserDao


//
@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

}