package com.obu.restaurant.utils

import android.content.Context
import android.content.SharedPreferences

object SessionUtils {

    private const val PREFS_NAME = "LoginPrefs"
    private const val USER_ID_KEY = "userId"

    // Función para obtener el ID del usuario que está actualmente logueado
    fun getLoggedInUser(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(USER_ID_KEY, null)  // Devuelve null si no está almacenado
    }

    // Función para almacenar el ID del usuario cuando inicie sesión
    fun saveLoggedInUser(context: Context, userId: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(USER_ID_KEY, userId)  // Almacena el ID del usuario
        editor.apply()
    }

    // Función para borrar el ID del usuario cuando cierre sesión
    fun clearSession(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(USER_ID_KEY)  // Elimina el ID del usuario
        editor.apply()
    }
}
