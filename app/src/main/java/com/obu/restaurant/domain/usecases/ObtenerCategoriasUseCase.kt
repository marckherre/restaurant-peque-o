package com.obu.restaurant.domain.usecases

import com.obu.restaurant.data.models.Categoria
import com.obu.restaurant.data.repository.interfaces.ICategoriaRepository

class ObtenerCategoriasUseCase(private val categoriaRepository: ICategoriaRepository) {
    suspend fun execute(): List<Categoria> {
        return categoriaRepository.obtenerCategorias()
    }
}