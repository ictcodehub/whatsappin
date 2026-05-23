/**
 * =====================================================================
 * File: Theme.kt
 * Tujuan: Menyediakan skema warna dan tema Material 3 dinamis bertema WhatsApp asli yang membangun skema gelap/terang dari kode warna HEX bebas
 * Dipakai oleh: MainActivity dan seluruh UI aplikasi Jetpack Compose
 * Dependensi Utama: Material3 API, android.graphics.Color
 * Daftar Fungsi: WhatsappInTheme, getDynamicColorScheme
 * Side Effect: Menyesuaikan skema warna sistem secara dinamis mengikuti setelan HEX kustom pengguna
 * =====================================================================
 */
package com.heytayo.whatsappin.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Palet warna latar belakang & kontainer bawaan WhatsApp (Konsisten di semua aksen)
private val WA_Slate_Dark = Color(0xFF111B21)
private val WA_BG_Light = Color(0xFFF0F2F5)
private val WA_BG_Dark = Color(0xFF0B141A)

/**
 * Membangun ColorScheme secara dinamis dari kode warna HEX kustom
 */
fun getDynamicColorScheme(hexColor: String, isDark: Boolean): ColorScheme {
    val baseColor = try {
        Color(android.graphics.Color.parseColor(hexColor))
    } catch (e: Exception) {
        Color(0xFF008069) // Fallback default Teal jika gagal parsing
    }

    return if (isDark) {
        darkColorScheme(
            primary = baseColor,
            onPrimary = WA_BG_Dark,
            primaryContainer = baseColor.copy(alpha = 0.25f),
            onPrimaryContainer = Color(0xFFE9EDEF),
            secondary = baseColor,
            onSecondary = WA_BG_Dark,
            surface = WA_Slate_Dark,
            onSurface = Color(0xFFE9EDEF),
            background = WA_BG_Dark,
            onBackground = Color(0xFFE9EDEF),
            surfaceVariant = Color(0xFF202C33),
            onSurfaceVariant = Color(0xFF8696A0)
        )
    } else {
        lightColorScheme(
            primary = baseColor,
            onPrimary = Color.White,
            primaryContainer = baseColor.copy(alpha = 0.15f),
            onPrimaryContainer = Color(0xFF111B21),
            secondary = baseColor,
            onSecondary = Color.White,
            surface = Color.White,
            onSurface = Color(0xFF111B21),
            background = WA_BG_Light,
            onBackground = Color(0xFF111B21),
            surfaceVariant = Color(0xFFECE5DD),
            onSurfaceVariant = Color(0xFF667781)
        )
    }
}

@Composable
fun WhatsappInTheme(
    themeColor: String = "#008069",
    themeMode: String = "System",
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Tentukan mode gelap berdasarkan preferensi pengguna
    val useDarkTheme = when (themeMode) {
        "Light" -> false
        "Dark" -> true
        else -> darkTheme // "System"
    }

    // Periksa apakah warna berupa kode HEX
    val colorScheme = if (themeColor.startsWith("#")) {
        getDynamicColorScheme(themeColor, useDarkTheme)
    } else {
        // Fallback untuk warna berbasis nama lama agar tetap aman
        val hex = when (themeColor) {
            "Mint" -> "#00A884"
            "Purple" -> "#8A3FFC"
            "Blue" -> "#0F62FE"
            "Coral" -> "#FF6F61"
            else -> "#008069" // Teal
        }
        getDynamicColorScheme(hex, useDarkTheme)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
