package com.obu.restaurant.domain.usecases
import com.obu.restaurant.data.models.User
import com.obu.restaurant.data.repository.interfaces.IUserRepository

class LoginUseCase(private val userRepository: IUserRepository) {

    suspend fun execute(username: String, password: String): Result<User> {
        val result = userRepository.login(username, password)
        val status = result["status"] as Boolean
        val message = result["message"] as String

        return if (status) {
            // Si el login fue exitoso, devolver Result.success con el usuario
            val user = result["user"] as User
            Result.success(user)
        } else {
            // Si el login falló, devolver Result.failure con una excepción y el mensaje
            Result.failure(Exception(message))
        }
    }
}