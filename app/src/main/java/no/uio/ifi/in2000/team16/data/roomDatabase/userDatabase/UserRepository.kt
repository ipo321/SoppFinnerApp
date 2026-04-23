package no.uio.ifi.in2000.team16.data.roomDatabase.userDatabase

import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun getAllUsers(): List<User> = userDao.getAll()

    suspend fun insertUsers(vararg users: User) = userDao.insertAll(*users)

    suspend fun deleteUser(user: User) = userDao.delete(user)

}
