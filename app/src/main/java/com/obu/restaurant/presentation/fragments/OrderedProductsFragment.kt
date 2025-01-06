package com.obu.restaurant.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.obu.restaurant.databinding.FragmentOrderedProductsBinding
import com.obu.restaurant.presentation.viewmodels.AtencionViewModel
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.obu.restaurant.presentation.adapters.OrderedProductsAdapter

class OrderedProductsFragment : Fragment() {

    private lateinit var binding: FragmentOrderedProductsBinding
    private lateinit var orderedAdapter: OrderedProductsAdapter
    private lateinit var atencionViewModel: AtencionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla el layout correctamente
        binding = FragmentOrderedProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar el RecyclerView
        orderedAdapter = OrderedProductsAdapter(emptyList())
        binding.recyclerViewOrderedProducts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = orderedAdapter  // Asegúrate de que el adaptador esté asignado aquí
        }

        // Log para verificar el tamaño del RecyclerView
        binding.recyclerViewOrderedProducts.viewTreeObserver.addOnGlobalLayoutListener {
            Log.d("RecyclerViewSize", "Ancho: ${binding.recyclerViewOrderedProducts.width}, Alto: ${binding.recyclerViewOrderedProducts.height}")
        }

        // Obtener el ViewModel desde la actividad
        atencionViewModel = ViewModelProvider(requireActivity()).get(AtencionViewModel::class.java)

        // Obtener el ID de la atención desde los extras
        val mesaId = requireActivity().intent.getIntExtra("mesa_id", 0)

        // Llamar al método del ViewModel para obtener los detalles
        atencionViewModel.obtenerDetallesOrden(mesaId)

        // Observar la lista de productos ordenados (filtrados por estado)
        atencionViewModel.productosOrdenados.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { detallesOrdenados ->
                    Log.d("OrderedProductsFragment", "Productos ordenados obtenidos: ${detallesOrdenados.size}")
                    orderedAdapter.updateDetalles(detallesOrdenados)  // Actualizar el adaptador con los detalles filtrados
                },
                onFailure = { error ->
                    Toast.makeText(context, "Error al cargar los productos ordenados", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}
