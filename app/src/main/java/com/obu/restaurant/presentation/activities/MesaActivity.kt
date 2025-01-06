package com.obu.restaurant.presentation.activities

import DatabaseService
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.obu.restaurant.R
import com.obu.restaurant.data.database.AtencionDao
import com.obu.restaurant.data.database.MesaDao
import com.obu.restaurant.data.models.Atencion
import com.obu.restaurant.data.models.Mesa
import com.obu.restaurant.data.repository.implementations.AtencionRepository
import com.obu.restaurant.data.repository.implementations.MesaRepository
import com.obu.restaurant.databinding.ActivityMesasBinding
import com.obu.restaurant.domain.usecases.GetActiveMesasUseCase
import com.obu.restaurant.domain.usecases.RegistrarAtencionUseCase
import com.obu.restaurant.presentation.adapters.MesaAdapter
import com.obu.restaurant.presentation.viewmodels.MesaViewModel
import com.obu.restaurant.presentation.viewmodels.MesaViewModelFactory
import com.obu.restaurant.utils.DialogUtils
import com.obu.restaurant.utils.SessionUtils

class MesaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMesasBinding
    private lateinit var mesaViewModel: MesaViewModel
    private lateinit var mesaAdapter: MesaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar el layout con View Binding
        binding = ActivityMesasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Obtén el tamaño de la pantalla
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        // Define el tamaño de cada columna en píxeles (por ejemplo, 150dp convertido a píxeles)
        val columnWidthPx = resources.getDimensionPixelSize(R.dimen.column_width)

        // Calcula cuántas columnas caben en la pantalla
        val spanCount = screenWidth / columnWidthPx

        // Configura el RecyclerView con el número calculado de columnas
        binding.tablesRecyclerView.layoutManager = GridLayoutManager(this, spanCount)
        mesaAdapter = MesaAdapter { mesa ->
            onMesaSelected(mesa)
        }
        binding.tablesRecyclerView.adapter = mesaAdapter

        // Crear dependencias manualmente
        val databaseService = DatabaseService()
        val mesaDao = MesaDao(databaseService)
        val atencionDao = AtencionDao(databaseService)
        val mesaRepository = MesaRepository(mesaDao)
        val atencionRepository = AtencionRepository(atencionDao)
        val getActiveMesasUseCase = GetActiveMesasUseCase(mesaRepository)
        val registrarAtencionUseCase = RegistrarAtencionUseCase(atencionRepository)

        // Crear el ViewModel usando la Factory
        mesaViewModel = ViewModelProvider(
            this,
            MesaViewModelFactory(getActiveMesasUseCase, registrarAtencionUseCase) // Pasar ambos casos de uso
        ).get(MesaViewModel::class.java)

        // Observar los cambios en las mesas activas
        mesaViewModel.mesas.observe(this) { result ->
            // Manejar éxito o error usando el fold del Result
            result?.fold(
                onSuccess = { mesas ->
                    Log.d("vistaMesa", "Número de mesas observadas: ${mesas.size}")
                    mesaAdapter.submitList(mesas)
                    binding.swipeRefreshLayout.isRefreshing = false
                },
                onFailure = { error ->
                    val errorMessage = error.message ?: "Error inesperado al obtener las mesas"
                    Log.e("vistaMesa", "Error al obtener las mesas: ${errorMessage}")
                    DialogUtils.showDialog(
                        context = this,
                        message = error.message ?: "Error inesperado al obtener las mesas",
                        dialogType = DialogUtils.DialogType.ERROR
                    )
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            )
        }

        // Configurar el Swipe to Refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            // Llamar al método para cargar de nuevo las mesas
            mesaViewModel.fetchMesasConEstado()
        }

        // Cargar las mesas activas
        mesaViewModel.fetchMesasConEstado()
    }

    // Método para manejar el clic en una mesa
    private fun onMesaSelected(mesa: Mesa) {
        if (mesa.indEst == "L") {
            // Mesa libre: pedir la cantidad de clientes
            showClientCountDialog(mesa)
        } else {
            // Mesa ocupada: navegar directamente a AtencionActivity
            val intent = Intent(this, AtencionActivity::class.java).apply {
                putExtra("mesa_id", mesa.id)
                putExtra("mesa_nombre", mesa.desNom)
            }
            startActivity(intent)
        }
    }

    // Función para mostrar un diálogo y pedir la cantidad de clientes usando DialogUtils
    private fun showClientCountDialog(mesa: Mesa) {
        // Usamos DialogUtils para mostrar un diálogo de confirmación
        DialogUtils.showDialog(
            context = this,
            message = "Ingresa la cantidad de clientes para la mesa ${mesa.desNom}",
            dialogType = DialogUtils.DialogType.CONFIRMATION,
            onConfirm = {
                val editText = EditText(this)
                editText.inputType = InputType.TYPE_CLASS_NUMBER

                // Mostrar el diálogo de cantidad de clientes
                AlertDialog.Builder(this)
                    .setTitle("Cantidad de Clientes")
                    .setView(editText)
                    .setPositiveButton("Aceptar") { _, _ ->
                        val clientCount = editText.text.toString().toIntOrNull()
                        if (clientCount != null && clientCount > 0) {
                            // Registrar la nueva atención en la base de datos
                            registrarNuevaAtencion(mesa, clientCount)
                        } else {
                            Toast.makeText(this, "Ingresa un número válido de clientes", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )
    }

    // Función para registrar la atención en la base de datos
    private fun registrarNuevaAtencion(mesa: Mesa, clientCount: Int) {
        // Crear el objeto Atencion sin los campos de fechas
        val atencion = Atencion(
            id = 0,  // ID autogenerado, no lo necesitamos al crear
            nom_mesa = mesa.desNom,
            cnt_cliente = clientCount,
            val_total = 0.0,  // Valor total inicial es 0
            met_pago = '1',  // Método de pago por defecto (efectivo)
            user_reg = SessionUtils.getLoggedInUser(this)?: "desconocido",  // Usuario actual
            fec_reg="",
            fec_mod ="",
            user_mod ="",
            ind_est = "0"  // Estado 'iniciada'
        )

        // Llamar al ViewModel para registrar la atención en la base de datos
        mesaViewModel.registrarAtencion(atencion).observe(this) { result ->
            result.fold(
                onSuccess = { atencionCreada ->
                    // La atención creada devuelve el ID generado por la base de datos
                    val intent = Intent(this, AtencionActivity::class.java).apply {
                        putExtra("mesa_id", atencionCreada.id)  // Pasar el ID generado
                        putExtra("mesa_nombre", atencionCreada.nom_mesa)
                    }
                    startActivity(intent)
                },
                onFailure = { error ->
                    Toast.makeText(this, "Error al registrar la atención: ${error.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }


    // Crear el menú de opciones (opcional)
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)  // Asegúrate de que el archivo `main_menu.xml` está creado en `res/menu/`
        return true
    }

    // Manejar la selección de opciones del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Método para mostrar el diálogo de confirmación
    private fun showLogoutConfirmationDialog() {
        DialogUtils.showDialog(
            context = this,
            message = "¿Estás seguro de que deseas cerrar sesión?",
            dialogType = DialogUtils.DialogType.CONFIRMATION,
            onConfirm = {
                // Acción si el usuario confirma
                logout()
            },
            onCancel = {
                // Acción si el usuario cancela
            }
        )
    }

    // Método para cerrar sesión
    private fun logout() {
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()  // Limpiar los datos de sesión
        editor.apply()

        // Redirigir al login
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()  // Finalizar la actividad actual
    }

}