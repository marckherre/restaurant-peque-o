package com.obu.restaurant.data.database

import DatabaseService
import android.util.Log
import com.obu.restaurant.data.models.Atencion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement

class AtencionDao(private val databaseService: DatabaseService) {

    // Insertar una nueva atención en la base de datos
    suspend fun insertAtencion(atencion: Atencion): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            var connection: Connection? = null
            try {
                connection = databaseService.getConnection()

                if (connection == null) {
                    Log.e("bd-atencion", "No se pudo establecer una conexión con la base de datos.")
                    return@withContext mapOf(
                        "status" to false,
                        "message" to "Error al conectar con la base de datos. Por favor, intente más tarde."
                    )
                }

                Log.d("bd-atencion", "Conexión establecida con éxito a la base de datos.")

                connection.use { conn ->
                    val statement: PreparedStatement = conn.prepareStatement(
                        """
                        INSERT INTO atencion (nom_mesa, cnt_cliente, met_pago, user_reg, ind_est)
                        VALUES (?, ?, ?, ?, ?)
                        """,
                        Statement.RETURN_GENERATED_KEYS
                    )
                    statement.use { stmt ->
                        stmt.setString(1, atencion.nom_mesa)
                        stmt.setInt(2, atencion.cnt_cliente)
                        stmt.setString(3, atencion.met_pago.toString())
                        stmt.setString(4, atencion.user_reg)
                        stmt.setString(5, atencion.ind_est)

                        // Ejecutar la inserción
                        val rowsInserted = stmt.executeUpdate()

                        if (rowsInserted == 0) {
                            throw SQLException("No se pudo insertar la atención.")
                        }

                        // Obtener el ID autogenerado
                        val resultSet = stmt.generatedKeys
                        resultSet.use { rs ->
                            if (rs.next()) {
                                val generatedId = rs.getInt(1)
                                val atencionConId = atencion.copy(id = generatedId)

                                // Retornar éxito con la atención creada
                                Log.d("bd-atencion", "Atención registrada correctamente.")
                                return@withContext mapOf(
                                    "status" to true,
                                    "message" to "Atención registrada correctamente.",
                                    "atencion" to atencionConId
                                )
                            } else {
                                throw SQLException("No se pudo obtener el ID generado.")
                            }
                        }
                    }
                }
            } catch (e: SQLException) {
                Log.e("bd-atencion", "Error al registrar la atención: ${e.message}")
                return@withContext mapOf(
                    "status" to false,
                    "message" to "Error al registrar la atención: ${e.message}"
                )
            } finally {
                connection?.close()
            }
        }
    }

    // Actualizar una atención existente en la base de datos
    suspend fun updateAtencion(atencion: Atencion): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            var connection: Connection? = null
            try {
                connection = databaseService.getConnection()

                if (connection == null) {
                    Log.e("bd-atencion", "No se pudo establecer una conexión con la base de datos.")
                    return@withContext mapOf(
                        "status" to false,
                        "message" to "Error al conectar con la base de datos. Por favor, intente más tarde."
                    )
                }

                Log.d("bd-atencion", "Conexión establecida con éxito a la base de datos.")

                connection.use { conn ->
                    val statement: PreparedStatement = conn.prepareStatement(
                        """
                        UPDATE atencion 
                        SET cnt_cliente = ?, val_total = ?, met_pago = ?, user_mod = ?, ind_est = ? 
                        WHERE id = ?
                        """
                    )
                    statement.use { stmt ->
                        stmt.setInt(1, atencion.cnt_cliente)
                        stmt.setDouble(2, atencion.val_total)
                        stmt.setString(3, atencion.met_pago.toString())
                        stmt.setString(4, atencion.user_mod)
                        stmt.setString(5, atencion.ind_est)
                        stmt.setInt(6, atencion.id)

                        // Ejecutar la actualización
                        val rowsUpdated = stmt.executeUpdate()

                        if (rowsUpdated > 0) {
                            Log.d("bd-atencion", "Atención actualizada correctamente.")
                            return@withContext mapOf(
                                "status" to true,
                                "message" to "Atención actualizada correctamente."
                            )
                        } else {
                            throw SQLException("No se pudo actualizar la atención.")
                        }
                    }
                }
            } catch (e: SQLException) {
                Log.e("bd-atencion", "Error al actualizar la atención: ${e.message}")
                return@withContext mapOf(
                    "status" to false,
                    "message" to "Error al actualizar la atención: ${e.message}"
                )
            } finally {
                connection?.close()
            }
        }
    }

    // Obtener una atención por su ID
    suspend fun getAtencionById(id: Int): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            var connection: Connection? = null
            try {
                connection = databaseService.getConnection()

                if (connection == null) {
                    Log.e("bd-atencion", "No se pudo establecer una conexión con la base de datos.")
                    return@withContext mapOf(
                        "status" to false,
                        "message" to "Error al conectar con la base de datos. Por favor, intente más tarde."
                    )
                }

                Log.d("bd-atencion", "Conexión establecida con éxito a la base de datos.")

                connection.use { conn ->
                    val statement: PreparedStatement = conn.prepareStatement(
                        "SELECT * FROM atencion WHERE id = ?"
                    )
                    statement.use { stmt ->
                        stmt.setInt(1, id)
                        val resultSet = stmt.executeQuery()
                        resultSet.use { rs ->
                            if (rs.next()) {
                                val atencion = Atencion(
                                    id = rs.getInt("id"),
                                    nom_mesa = rs.getString("nom_mesa"),
                                    cnt_cliente = rs.getInt("cnt_cliente"),
                                    val_total = rs.getDouble("val_total"),
                                    met_pago = rs.getString("met_pago")[0],
                                    fec_reg = rs.getString("fec_reg"),
                                    user_reg = rs.getString("user_reg"),
                                    fec_mod = rs.getString("fec_mod"),
                                    user_mod = rs.getString("user_mod"),
                                    ind_est = rs.getString("ind_est")
                                )

                                Log.d("bd-atencion", "Atención obtenida correctamente.")
                                return@withContext mapOf(
                                    "status" to true,
                                    "message" to "Atención obtenida correctamente.",
                                    "atencion" to atencion
                                )
                            } else {
                                return@withContext mapOf(
                                    "status" to false,
                                    "message" to "Atención no encontrada."
                                )
                            }
                        }
                    }
                }
            } catch (e: SQLException) {
                Log.e("bd-atencion", "Error al obtener la atención: ${e.message}")
                return@withContext mapOf(
                    "status" to false,
                    "message" to "Error al obtener la atención: ${e.message}"
                )
            } finally {
                connection?.close()
            }
        }
    }
}
