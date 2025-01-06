package com.obu.restaurant.domain.usecases

import com.obu.restaurant.data.models.OrdenDetalle
import com.obu.restaurant.data.repository.interfaces.IOrdenDetalleRepository

class ObtenerDetallesOrdenUseCase(private val ordenDetalleRepository: IOrdenDetalleRepository) {

    suspend fun execute(idAtencion: Int): Result<List<OrdenDetalle>> {
        val result = ordenDetalleRepository.obtenerDetallesOrden(idAtencion)
        val status = result["status"] as Boolean
        val message = result["message"] as String

        return if (status) {
            val detallesOrden = result["detalles"] as List<OrdenDetalle>
            Result.success(detallesOrden)
        } else {
            Result.failure(Exception(message))
        }
    }
}
