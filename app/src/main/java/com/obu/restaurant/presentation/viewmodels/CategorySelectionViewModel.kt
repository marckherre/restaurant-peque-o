package com.obu.restaurant.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obu.restaurant.data.models.Categoria
import com.obu.restaurant.data.models.Producto
import com.obu.restaurant.domain.usecases.ObtenerCategoriasUseCase
import com.obu.restaurant.domain.usecases.ObtenerProductosUseCase
import kotlinx.coroutines.launch

class CategorySelectionViewModel(
    private val obtenerCategoriasUseCase: ObtenerCategoriasUseCase,
    private val obtenerProductosUseCase: ObtenerProductosUseCase
) : ViewModel() {

    private val _categorias = MutableLiveData<List<Categoria>>()
    val categorias: LiveData<List<Categoria>> get() = _categorias

    private val _productosPorCategoria = MutableLiveData<List<Producto>>()
    val productosPorCategoria: LiveData<List<Producto>> get() = _productosPorCategoria

    // Cargar las categorías disponibles
    fun cargarCategorias() {
        viewModelScope.launch {
            val categoriasResult = obtenerCategoriasUseCase.execute()
            _categorias.postValue(categoriasResult)
        }
    }

    // Cargar los productos de una categoría específica
    fun cargarProductosPorCategoria(idCategoria: Int) {
        viewModelScope.launch {
            val productosResult = obtenerProductosUseCase.execute(idCategoria)
            _productosPorCategoria.postValue(productosResult)
        }
    }
}