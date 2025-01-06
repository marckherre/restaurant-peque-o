package com.obu.restaurant.data.repository.implementations

import com.obu.restaurant.data.database.OrdenDetalleDao
import com.obu.restaurant.data.repository.interfaces.IOrdenDetalleRepository

class OrdenDetalleRepository(private val ordenDetalleDao: OrdenDetalleDao) :
    IOrdenDetalleRepository {

    override suspend fun obtenerDetallesOrden(idAtencion: Int): Map<String, Any> {
        return ordenDetalleDao.getDetallesOrden(idAtencion)
    }
    override suspend fun cancelarDetalleOrden(idDetalle: Int): Map<String, Any> {
        return ordenDetalleDao.cancelarDetalle(idDetalle)
    }
}