package com.obu.restaurant.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.obu.restaurant.data.models.Producto
import com.obu.restaurant.databinding.ItemProductoBinding

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val productos = mutableListOf<Producto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val producto = productos[position]
        holder.bind(producto)
    }

    override fun getItemCount(): Int = productos.size

    fun updateProductos(nuevosProductos: List<Producto>) {
        productos.clear()
        productos.addAll(nuevosProductos)
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(private val binding: ItemProductoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(producto: Producto) {
            // Mostrar nombre y precio del producto
            binding.textViewProductoNombre.text = producto.des_nom
            binding.textViewProductoPrecio.text = "S/. ${producto.val_unitario}"

            // Mostrar cantidad actual
            binding.textViewCantidad.text = producto.cantidad.toString()

            // Actualizar precio total si cambia la cantidad
            fun actualizarPrecioTotal() {
                val precioTotal = producto.val_unitario * producto.cantidad
                val formatter = NumberFormat.getNumberInstance(Locale("es", "PE"))
                formatter.minimumFractionDigits = 2
                formatter.maximumFractionDigits = 2
                binding.textViewProductoPrecio.text = "S/. ${formatter.format(precioTotal)}"
            }

            // Listener para aumentar cantidad
            binding.buttonIncrease.setOnClickListener {
                producto.cantidad++
                binding.textViewCantidad.text = producto.cantidad.toString()
                actualizarPrecioTotal()
            }

            // Listener para disminuir cantidad
            binding.buttonDecrease.setOnClickListener {
                if (producto.cantidad > 0) {
                    producto.cantidad--
                    binding.textViewCantidad.text = producto.cantidad.toString()
                    actualizarPrecioTotal()
                }
            }
        }
    }
}