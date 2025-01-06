package com.obu.restaurant.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.obu.restaurant.databinding.FragmentAddingProductsBinding
import com.obu.restaurant.presentation.adapters.AddingProductsAdapter
import com.obu.restaurant.presentation.viewmodels.AtencionViewModel


class AddingProductsFragment : Fragment() {

    private lateinit var binding: FragmentAddingProductsBinding
    private lateinit var addingAdapter: AddingProductsAdapter
    private lateinit var atencionViewModel: AtencionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddingProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        atencionViewModel = ViewModelProvider(requireActivity()).get(AtencionViewModel::class.java)

        addingAdapter = AddingProductsAdapter(
            emptyList(),
            onCancelarProducto = { idDetalle ->
                atencionViewModel.cancelarDetalle(idDetalle)
            }
        )

        binding.recyclerViewAddingProducts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = addingAdapter
        }

        atencionViewModel.productosAgregando.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { detalles ->
                    addingAdapter.updateDetalles(detalles)
                },
                onFailure = {
                    Toast.makeText(context, "Error al cargar los productos añadidos", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}
