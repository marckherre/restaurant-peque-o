package com.obu.restaurant.data.repository.implementations

import com.obu.restaurant.data.database.AtencionDao
import com.obu.restaurant.data.models.Atencion
import com.obu.restaurant.data.repository.interfaces.IAtencionRepository

class AtencionRepository(private val atencionDao: AtencionDao) : IAtencionRepository {

    // Registrar una nueva atención y devolver el resultado en forma de mapa
    override suspend fun registrarAtencion(atencion: Atencion): Map<String, Any> {
        return atencionDao.insertAtencion(atencion)
    }

}