/**
 * =====================================================================
 * File: PhoneNumberUtils.kt
 * Tujuan: Menyediakan utilitas validasi, pemformatan nomor WhatsApp, dan ekstraksi regex OCR
 * Dipakai oleh: MainViewModel, HistoryRepository
 * Dependensi Utama: Regex Java/Kotlin
 * Daftar Fungsi: formatForWhatsApp, isValidIndonesianNumber, extractPhoneNumbers
 * Side Effect: None (Pure Utility Functions)
 * =====================================================================
 */
package com.heytayo.whatsappin.util

/**
 * Utility functions for phone number formatting and validation.
 */
object PhoneNumberUtils {

    /**
     * Cleans and formats a phone number for WhatsApp:
     * - Removes all non-digit characters (spaces, dashes, parentheses)
     * - Converts leading "0" to "62" (Indonesia country code)
     * - Removes leading "+" if present
     */
    fun formatForWhatsApp(input: String): String {
        // Remove all non-digit characters except leading +
        val cleaned = input.trim()
        val digitsOnly = cleaned.replace(Regex("[^0-9+]"), "")

        // Remove leading + for final format
        val withoutPlus = digitsOnly.removePrefix("+")

        // Convert leading 0 to 62
        return if (withoutPlus.startsWith("0")) {
            "62${withoutPlus.substring(1)}"
        } else {
            withoutPlus
        }
    }

    /**
     * Validates if the formatted number looks like a valid Indonesian phone number.
     * Must start with 62 and have 10-14 digits total.
     */
    fun isValidIndonesianNumber(formattedNumber: String): Boolean {
        return formattedNumber.matches(Regex("^62\\d{8,12}$"))
    }

    /**
     * Extracts phone numbers from OCR text using regex patterns
     * for Indonesian phone numbers.
     */
    fun extractPhoneNumbers(text: String): List<String> {
        // Patterns for Indonesian phone numbers
        val patterns = listOf(
            Regex("\\+62[\\s\\-]?\\d[\\d\\s\\-]{7,13}"),   // +62xxx
            Regex("62[\\s\\-]?\\d[\\d\\s\\-]{7,13}"),       // 62xxx
            Regex("0[\\s\\-]?8[\\d\\s\\-]{7,12}"),          // 08xxx
            Regex("0[\\s\\-]?[2-9][\\d\\s\\-]{6,11}")       // 0[2-9]xxx (landline)
        )

        val results = mutableSetOf<String>()

        for (pattern in patterns) {
            val matches = pattern.findAll(text)
            for (match in matches) {
                val cleaned = match.value.replace(Regex("[^0-9+]"), "")
                if (cleaned.length in 9..15) {
                    results.add(cleaned)
                }
            }
        }

        return results.toList()
    }
}
