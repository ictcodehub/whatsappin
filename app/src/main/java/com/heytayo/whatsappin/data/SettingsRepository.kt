/**
 * =====================================================================
 * File: SettingsRepository.kt
 * Tujuan: Mengelola penyimpanan persisten pengaturan aplikasi (bahasa, warna tema, mode, template kustom, haptic) menggunakan Jetpack DataStore
 * Dipakai oleh: MainViewModel
 * Dependensi Utama: Jetpack DataStore Preferences
 * Daftar Fungsi: val settings, suspend fun updateLanguage, suspend fun updateThemeColor, suspend fun updateThemeMode, suspend fun addTemplate, suspend fun removeTemplate, suspend fun setHapticEnabled
 * Side Effect: I/O (membaca & menulis berkas dataStore preferences lokal)
 * =====================================================================
 */
package com.heytayo.whatsappin.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "whatsappin_settings")

data class AppSettings(
    val language: String,
    val themeColor: String,
    val themeMode: String,
    val templates: List<String>,
    val hapticEnabled: Boolean
)

class SettingsRepository(private val context: Context) {

    companion object {
        private val KEY_LANGUAGE = stringPreferencesKey("app_language")
        private val KEY_THEME_COLOR = stringPreferencesKey("app_theme_color")
        private val KEY_THEME_MODE = stringPreferencesKey("app_theme_mode")
        private val KEY_TEMPLATES = stringPreferencesKey("app_custom_templates")
        private val KEY_HAPTIC = booleanPreferencesKey("app_haptic_feedback")
        
        private const val SEPARATOR = "|"
        private const val DEFAULT_TEMPLATES = "Halo, permisi...|Boleh minta infonya?|Saya tertarik dengan produk Anda.|P"
    }

    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { prefs ->
        val rawTemplates = prefs[KEY_TEMPLATES] ?: DEFAULT_TEMPLATES
        val templatesList = if (rawTemplates.isEmpty()) emptyList() else rawTemplates.split(SEPARATOR)
        
        AppSettings(
            language = prefs[KEY_LANGUAGE] ?: "id",
            themeColor = prefs[KEY_THEME_COLOR] ?: "#008069",
            themeMode = prefs[KEY_THEME_MODE] ?: "System",
            templates = templatesList,
            hapticEnabled = prefs[KEY_HAPTIC] ?: true
        )
    }

    suspend fun updateLanguage(language: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = language
        }
    }

    suspend fun updateThemeColor(color: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[KEY_THEME_COLOR] = color
        }
    }

    suspend fun updateThemeMode(mode: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[KEY_THEME_MODE] = mode
        }
    }

    suspend fun addTemplate(template: String) {
        if (template.isEmpty() || template.contains(SEPARATOR)) return
        context.settingsDataStore.edit { prefs ->
            val raw = prefs[KEY_TEMPLATES] ?: DEFAULT_TEMPLATES
            val current = if (raw.isEmpty()) mutableListOf() else raw.split(SEPARATOR).toMutableList()
            if (!current.contains(template)) {
                current.add(template)
                prefs[KEY_TEMPLATES] = current.joinToString(SEPARATOR)
            }
        }
    }

    suspend fun removeTemplate(template: String) {
        context.settingsDataStore.edit { prefs ->
            val raw = prefs[KEY_TEMPLATES] ?: DEFAULT_TEMPLATES
            val current = raw.split(SEPARATOR).toMutableList()
            current.remove(template)
            prefs[KEY_TEMPLATES] = current.joinToString(SEPARATOR)
        }
    }

    suspend fun setHapticEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[KEY_HAPTIC] = enabled
        }
    }
}
