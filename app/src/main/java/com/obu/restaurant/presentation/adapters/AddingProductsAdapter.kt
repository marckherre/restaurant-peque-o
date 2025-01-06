package com.obu.restaurant.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.obu.restaurant.data.models.OrdenDetalle
import com.obu.restaurant.databinding.ItemOrdenDetalleBinding
import com.obu.restaurant.utils.DialogUtils
import java.text.NumberFormat
import java.util.Locale

class AddingProductsAdapter(private var detalles: List<OrdenDetalle>, private val onCancelarProducto: (Int) -> Unit) :
    RecyclerView.Adapter<AddingProductsAdapter.AddingViewHolder>() {

    // Mapa auxiliar para guardar las cantidades temporales
    private val cantidadesTemporales = mutableMapOf<Int, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddingViewHolder {
        val binding = ItemOrdenDetalleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddingViewHolder(binding, cantidadesTemporales, onCancelarProducto) // Pasar el callback aquí
    }

    override fun onBindViewHolder(holder: AddingViewHolder, position: Int) {
        val detalle = detalles[position]

        // Inicializar la cantidad temporal si no existe
        if (!cantidadesTemporales.containsKey(detalle.producto)) {
            cantidadesTemporales[detalle.producto] = detalle.cnt_produc
        }

        // Pasar la cantidad temporal actual al ViewHolder
        holder.bind(detalle, cantidadesTemporales[detalle.producto] ?: detalle.cnt_produc)
    }

    override fun getItemCount(): Int = detalles.size

    // Método para actualizar la lista de detalles
    fun updateDetalles(nuevosDetalles: List<OrdenDetalle>) {
        detalles = nuevosDetalles
        notifyDataSetChanged()
    }

    // Método para obtener las cantidades actualizadas al confirmar
    fun obtenerCantidadesActualizadas(): Map<Int, Int> = cantidadesTemporales

    // Clase ViewHolder con referencia al mapa de cantidades
    class AddingViewHolder(
        private val binding: ItemOrdenDetalleBinding,
        private val cantidadesTemporales: MutableMap<Int, Int>,
        private val onCancelarProducto: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(detalle: OrdenDetalle, cantidadTemporal: Int) {
            // Seteamos el nombre del producto
            binding.textViewProducto.text = detalle.des_producto

            // Mostramos la cantidad temporal
            binding.textViewCantidad.text = cantidadTemporal.toString()

            // Calculamos y mostramos el precio total
            val precioTotal = cantidadTemporal * detalle.val_produc
            val formatter = NumberFormat.getNumberInstance(Locale("es", "PE"))
            formatter.minimumFractionDigits = 2
            formatter.maximumFractionDigits = 2
            binding.textViewPrecio.text = "S/. ${formatter.format(precioTotal)}"

            // Listener para aumentar la cantidad
            binding.buttonIncrease.setOnClickListener {
                val nuevaCantidad = cantidadTemporal + 1
                cantidadesTemporales[detalle.producto] = nuevaCantidad
                bind(detalle, nuevaCantidad) // Refresca los datos
            }

            // Listener para disminuir la cantidad
            binding.buttonDecrease.setOnClickListener {
                if (cantidadTemporal > 1) {
                    val nuevaCantidad = cantidadTemporal - 1
                    cantidadesTemporales[detalle.producto] = nuevaCantidad
                    bind(detalle, nuevaCantidad) // Refresca los datos
                } else {
                    DialogUtils.showDialog(
                        context = binding.root.context,
                        message = "¿Deseas eliminar este producto de la lista?",
                        dialogType = DialogUtils.DialogType.CONFIRMATION,
                        onConfirm = {
                            Log.d(
                                "ProductoEliminado",
                                "Producto a eliminar: ${detalle.des_producto}, id: ${detalle.id}"
                            )
                            onCancelarProducto(detalle.id) // Llama al callback con el ID del detalle
                        },
                        onCancel = {
                            // No hacemos nada si el usuario cancela
                            Log.d("ProductoEliminado", "Eliminación cancelada para: ${detalle.des_producto}")
                        }
                    )
                }
            }
        }
    }
}