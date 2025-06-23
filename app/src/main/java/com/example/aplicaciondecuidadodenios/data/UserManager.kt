package com.example.aplicaciondecuidadodenios.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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
        // Podrías añadir más claves aquí, como el ID del usuario, etc.
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

    // Marcar que el usuario ha iniciado sesión
    suspend fun login() {
        Log.d("UserManagerDebug", "login() se ha llamado!")
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = true
        }
    }

    // Marcar que el usuario ha cerrado sesión
    suspend fun logout() {
        Log.d("UserManagerDebug", "logout() se ha llamado!")
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = false
            // Opcional: borrar cualquier otro dato de sesión si lo hubieras guardado
        }
    }
}