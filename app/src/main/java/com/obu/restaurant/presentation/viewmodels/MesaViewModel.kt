package com.obu.restaurant.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obu.restaurant.data.models.Atencion
import com.obu.restaurant.data.models.Mesa
import com.obu.restaurant.domain.usecases.GetActiveMesasUseCase
import com.obu.restaurant.domain.usecases.RegistrarAtencionUseCase
import kotlinx.coroutines.launch

class MesaViewModel(private val getActiveMesasUseCase: GetActiveMesasUseCase,
                    private val registrarAtencionUseCase: RegistrarAtencionUseCase
) : ViewModel() {

    private val _mesas = MutableLiveData<Result<List<Mesa>>>()
    val mesas: LiveData<Result<List<Mesa>>> get() = _mesas

    fun fetchMesasConEstado() {
        viewModelScope.launch {
            try {
                // Ejecutar el caso de uso que devolverá un Result<List<Mesa>>
                val mesasConEstadoResult = getActiveMesasUseCase.execute()

                // Publicar el resultado (éxito o error)
                _mesas.postValue(mesasConEstadoResult)

                // Logs para depuración
                mesasConEstadoResult.fold(
                    onSuccess = { mesas ->
                        Log.d("MesaViewModel", "Mesas recibidas: ${mesas.size}")
                    },
                    onFailure = { error ->
                        Log.e("MesaViewModel", "Error al obtener las mesas: ${error.message}")
                    }
                )

            } catch (e: Exception) {
                // Si ocurre alguna excepción inesperada, manejarla
                Log.e("MesaViewModel", "Error inesperado al obtener las mesas", e)
                _mesas.postValue(Result.failure(e))
            }
        }
    }

    fun registrarAtencion(atencion: Atencion): LiveData<Result<Atencion>> {
        val resultado = MutableLiveData<Result<Atencion>>()

        viewModelScope.launch {
            try {
                // No es necesario envolver de nuevo en Result.success(), ya que 'execute' devuelve Result directamente
                val atencionCreada = registrarAtencionUseCase.execute(atencion)
                resultado.postValue(atencionCreada)  // Publicar el Result<Atencion> directamente
            } catch (e: Exception) {
                resultado.postValue(Result.failure(e))  // Emitimos un error en caso de fallo
            }
        }

        return resultado  // Retornamos el LiveData para que la vista pueda observar el resultado
    }

}
