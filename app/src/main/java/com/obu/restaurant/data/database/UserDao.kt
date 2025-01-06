package com.obu.restaurant.data.database

import DatabaseService
import android.util.Log
import com.obu.restaurant.data.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.SQLException

class UserDao(private val databaseService: DatabaseService) {

    suspend fun authenticateUser(username: String, password: String): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            try {
                val connection = databaseService.getConnection()

                if (connection == null) {
                    Log.d("bd-login","Conexión exitosa a la base de datos.")
                    return@withContext mapOf(
                        "status" to false,
                        "message" to "No se pudo establecer una conexión con la base de datos."
                    )
                }

                connection.use { conn ->
                    val statement = conn.prepareStatement(
                        "SELECT * FROM usuarios WHERE user = ? AND password = ?"
                    )
                    statement.use { stmt ->
                        stmt.setString(1, username)
                        stmt.setString(2, password)
                        val resultSet = stmt.executeQuery()
                        resultSet.use { rs ->
                            if (rs.next()) {
                                // Usuario autenticado exitosamente
                                val user = User(
                                    id = rs.getInt("id"),
                                    user = rs.getString("user"),
                                    des_nom = rs.getString("des_nom"),
                                    rol = rs.getInt("rol"),
                                    ind_est = rs.getString("ind_est"),
                                    password = rs.getString("password")
                                )
                                Log.d("bd-login","Usuario autenticado correctamente.")
                                return@withContext mapOf(
                                    "status" to true,
                                    "message" to "Usuario autenticado correctamente.",
                                    "user" to user
                                )
                            } else {
                                // Usuario o contraseña incorrectos
                                Log.d("bd-login","Usuario o contraseña incorrectos.")
                                return@withContext mapOf(
                                    "status" to false,
                                    "message" to "Usuario o contraseña incorrectos."
                                )
                            }
                        }
                    }
                }
            } catch (e: SQLException) {
                Log.d("bd-login","Error al conectarse a la base de datos: ${e.message}")
                return@withContext mapOf(
                    "status" to false,
                    "message" to "Error al conectarse a la base de datos: ${e.message}"
                )
            } catch (e: Exception) {
                Log.d("bd-login","Ocurrió un error inesperado: ${e.message}")
                return@withContext mapOf(
                    "status" to false,
                    "message" to "Ocurrió un error inesperado: ${e.message}"
                )
            }
        }
    }
}

