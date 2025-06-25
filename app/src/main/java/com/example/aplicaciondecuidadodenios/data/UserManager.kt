package com.example.aplicaciondecuidadodenios.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey // ¡IMPORTANTE: Nueva importación!
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Almacena DataStore en el contexto de la aplicación, haciéndolo un Singleton virtual
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserManager(context: Context) {

    private val dataStore = context.dataStore

    // Claves para DataStore
    private object PreferencesKeys {
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ID = stringPreferencesKey("user_id") // ¡NUEVA CLAVE!
    }

    // --- Métodos para la primera apertura ---

    // Obtener el estado de si es la primera apertura
    val isFirstLaunch: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_FIRST_LAUNCH] ?: true // Por defecto es true si no existe
        }

    // Marcar que la app ya ha sido abierta por primera vez
    suspend fun setFirstLaunchDone() {
        Log.d("UserManagerDebug", "¡setFirstLaunchDone() se ha llamado!")
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_FIRST_LAUNCH] = false
        }
    }

    // --- Métodos para el estado de sesión ---

    // Obtener el estado de si el usuario está logueado
    val isLoggedIn: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] ?: false // Por defecto es false si no existe
        }

    // Obtener el ID del usuario logueado (¡NUEVA PROPIEDAD!)
    val userId: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USER_ID] // Puede ser nulo si no hay ID guardado
        }

    // Marcar que el usuario ha iniciado sesión Y guardar su ID (¡MÉTODO MODIFICADO!)
    suspend fun login(id: String) { // Ahora recibe el ID como parámetro
        Log.d("UserManagerDebug", "login() se ha llamado! ID: $id")
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = true
            preferences[PreferencesKeys.USER_ID] = id // Guarda el ID del usuario
        }
    }

    // Marcar que el usuario ha cerrado sesión (¡MÉTODO MODIFICADO!)
    suspend fun logout() {
        Log.d("UserManagerDebug", "logout() se ha llamado!")
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = false
            preferences.remove(PreferencesKeys.USER_ID) // Limpiar el ID también al cerrar sesión
        }
    }

    // Puedes añadir una función específica para guardar el ID si el 'login' ya lo hace.
    // O si en algún otro punto de tu app necesitas solo guardar el ID sin cambiar el estado de login.
    // Si 'login' siempre guarda el ID, este no es estrictamente necesario, pero es bueno tener la opción.
    suspend fun saveUserId(id: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = id
        }
    }
}