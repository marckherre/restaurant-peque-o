package com.obu.restaurant.data.models

data class User(
    val id: Int,
    val user: String,
    val des_nom : String,
    val password : String,
    val rol: Int,
    val ind_est: String
    // Otros campos necesarios
)