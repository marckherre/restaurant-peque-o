package com.obu.restaurant.data.repository.implementations

import com.obu.restaurant.data.database.UserDao
import com.obu.restaurant.data.repository.interfaces.IUserRepository

class UserRepository(private val userDao: UserDao) : IUserRepository {

    override suspend fun login(username: String, password: String): Map<String, Any> {
        return userDao.authenticateUser(username, password)
    }
}