package com.obu.restaurant.domain.usecases

import com.obu.restaurant.data.repository.interfaces.IOrdenDetalleRepository

class CancelarDetalleOrdenUseCase(private val ordenDetalleRepository: IOrdenDetalleRepository) {

    suspend fun execute(idDetalle: Int): Result<Unit> {
        val result = ordenDetalleRepository.cancelarDetalleOrden(idDetalle)
        val status = result["status"] as Boolean
        val message = result["message"] as String

        return if (status) {
            Result.success(Unit)
        } else {
            Result.failure(Exception(message))
        }
    }
}