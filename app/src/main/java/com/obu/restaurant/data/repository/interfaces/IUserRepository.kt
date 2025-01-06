package com.obu.restaurant.data.repository.interfaces

interface  IUserRepository {
    suspend fun login(username: String, password: String): Map<String, Any>
}