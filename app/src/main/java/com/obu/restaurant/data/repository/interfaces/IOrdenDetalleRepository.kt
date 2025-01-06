package com.obu.restaurant.data.repository.interfaces

interface IOrdenDetalleRepository {
    suspend fun obtenerDetallesOrden(idAtencion: Int): Map<String, Any>
    suspend fun cancelarDetalleOrden(idDetalle: Int): Map<String, Any>
}