package com.obu.restaurant.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.obu.restaurant.domain.usecases.ObtenerCategoriasUseCase
import com.obu.restaurant.domain.usecases.ObtenerProductosUseCase

class CategorySelectionViewModelFactory(
    private val obtenerCategoriasUseCase: ObtenerCategoriasUseCase,
    private val obtenerProductosUseCase: ObtenerProductosUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategorySelectionViewModel::class.java)) {
            return CategorySelectionViewModel(obtenerCategoriasUseCase, obtenerProductosUseCase) as T
        }
        throw IllegalArgumentException("Clase de ViewModel desconocida")
    }
}