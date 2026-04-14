package no.uio.ifi.in2000.team16.data.roomDatabase.userDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

//Lowkey an example interface for how we can do the rest of the dataclasses interfaces
//An interface with the SQL commands for differnt
//Operations we can  use to manipluate the database

//functions are all SQL querys simplified to functioncalls

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    suspend fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE uid IN (:userIds) ")
    suspend fun loadAllByIds(userIds: IntArray): List<User>

    @Insert
    suspend fun insertAll(vararg users: User)

    @Delete
    suspend fun delete(user: User)
}