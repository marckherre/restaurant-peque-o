package com.obu.restaurant.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obu.restaurant.data.models.OrdenDetalle
import com.obu.restaurant.domain.usecases.CancelarDetalleOrdenUseCase
import com.obu.restaurant.domain.usecases.ObtenerDetallesOrdenUseCase
import kotlinx.coroutines.launch

class AtencionViewModel(
    private val obtenerDetallesOrdenUseCase: ObtenerDetallesOrdenUseCase,
    private val cancelarDetalleOrdenUseCase: CancelarDetalleOrdenUseCase // Nuevo caso de uso
) : ViewModel() {
    private val _productosOrdenados = MutableLiveData<Result<List<OrdenDetalle>>>()
    val productosOrdenados: LiveData<Result<List<OrdenDetalle>>> get() = _productosOrdenados

    private val _productosAgregando = MutableLiveData<Result<List<OrdenDetalle>>>()
    val productosAgregando: LiveData<Result<List<OrdenDetalle>>> get() = _productosAgregando

    fun obtenerDetallesOrden(idAtencion: Int) {
        viewModelScope.launch {
            try {
                val result = obtenerDetallesOrdenUseCase.execute(idAtencion)

                result.fold(
                    onSuccess = { detalles ->
                        // Usar las variables _productosOrdenados y _productosAgregando del ViewModel
                        val productosFiltradosOrdenados = detalles.filter { it.ind_est in listOf("1", "2", "3") }
                        val productosFiltradosAgregando = detalles.filter { it.ind_est == "0" }

                        // Postear los resultados filtrados
                        _productosOrdenados.postValue(Result.success(productosFiltradosOrdenados))
                        _productosAgregando.postValue(Result.success(productosFiltradosAgregando))
                    },
                    onFailure = { error ->
                        _productosOrdenados.postValue(Result.failure(error))
                        _productosAgregando.postValue(Result.failure(error))
                    }
                )
            } catch (e: Exception) {
                _productosOrdenados.postValue(Result.failure(e))
                _productosAgregando.postValue(Result.failure(e))
            }
        }
    }

    fun confirmarCambiosEnAgregados(cantidadesActualizadas: Map<Int, Int>) {
        // Aquí procesarás las cantidades actualizadas antes de enviar a la base de datos.
        viewModelScope.launch {
            cantidadesActualizadas.forEach { (idProducto, nuevaCantidad) ->
                Log.d("AtencionViewModel", "Producto $idProducto tiene nueva cantidad: $nuevaCantidad")
                // Aquí puedes llamar al caso de uso para actualizar la base de datos
            }
        }
    }

    fun cancelarDetalle(idDetalle: Int) {
        viewModelScope.launch {
            val result = cancelarDetalleOrdenUseCase.execute(idDetalle)
            result.fold(
                onSuccess = {
                    Log.d("AtencionViewModel", "Producto cancelado correctamente: $idDetalle")
                    // Filtrar el producto cancelado de la lista observable
                    _productosAgregando.value?.onSuccess { listaActual ->
                        val nuevaLista = listaActual.filter { it.id != idDetalle }
                        _productosAgregando.postValue(Result.success(nuevaLista))
                    }
                },
                onFailure = { error ->
                    Log.e("AtencionViewModel", "Error al cancelar producto: ${error.message}")
                }
            )
        }
    }
}
