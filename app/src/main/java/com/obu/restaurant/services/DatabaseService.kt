import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.Properties

class DatabaseService {

    //private val url = "jdbc:mysql://192.168.18.50:3306/restaurant_db"
    private val url = "jdbc:mysql://192.168.18.109:3306/restaurant_db"
    private val user = "root"
    private val password = "root"
    private val connectionTimeout = 5000 // Timeout de conexión en milisegundos (5 segundos)
    private val socketTimeout = 5000 // Timeout de socket (lectura) en milisegundos (5 segundos)
    private val maxRetries = 2 // Número máximo de intentos de reconexión
    private val retryDelay = 2000L // Tiempo de espera entre intentos (en milisegundos)

    // Función para obtener la conexión a la base de datos con manejo de errores
    fun getConnection(): Connection? {
        var connection: Connection? = null
        var attempts = 0
        // Configuración de las propiedades de conexión
        val properties = Properties().apply {
            put("user", user)
            put("password", password)
            put("connectTimeout", connectionTimeout.toString()) // Timeout de conexión
            put("socketTimeout", socketTimeout.toString()) // Timeout de lectura
        }

        // Reintentar la conexión si falla hasta 'maxRetries' veces
        while (attempts < maxRetries) {
            try {
                // Cargar el driver de MySQL
                Class.forName("com.mysql.jdbc.Driver")

                // Intentar establecer la conexión
                connection = DriverManager.getConnection(url,properties)
                Log.d("bd-Mysql","Conexión exitosa a la base de datos.")
                return connection // Retorna la conexión si es exitosa

            } catch (e: SQLException) {
                attempts++
                Log.d("bd-Mysql","Error al conectar con la base de datos: ${e.message}. Intento $attempts de $maxRetries.")
                if (attempts >= maxRetries) {
                    Log.d("bd-Mysql","Conexión fallida después de $attempts intentos. Por favor, intente más tarde.")
                    return null // Retorna null si no se pudo conectar después de los intentos
                }
                // Esperar antes de volver a intentar
                Thread.sleep(retryDelay)

            } catch (e: ClassNotFoundException) {
                Log.d("bd-Mysql","Driver de MySQL no encontrado: ${e.message}")
                return null // Si el driver no está, no tiene sentido seguir intentando
            }
        }

        return null // Retornar null si no se logró la conexión
    }
}
