package com.obu.restaurant.data.database

import DatabaseService
import android.util.Log
import com.obu.restaurant.data.models.Mesa
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.SQLException

class MesaDao(private val databaseService: DatabaseService) {

    // Método para obtener las mesas ocupadas con información adicional
    suspend fun getMesasConEstado(): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            val mesasList = mutableListOf<Mesa>()
            var connection: Connection? = null

            try {
                // Intentar obtener una conexión
                connection = databaseService.getConnection()
                if (connection == null) {
                    Log.e("bd-MesaDao", "No se pudo establecer la conexión a la base de datos.")
                    return@withContext mapOf(
                        "status" to false,
                        "message" to "Error al conectar con la base de datos. Por favor, intente más tarde."
                    )
                }

                Log.d("bd-MesaDao", "Conexión a la base de datos establecida")

                val query = """
                    SELECT atencion.id,atencion.nom_mesa,atencion.cnt_cliente,atencion.ind_est AS estado,SUM(orden_detalle.cnt_produc * orden_detalle.val_produc) AS total_atencion,
                    TIMESTAMPDIFF(MINUTE, atencion.fec_reg, NOW()) AS tiempo_atencion, atencion.user_reg AS usuario_atencion,atencion.fec_reg,
                    atencion.fec_mod,atencion.user_mod
                    FROM atencion
                    LEFT JOIN orden_detalle ON atencion.id = orden_detalle.id_atencion 
                    WHERE atencion.ind_est = '0'
                    GROUP BY atencion.id
                    UNION ALL
                    SELECT id, des_nom AS nom_mesa, NULL AS cnt_cliente,'L' AS estado,NULL AS total_atencion,0 AS tiempo_atencion,  
                    NULL AS usuario_atencion,NULL AS fec_reg, NULL AS fec_mod,NULL AS user_mod  
                    FROM mesas where des_nom not in (select nom_mesa from atencion where ind_est = '0');
                """
                Log.d("bd-MesaDao", "Ejecutando consulta SQL: $query")

                val statement = connection.prepareStatement(query)
                val resultSet = statement.executeQuery()

                // Verificar si hay resultados
                if (!resultSet.isBeforeFirst) {
                    Log.w("bd-MesaDao", "No se encontraron mesas activas")
                }

                while (resultSet.next()) {
                    val mesa = Mesa(
                        id = resultSet.getInt("id"),
                        desNom = resultSet.getString("nom_mesa") ?: "",
                        userReg = resultSet.getString("usuario_atencion") ?: "",
                        indEst = resultSet.getString("estado") ?: "L",  // Estado 'L' para mesas libres
                        cantidadClientes = resultSet.getInt("cnt_cliente") ?: 0,  // Cantidad de clientes, null para libres
                        montoAcumulado = resultSet.getDouble("total_atencion") ?: 0.0,  // Total acumulado, 0 para libres
                        tiempoAtencion = resultSet.getInt("tiempo_atencion") ?: 0,  // Tiempo transcurrido, 0 para libres
                        usuarioApertura = resultSet.getString("usuario_atencion") ?: "",
                        valorTotalAtencion = resultSet.getDouble("total_atencion") ?: 0.0  // Valor total, 0 para libres
                    )
                    mesasList.add(mesa)
                }

                resultSet.close()
                statement.close()

                Log.d("bd-MesaDao", "Total de mesas obtenidas: ${mesasList.size}")

                // Retornar el estado exitoso con la lista de mesas
                return@withContext mapOf(
                    "status" to true,
                    "mesas" to mesasList,
                    "message" to "Mesas obtenidas correctamente"
                )

            } catch (e: SQLException) {
                Log.e("bd-MesaDao", "Error al ejecutar la consulta SQL: ${e.message}")
                return@withContext mapOf(
                    "status" to false,
                    "message" to "Error al obtener los datos de las mesas: ${e.message}"
                )
            } catch (e: Exception) {
                Log.e("bd-MesaDao", "Ocurrió un error: ${e.message}")
                return@withContext mapOf(
                    "status" to false,
                    "message" to "Ocurrió un error inesperado: ${e.message}"
                )
            } finally {
                // Asegurarse de cerrar la conexión si fue establecida
                connection?.close()
            }
        }
    }
}
