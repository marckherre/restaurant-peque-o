package com.obu.restaurant.data.repository.implementations

import com.obu.restaurant.data.database.MesaDao
import com.obu.restaurant.data.repository.interfaces.IMesaRepository

class MesaRepository(private val mesaDao: MesaDao) : IMesaRepository {

    override suspend fun getMesasConEstado(): Map<String, Any> {
        return mesaDao.getMesasConEstado()
    }
}
