package com.obu.restaurant.data.repository.implementations

import DatabaseService
import android.util.Log
import com.obu.restaurant.data.models.Producto
import com.obu.restaurant.data.repository.interfaces.IProductoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.SQLException

class ProductoRepository(private val databaseService: DatabaseService) : IProductoRepository {

    override suspend fun obtenerProductosPorCategoria(idCategoria: Int): List<Producto> = withContext(Dispatchers.IO) {
        val productos = mutableListOf<Producto>()
        var connection: Connection? = null

        try {
            // Obtener la conexión
            connection = databaseService.getConnection()
            if (connection == null) {
                Log.e("ProductoRepository", "No se pudo establecer la conexión a la base de datos.")
                return@withContext emptyList<Producto>()
            }

            // Consulta SQL
            val query = """
                SELECT id, id_categoria, des_nom, val_unitario, ind_est 
                FROM producto 
                WHERE id_categoria = ? AND ind_est = '1'
            """
            val statement = connection.prepareStatement(query)
            statement.setInt(1, idCategoria)
            val resultSet = statement.executeQuery()

            // Procesar los resultados
            while (resultSet.next()) {
                val producto = Producto(
                    id = resultSet.getInt("id"),
                    id_categoria = resultSet.getInt("id_categoria"),
                    des_nom = resultSet.getString("des_nom"),
                    val_unitario = resultSet.getDouble("val_unitario"),
                    ind_est = resultSet.getString("ind_est").first()
                )
                productos.add(producto)
            }

            resultSet.close()
            statement.close()

        } catch (e: SQLException) {
            Log.e("ProductoRepository", "Error al ejecutar la consulta SQL: ${e.message}")
        } catch (e: Exception) {
            Log.e("ProductoRepository", "Error inesperado: ${e.message}")
        } finally {
            connection?.close()
        }

        return@withContext productos
    }
}