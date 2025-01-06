package com.obu.restaurant.data.models

data class Producto(
    val id: Int,
    val id_categoria: Int,
    val des_nom: String,
    val val_unitario: Double,
    val ind_est: Char
)