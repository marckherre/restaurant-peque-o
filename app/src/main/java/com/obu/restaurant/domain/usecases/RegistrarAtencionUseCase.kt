package com.obu.restaurant.domain.usecases

import com.obu.restaurant.data.models.Atencion
import com.obu.restaurant.data.repository.interfaces.IAtencionRepository

class RegistrarAtencionUseCase(private val atencionRepository: IAtencionRepository) {

    suspend fun execute(atencion: Atencion): Result<Atencion> {
        val result = atencionRepository.registrarAtencion(atencion)
        val status = result["status"] as Boolean
        val message = result["message"] as String

        return if (status) {
            val atencionCreada = result["atencion"] as Atencion
            Result.success(atencionCreada)
        } else {
            Result.failure(Exception(message))
        }
    }
}