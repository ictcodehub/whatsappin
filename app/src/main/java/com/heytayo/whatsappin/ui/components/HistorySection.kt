/**
 * =====================================================================
 * File: HistorySection.kt
 * Tujuan: Merender riwayat nomor yang pernah dihubungi dalam bentuk daftar thread chat, terintegrasi penuh dengan lokalisasi bahasa
 * Dipakai oleh: MainActivity
 * Dependensi Utama: Material3 Compose, Icons, LanguageMapper
 * Daftar Fungsi: HistorySection, ChatThreadItem
 * Side Effect: None (Pure Composable)
 * =====================================================================
 */
package com.heytayo.whatsappin.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.heytayo.whatsappin.util.ContactUtils
import com.heytayo.whatsappin.util.LanguageMapper
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HistorySection(
    recentNumbers: List<String>,
    language: String,
    onNumberSelected: (String) -> Unit,
    onRemoveNumber: (String) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(visible = recentNumbers.isNotEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header Riwayat
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = LanguageMapper.getString("history_title", language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    TextButton(onClick = onClearHistory) {
                        Icon(
                            Icons.Default.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = LanguageMapper.getString("history_clear", language), 
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // List of Chat Items
                recentNumbers.forEachIndexed { index, number ->
                    ChatThreadItem(
                        number = number,
                        language = language,
                        onItemClick = { onNumberSelected(number) },
                        onRemoveClick = { onRemoveNumber(number) }
                    )

                    // Tambahkan garis pemisah di antara item chat
                    if (index < recentNumbers.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 56.dp, top = 4.dp, bottom = 4.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatThreadItem(
    number: String,
    language: String,
    onItemClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val displayName = remember(number) {
        ContactUtils.getContactName(context, number)
    }

    // Generate warna avatar yang menyenangkan secara konsisten
    val avatarColors = remember {
        listOf(
            Color(0xFFE57373), Color(0xFFF06292), Color(0xFFBA68C8),
            Color(0xFF9575CD), Color(0xFF7986CB), Color(0xFF64B5F6),
            Color(0xFF4FC3F7), Color(0xFF4DB6AC), Color(0xFF81C784),
            Color(0xFFFFB74D), Color(0xFFD4E157), Color(0xFFAED581)
        )
    }
    val colorIndex = remember(number) {
        val lastDigit = number.lastOrNull()?.toString()?.toIntOrNull() ?: 0
        lastDigit % avatarColors.size
    }
    val avatarColor = avatarColors[colorIndex]
    
    val initial = remember(number, displayName) {
        if (displayName != null && displayName.isNotEmpty()) {
            displayName.take(1).uppercase()
        } else {
            if (number.length >= 3) number.substring(number.length - 2) else "WA"
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onItemClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Lingkaran Avatar WhatsApp
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(avatarColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Konten Informasi Nomor
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayName ?: "+$number",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = LanguageMapper.getString("history_item_active", language),
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (displayName != null) "+$number" else LanguageMapper.getString("history_item_desc", language),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Tombol hapus chat
        IconButton(
            onClick = onRemoveClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Hapus dari riwayat",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
