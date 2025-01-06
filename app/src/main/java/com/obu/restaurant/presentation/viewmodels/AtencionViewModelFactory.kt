package com.obu.restaurant.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.obu.restaurant.domain.usecases.ObtenerDetallesOrdenUseCase
import com.obu.restaurant.domain.usecases.CancelarDetalleOrdenUseCase

class AtencionViewModelFactory(
    private val obtenerDetallesOrdenUseCase: ObtenerDetallesOrdenUseCase,
    private val cancelarDetalleOrdenUseCase: CancelarDetalleOrdenUseCase // Agregamos el segundo parámetro
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AtencionViewModel::class.java)) {
            AtencionViewModel(
                obtenerDetallesOrdenUseCase,
                cancelarDetalleOrdenUseCase // Pasamos ambos casos de uso al ViewModel
            ) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
