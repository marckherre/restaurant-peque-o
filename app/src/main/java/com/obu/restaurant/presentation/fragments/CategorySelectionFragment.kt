package com.obu.restaurant.presentation.fragments

import DatabaseService
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.obu.restaurant.data.models.Producto
import com.obu.restaurant.data.repository.implementations.CategoriaRepository
import com.obu.restaurant.data.repository.implementations.ProductoRepository
import com.obu.restaurant.databinding.FragmentCategorySelectionBinding
import com.obu.restaurant.domain.usecases.ObtenerCategoriasUseCase
import com.obu.restaurant.domain.usecases.ObtenerProductosUseCase
import com.obu.restaurant.presentation.viewmodels.CategorySelectionViewModel
import com.obu.restaurant.presentation.viewmodels.CategorySelectionViewModelFactory

class CategorySelectionFragment : Fragment() {

    private lateinit var binding: FragmentCategorySelectionBinding
    private lateinit var viewModel: CategorySelectionViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var productAdapter: ProductAdapter
    private val productos = mutableListOf<Producto>() // Lista temporal para los productos mostrados

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategorySelectionBinding.inflate(inflater, container, false)

        // Configurar dependencias
        val databaseService = DatabaseService()
        val categoriaRepository = CategoriaRepository(databaseService)
        val productoRepository = ProductoRepository(databaseService)
        val obtenerCategoriasUseCase = ObtenerCategoriasUseCase(categoriaRepository)
        val obtenerProductosUseCase = ObtenerProductosUseCase(productoRepository)
        val factory = CategorySelectionViewModelFactory(obtenerCategoriasUseCase, obtenerProductosUseCase)
        viewModel = ViewModelProvider(this, factory)[CategorySelectionViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar RecyclerViews
        setupRecyclerViews()

        // Observamos las categorías y los productos
        viewModel.categorias.observe(viewLifecycleOwner) { categorias ->
            categoryAdapter.updateCategorias(categorias) // Actualizar categorías en el adaptador
        }

        viewModel.productosPorCategoria.observe(viewLifecycleOwner) { productosCargados ->
            productos.clear()
            productos.addAll(productosCargados) // Actualizar la lista temporal de productos
            productAdapter.updateProductos(productosCargados) // Actualizar productos en el adaptador
        }

        // Cargar las categorías al inicio
        viewModel.cargarCategorias()

        // Listener para el botón "Agregar seleccionados"
        binding.btnConfirmarSeleccion.setOnClickListener {
            // Filtrar productos con cantidad mayor a 0
            val productosSeleccionados = productos.filter { it.cantidad > 0 }

            if (productosSeleccionados.isNotEmpty()) {
                // Pasar los productos seleccionados al ViewModel compartido
                viewModel.agregarProductos(productosSeleccionados)

                // Cerrar el fragmento y regresar a la pantalla anterior
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "No se han seleccionado productos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerViews() {
        // Configurar RecyclerView de Categorías
        categoryAdapter = CategoryAdapter { categoriaId ->
            viewModel.cargarProductosPorCategoria(categoriaId) // Cargar productos al seleccionar categoría
        }
        binding.recyclerViewCategorias.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }

        // Configurar RecyclerView de Productos
        productAdapter = ProductAdapter()
        binding.recyclerViewProductos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }
    }
}