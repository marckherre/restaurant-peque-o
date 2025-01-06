package com.obu.restaurant.presentation.activities

import DatabaseService
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.obu.restaurant.data.database.OrdenDetalleDao
import com.obu.restaurant.data.repository.implementations.OrdenDetalleRepository
import com.obu.restaurant.databinding.ActivityAtencionBinding
import com.obu.restaurant.domain.usecases.CancelarDetalleOrdenUseCase
import com.obu.restaurant.domain.usecases.ObtenerDetallesOrdenUseCase
import com.obu.restaurant.presentation.adapters.AtencionAdapter
import com.obu.restaurant.presentation.fragments.CategorySelectionFragment
import com.obu.restaurant.presentation.viewmodels.AtencionViewModel
import com.obu.restaurant.presentation.viewmodels.AtencionViewModelFactory

class AtencionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAtencionBinding
    private lateinit var atencionViewModel: AtencionViewModel
    private lateinit var atencionAdapter: AtencionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar el layout con ViewBinding
        binding = ActivityAtencionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar la barra de herramientas
        setSupportActionBar(binding.toolbar)

        // Configurar el ViewPager2 con el AtencionAdapter
        atencionAdapter = AtencionAdapter(this)
        binding.viewPager.adapter = atencionAdapter

        // Crear las dependencias necesarias
        val databaseService = DatabaseService()
        val ordenDetalleDao = OrdenDetalleDao(databaseService)
        val ordenDetalleRepository = OrdenDetalleRepository(ordenDetalleDao)
        val obtenerDetallesOrdenUseCase = ObtenerDetallesOrdenUseCase(ordenDetalleRepository)
        val cancelarDetalleOrdenUseCase = CancelarDetalleOrdenUseCase(ordenDetalleRepository)
        val factory = AtencionViewModelFactory(obtenerDetallesOrdenUseCase,cancelarDetalleOrdenUseCase)
        atencionViewModel = ViewModelProvider(this, factory).get(AtencionViewModel::class.java)

        // Obtener el ID de la atención de los extras del Intent
        val mesaId = intent.getIntExtra("mesa_id", 0)

        Log.d("bd-OrdenDetalleDao", "Ejecutando consulta SQL: $mesaId")

        // Configurar el ViewPager2 con el AtencionAdapter
        atencionAdapter = AtencionAdapter(this)
        binding.viewPager.adapter = atencionAdapter

        binding.viewPager.viewTreeObserver.addOnGlobalLayoutListener {
            Log.d("ViewPagerSize", "Ancho: ${binding.viewPager.width}, Alto: ${binding.viewPager.height}")
        }

        // Vincular el TabLayout con el ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Ordenados"   // Primera pestaña (productos ordenados)
                1 -> tab.text = "Agredados"   // Segunda pestaña (añadir productos)
            }
        }.attach()

// Observar los detalles de productos ordenados
        atencionViewModel.productosOrdenados.observe(this) { result ->
            result.fold(
                onSuccess = { detalles ->
                    // Aquí puedes hacer cualquier operación si necesitas usar estos datos directamente en la actividad
                },
                onFailure = { error ->
                    Toast.makeText(this, "Error al cargar los detalles: ${error.message}", Toast.LENGTH_LONG).show()
                }
            )
        }

        // Observar los detalles de productos agregando
        atencionViewModel.productosAgregando.observe(this) { result ->
            result.fold(
                onSuccess = { detalles ->
                    // Actualiza la UI para productos agregados
                },
                onFailure = { error ->
                    Toast.makeText(this, "Error al cargar los productos agregados: ${error.message}", Toast.LENGTH_LONG).show()
                }
            )
        }

        binding.btnAgregar.setOnClickListener {
            // Navegar al fragmento de selección de categorías
            supportFragmentManager.beginTransaction()
                .replace(binding.root.id, CategorySelectionFragment()) // Reemplaza el contenedor con el nuevo fragmento
                .addToBackStack(null) // Permite regresar a esta pantalla presionando atrás
                .commit()
        }


    }
}
