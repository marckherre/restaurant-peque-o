package com.obu.restaurant.data.repository.interfaces

import com.obu.restaurant.data.models.Categoria

interface ICategoriaRepository {
    suspend fun obtenerCategorias(): List<Categoria>
}