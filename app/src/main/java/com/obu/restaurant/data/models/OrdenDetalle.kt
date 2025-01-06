package com.obu.restaurant.data.models

data class OrdenDetalle(
    val id: Int,
    val id_atencion: Int,
    val producto: Int,
    val des_producto: String,
    val cat_producto: String,
    val cnt_produc: Int,
    val val_produc: Double,
    val nota_interna: String?,
    val fec_reg: String,
    val ind_est: String
)

