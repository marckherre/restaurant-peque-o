package com.obu.restaurant.data.models

data class Atencion(
    val id: Int,                  // Identificador único de la atención
    val nom_mesa: String,          // Identificador de la mesa
    val cnt_cliente: Int,          // Cantidad de clientes
    val val_total: Double,         // Valor total de la atención
    val met_pago: Char,            // Método de pago (Efectivo '1', Tarjeta '2', Aplicativo '3')
    val user_reg: String,          // Usuario que realizó el registro
    val user_mod: String?,         // Usuario que realizó la última modificación (puede ser null, String)
    val fec_reg: String?,           //
    val fec_mod: String?,
    val ind_est: String            // Indicador de estado (iniciada '0', terminada '1', cancelada '2')
)