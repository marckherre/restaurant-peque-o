package com.obu.restaurant.data.repository.interfaces

import com.obu.restaurant.data.models.Producto

interface IProductoRepository {
    suspend fun obtenerProductosPorCategoria(idCategoria: Int): List<Producto>
}