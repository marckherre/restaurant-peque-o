package com.obu.restaurant.domain.usecases

import com.obu.restaurant.data.models.Mesa
import com.obu.restaurant.data.repository.interfaces.IMesaRepository

class GetActiveMesasUseCase(private val mesaRepository: IMesaRepository) {

    suspend fun execute(): Result<List<Mesa>> {
        val result = mesaRepository.getMesasConEstado()
        val status = result["status"] as Boolean
        val message = result["message"] as String

        return if (status) {
            // Si se obtuvieron las mesas, devolver Result.success con la lista de mesas
            val mesas = result["mesas"] as List<Mesa>
            Result.success(mesas)
        } else {
            // Si hubo un error, devolver Result.failure con el mensaje de error
            Result.failure(Exception(message))
        }
    }
}
