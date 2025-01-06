package com.obu.restaurant.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.obu.restaurant.R
import com.obu.restaurant.data.models.Mesa
import com.obu.restaurant.databinding.ItemMesaBinding

class MesaAdapter (private val onItemClick: (Mesa) -> Unit): RecyclerView.Adapter<MesaAdapter.MesaViewHolder>() {

    private var mesasList: List<Mesa> = listOf()

    fun submitList(mesas: List<Mesa>) {
        mesasList = mesas
        Log.d("MesaAdapter", "Número de mesas en el adapter: ${mesasList.size}")  // Log para ver cuántas mesas se envían al Adapter
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesaViewHolder {
        val binding = ItemMesaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MesaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MesaViewHolder, position: Int) {
        val mesa = mesasList[position]
        Log.d("MesaAdapter", "Pintando mesa: ${mesa.desNom}")  // Log para ver el nombre de la mesa en cada posición
        holder.bind(mesa)
        // Configurar el click listener para cada mesa
        holder.itemView.setOnClickListener {
            onItemClick(mesa)  // Llamar la función lambda cuando se haga clic
        }
    }

    override fun getItemCount(): Int = mesasList.size

    inner class MesaViewHolder(private val binding: ItemMesaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mesa: Mesa) {
            // Mostrar solo el nombre si la mesa está libre ('L')
            if (mesa.indEst == "L") {
                binding.tableName.text = "Mesa ${mesa.desNom}"
                binding.userName.text = "" // Vacío
                binding.clientes.text = ""  // Vacío
                binding.time.text = "00:00"  // Vacío
                binding.total.text = ""  // Vacío
                binding.status.text = "Libre"

                val grayTextColor = binding.root.context.getColor(R.color.gray_medium)
                binding.tableName.setTextColor(grayTextColor)
                binding.status.setTextColor(grayTextColor)
                binding.status.setBackgroundResource(R.drawable.gradient_background_free)
                binding.time.setTextColor(0x00000000)  // Totalmente transparente
                binding.userIcon.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.gray_medium))
                binding.clockIcon.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.gray_medium))
                binding.clientsIcon.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.gray_medium))
                binding.root.setBackgroundResource(R.drawable.gradient_background_free)
                binding.root.elevation = 2f // Aplicar sombra ligera (elevation)
            } else {
                // Si la mesa está ocupada, mostrar toda la información
                binding.tableName.text = "Mesa ${mesa.desNom}"
                // Convertir el tiempo de atención a horas y minutos
                val horas = mesa.tiempoAtencion?.div(60) ?: 0  // Obtener las horas
                val minutos = mesa.tiempoAtencion?.rem(60) ?: 0  // Obtener los minutos restantes
                // Formatear a dos dígitos
                val formattedTime = String.format("%02d:%02d", horas, minutos)

                val formattedMonto = String.format("%.2f", mesa.montoAcumulado)

                binding.userName.text =  mesa.userReg // Vacío
                binding.clientes.text = "clientes: ${mesa.cantidadClientes}"  // Vacío
                binding.time.text = formattedTime  // Vacío
                binding.total.text = "S/${formattedMonto}"  // Vacío

                binding.root.setBackgroundResource(R.drawable.mesa_activa)
                binding.status.text = "Activa"
                binding.status.setTextColor(binding.root.context.getColor(android.R.color.white)) // Texto blanco
                binding.status.setBackgroundResource(R.drawable.rounded_background) // Fondo redondeado
            }
        }
    }

}
