package com.obu.restaurant.data.database

import DatabaseService
import android.util.Log
import com.obu.restaurant.data.models.OrdenDetalle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.SQLException

class OrdenDetalleDao(private val databaseService: DatabaseService) {

    // Método para obtener los detalles de una orden por su id_atencion
    suspend fun getDetallesOrden(idAtencion: Int): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            val detallesList = mutableListOf<OrdenDetalle>()
            var connection: Connection? = null

            try {
                // Intentar obtener una conexión
                connection = databaseService.getConnection()
                if (connection == null) {
                    Log.e("bd-OrdenDetalleDao", "No se pudo establecer la conexión a la base de datos.")
                    return@withContext mapOf(
                        "status" to false,
                        "message" to "Error al conectar con la base de datos. Por favor, intente más tarde."
                    )
                }

                Log.d("bd-OrdenDetalleDao", "Conexión a la base de datos establecida")

                // Consulta para obtener los detalles de la orden
                val query = """
                     SELECT a.id, a.id_atencion, a.producto,(select b.des_nom from producto b where b.id =a.producto) as des_producto , a.cat_producto, a.cnt_produc, a.val_produc, a.nota_interna, a.fec_reg, a.ind_est
                    FROM orden_detalle a
                    WHERE id_atencion = ?
                """
                Log.d("bd-OrdenDetalleDao", "Ejecutando consulta SQL: $query")

                val statement = connection.prepareStatement(query)
                statement.setInt(1, idAtencion)
                val resultSet = statement.executeQuery()

                // Verificar si hay resultados
                if (!resultSet.isBeforeFirst) {
                    Log.w("bd-OrdenDetalleDao", "No se encontraron detalles para la atención $idAtencion")
                }

                while (resultSet.next()) {
                    val detalle = OrdenDetalle(
                        id = resultSet.getInt("id"),
                        id_atencion = resultSet.getInt("id_atencion"),
                        producto = resultSet.getInt("producto"),
                        des_producto = resultSet.getString("des_producto"),
                        cat_producto = resultSet.getString("cat_producto") ?: "",
                        cnt_produc = resultSet.getInt("cnt_produc"),
                        val_produc = resultSet.getDouble("val_produc"),
                        nota_interna = resultSet.getString("nota_interna") ?: "",
                        fec_reg = resultSet.getString("fec_reg") ?: "",
                        ind_est = resultSet.getString("ind_est") ?: ""
                    )
                    detallesList.add(detalle)
                }

                resultSet.close()
                statement.close()

                Log.d("bd-OrdenDetalleDao", "Total de detalles obtenidos: ${detallesList.size}")

                // Retornar el estado exitoso con la lista de detalles
                return@withContext mapOf(
                    "status" to true,
                    "detalles" to detallesList,
                    "message" to "Detalles obtenidos correctamente"
                )

            } catch (e: SQLException) {
                Log.e("bd-OrdenDetalleDao", "Error al ejecutar la consulta SQL: ${e.message}")
                return@withContext mapOf(
                    "status" to false,
                    "message" to "Error al obtener los detalles de la orden: ${e.message}"
                )
            } catch (e: Exception) {
                Log.e("bd-OrdenDetalleDao", "Ocurrió un error: ${e.message}")
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

    // Método para cancelar un detalle de orden
    suspend fun cancelarDetalle(idDetalle: Int): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            var connection: Connection? = null

            try {
                // Obtener conexión a la base de datos
                connection = databaseService.getConnection()
                if (connection == null) {
                    Log.e("bd-OrdenDetalleDao", "No se pudo establecer la conexión a la base de datos.")
                    return@withContext mapOf(
                        "status" to false,
                        "message" to "Error al conectar con la base de datos. Por favor, intente más tarde."
                    )
                }

                Log.d("bd-OrdenDetalleDao", "Conexión a la base de datos establecida para cancelar detalle")

                // Consulta para actualizar el estado a '4' (cancelado)
                val query = """
                    UPDATE orden_detalle
                    SET ind_est = '4'
                    WHERE id = ?
                """
                val statement = connection.prepareStatement(query)
                statement.setInt(1, idDetalle)

                // Ejecutar la actualización
                val rowsUpdated = statement.executeUpdate()
                statement.close()

                Log.d("bd-OrdenDetalleDao", "Filas actualizadas: $rowsUpdated")

                if (rowsUpdated > 0) {
                    return@withContext mapOf(
                        "status" to true,
                        "message" to "Detalle cancelado correctamente"
                    )
                } else {
                    return@withContext mapOf(
                        "status" to false,
                        "message" to "No se encontró el detalle con ID $idDetalle"
                    )
                }

            } catch (e: SQLException) {
                Log.e("bd-OrdenDetalleDao", "Error al cancelar detalle: ${e.message}")
                return@withContext mapOf(
                    "status" to false,
                    "message" to "Error al cancelar el detalle: ${e.message}"
                )
            } finally {
                connection?.close()
            }
        }
    }
}