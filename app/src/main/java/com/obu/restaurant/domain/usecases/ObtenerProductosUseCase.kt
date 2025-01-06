package com.obu.restaurant.domain.usecases

import com.obu.restaurant.data.models.Producto
import com.obu.restaurant.data.repository.interfaces.IProductoRepository

class ObtenerProductosUseCase(private val productoRepository: IProductoRepository) {
    suspend fun execute(idCategoria: Int): List<Producto> {
        return productoRepository.obtenerProductosPorCategoria(idCategoria)
    }
}