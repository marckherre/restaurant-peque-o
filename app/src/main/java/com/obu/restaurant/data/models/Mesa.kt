package com.obu.restaurant.data.models

data class Mesa(
    val id: Int,
    val desNom: String,   // Nombre de la mesa (des_nom en la BD)
    val userReg: String,  // Usuario que registró la mesa (user_reg en la BD)
    val indEst: String,     // Estado de la mesa: '1' (activa) o '0' (inactiva) (ind_est en la BD)

    // Nuevos campos relacionados con la atención
    val cantidadClientes: Int?,         // Número de clientes en la mesa (cnt_cliente)
    val montoAcumulado: Double?,        // Total acumulado de la atención (desde orden_detalle)
    val tiempoAtencion: Int?,           // Tiempo transcurrido desde que comenzó la atención
    val usuarioApertura: String?,       // Usuario que aperturó la atención
    val valorTotalAtencion: Double?     // Valor total de la atención (val_total desde atencion)
)

