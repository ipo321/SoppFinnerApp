package no.uio.ifi.in2000.team16.data.roomDatabase


import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import no.uio.ifi.in2000.team16.data.roomDatabase.userDatabase.UserDao
import javax.inject.Singleton

@Module
//Makes one instance of the database for as long as the app lives.
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        //Makes the singleton live for as long as the app does, not just for the activity
        @ApplicationContext context : Context
    ): AppDatabase{

        //This creates the Room itself, we have to cast it since the room api
        // requires it
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }
    //calls upon the userDataAccessObject when the userDao is requested
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
}