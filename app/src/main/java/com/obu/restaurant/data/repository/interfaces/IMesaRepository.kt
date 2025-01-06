package com.obu.restaurant.data.repository.interfaces

interface IMesaRepository {
    suspend fun getMesasConEstado(): Map<String, Any>
}