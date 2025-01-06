package com.obu.restaurant.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.obu.restaurant.domain.usecases.GetActiveMesasUseCase
import com.obu.restaurant.domain.usecases.RegistrarAtencionUseCase

class MesaViewModelFactory(
    private val getActiveMesasUseCase: GetActiveMesasUseCase,
    private val registrarAtencionUseCase: RegistrarAtencionUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MesaViewModel::class.java)) {
            // Pasar ambos casos de uso al ViewModel
            MesaViewModel(getActiveMesasUseCase, registrarAtencionUseCase) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}