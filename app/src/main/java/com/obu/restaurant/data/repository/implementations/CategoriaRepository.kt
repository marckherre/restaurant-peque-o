package com.obu.restaurant.data.repository.implementations

import DatabaseService
import android.util.Log
import com.obu.restaurant.data.models.Categoria
import com.obu.restaurant.data.repository.interfaces.ICategoriaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.SQLException

class CategoriaRepository(private val databaseService: DatabaseService) : ICategoriaRepository {

    override suspend fun obtenerCategorias(): List<Categoria> = withContext(Dispatchers.IO) {
        val categorias = mutableListOf<Categoria>()
        var connection: Connection? = null

        try {
            // Obtener la conexión
            connection = databaseService.getConnection()
            if (connection == null) {
                Log.e("CategoriaRepository", "No se pudo establecer la conexión a la base de datos.")
                return@withContext emptyList<Categoria>()
            }

            // Consulta SQL
            val query = "SELECT id, des_nom, ind_est FROM categoria WHERE ind_est = '1'"
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()

            // Procesar los resultados
            while (resultSet.next()) {
                val categoria = Categoria(
                    id = resultSet.getInt("id"),
                    des_nom = resultSet.getString("des_nom"),
                    ind_est = resultSet.getString("ind_est").first()
                )
                categorias.add(categoria)
            }

            resultSet.close()
            statement.close()

        } catch (e: SQLException) {
            Log.e("CategoriaRepository", "Error al ejecutar la consulta SQL: ${e.message}")
        } catch (e: Exception) {
            Log.e("CategoriaRepository", "Error inesperado: ${e.message}")
        } finally {
            connection?.close()
        }

        return@withContext categorias
    }
}