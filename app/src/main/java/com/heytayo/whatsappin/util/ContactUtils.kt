/**
 * =====================================================================
 * File: ContactUtils.kt
 * Tujuan: Menyediakan utilitas pemeriksaan izin kontak dan pencarian nama kontak dari sistem Android
 * Dipakai oleh: MainActivity, HistorySection
 * Dependensi Utama: Android ContactsContract, ContentResolver, Runtime Permission
 * Daftar Fungsi: hasContactsPermission, getContactName
 * Side Effect: Membaca database kontak internal sistem Android (I/O Read)
 * =====================================================================
 */
package com.heytayo.whatsappin.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import androidx.core.content.ContextCompat

object ContactUtils {

    /**
     * Memeriksa apakah aplikasi memiliki izin untuk membaca kontak.
     */
    fun hasContactsPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Mencari nama kontak berdasarkan nomor telepon.
     * Menggunakan query PhoneLookup dari ContentResolver.
     */
    fun getContactName(context: Context, phoneNumber: String): String? {
        if (!hasContactsPermission(context)) return null

        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

        return try {
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
                    if (columnIndex >= 0) {
                        cursor.getString(columnIndex)
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
