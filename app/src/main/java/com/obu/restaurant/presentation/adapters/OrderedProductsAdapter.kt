package com.obu.restaurant.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.obu.restaurant.data.models.OrdenDetalle
import com.obu.restaurant.databinding.ItemOrdenDetalleBinding
import java.text.NumberFormat
import java.util.Locale

class OrderedProductsAdapter(private var detalles: List<OrdenDetalle>) :
    RecyclerView.Adapter<OrderedProductsAdapter.OrdenViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdenViewHolder {
        val binding = ItemOrdenDetalleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrdenViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrdenViewHolder, position: Int) {
        val detalle = detalles[position]
        holder.bind(detalle)
    }

    override fun getItemCount(): Int = detalles.size

    fun updateDetalles(nuevosDetalles: List<OrdenDetalle>) {
        detalles = nuevosDetalles
        notifyDataSetChanged()
    }

    class OrdenViewHolder(private val binding: ItemOrdenDetalleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(detalle: OrdenDetalle) {
            binding.textViewProducto.text = detalle.des_producto
            binding.textViewCantidad.text = detalle.cnt_produc.toString()

            // Formatear el precio total
            val precioTotal = detalle.cnt_produc * detalle.val_produc
            val formatter = NumberFormat.getNumberInstance(Locale("es", "PE"))
            formatter.minimumFractionDigits = 2
            formatter.maximumFractionDigits = 2
            binding.textViewPrecio.text = "S/. ${formatter.format(precioTotal)}"

            // Ocultar botones en la pestaña "Ordenados"
            binding.buttonIncrease.visibility = View.GONE
            binding.buttonDecrease.visibility = View.GONE
        }
    }
}
