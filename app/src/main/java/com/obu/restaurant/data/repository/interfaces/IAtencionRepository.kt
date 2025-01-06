package com.obu.restaurant.data.repository.interfaces

import com.obu.restaurant.data.models.Atencion

interface IAtencionRepository {
    suspend fun registrarAtencion(atencion: Atencion): Map<String, Any>
}