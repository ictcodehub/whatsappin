/**
 * =====================================================================
 * File: HistoryRepository.kt
 * Tujuan: Mengelola penyimpanan persisten riwayat kontak terakhir dengan Jetpack DataStore
 * Dipakai oleh: MainViewModel
 * Dependensi Utama: Jetpack DataStore Preferences
 * Daftar Fungsi: val recentNumbers, suspend fun addNumber, suspend fun removeNumber, suspend fun clearHistory
 * Side Effect: I/O (membaca dan menulis file lokal dataStore preferences)
 * =====================================================================
 */
package com.heytayo.whatsappin.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "wa_direct_history")

class HistoryRepository(private val context: Context) {

    companion object {
        private val HISTORY_KEY = stringPreferencesKey("recent_numbers")
        private const val MAX_HISTORY = 10
        private const val SEPARATOR = "|"
    }

    val recentNumbers: Flow<List<String>> = context.dataStore.data.map { prefs ->
        val raw = prefs[HISTORY_KEY] ?: ""
        if (raw.isEmpty()) emptyList() else raw.split(SEPARATOR)
    }

    suspend fun addNumber(number: String) {
        context.dataStore.edit { prefs ->
            val raw = prefs[HISTORY_KEY] ?: ""
            val current = if (raw.isEmpty()) mutableListOf() else raw.split(SEPARATOR).toMutableList()

            // Remove if already exists (to move it to top)
            current.remove(number)
            // Add to front
            current.add(0, number)
            // Limit to MAX_HISTORY
            val trimmed = current.take(MAX_HISTORY)

            prefs[HISTORY_KEY] = trimmed.joinToString(SEPARATOR)
        }
    }

    suspend fun removeNumber(number: String) {
        context.dataStore.edit { prefs ->
            val raw = prefs[HISTORY_KEY] ?: ""
            val current = raw.split(SEPARATOR).toMutableList()
            current.remove(number)
            prefs[HISTORY_KEY] = current.joinToString(SEPARATOR)
        }
    }

    suspend fun clearHistory() {
        context.dataStore.edit { prefs ->
            prefs.remove(HISTORY_KEY)
        }
    }
}
