package com.obu.restaurant.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.obu.restaurant.R

object DialogUtils {

    // Enum para definir los tipos de diálogos
    enum class DialogType {
        INFO,
        ERROR,
        CONFIRMATION
    }

    // Función para mostrar el diálogo
    fun showDialog(
        context: Context,
        message: String,
        dialogType: DialogType,
        onConfirm: (() -> Unit)? = null, // Opción de acción para botones de confirmación
        onCancel: (() -> Unit)? = null   // Opción de acción para botones de cancelar
    ) {
        val builder = AlertDialog.Builder(context)

        // Inflamos el layout personalizado para el título
        val inflater = LayoutInflater.from(context)
        val titleView: View = inflater.inflate(R.layout.dialog_title, null)
        val titleText = titleView.findViewById<TextView>(R.id.dialogTitleText)

        // Configurar el color y texto del título según el tipo de diálogo
        when (dialogType) {
            DialogType.INFO -> {
                titleView.setBackgroundResource(R.color.colorInfo)  // Azul para informativos
                titleText.text = "Información"
            }
            DialogType.ERROR -> {
                titleView.setBackgroundResource(R.color.colorError) // Rojo para errores
                titleText.text = "Error"
            }
            DialogType.CONFIRMATION -> {
                titleView.setBackgroundResource(R.color.colorConfirm) // Verde para confirmaciones
                titleText.text = "Confirmación"
            }
        }

        // Asignamos el título personalizado
        builder.setCustomTitle(titleView)
            .setMessage(message)

        // Configurar botones según el tipo de diálogo
        when (dialogType) {
            DialogType.INFO, DialogType.ERROR -> {
                builder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    onConfirm?.invoke()
                }
            }
            DialogType.CONFIRMATION -> {
                builder.setPositiveButton("Sí") { dialog, _ ->
                    dialog.dismiss()
                    onConfirm?.invoke()
                }
                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                    onCancel?.invoke()
                }
            }
        }

        // Mostrar el diálogo
        builder.create().show()
    }
}
