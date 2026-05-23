/**
 * =====================================================================
 * File: SettingsScreen.kt
 * Tujuan: Menyediakan antarmuka halaman Pengaturan (Settings) halaman penuh (full-screen) dengan penataan urutan baru, kustomisasi palet warna horizontal 1 baris, & input HEX kustom
 * Dipakai oleh: MainActivity
 * Dependensi Utama: Material3 Compose, Icons, LanguageMapper, android.graphics.Color
 * Daftar Fungsi: SettingsScreen, ThemeColorHexCircle, SettingsCategoryHeader
 * Side Effect: Pemicuan getaran haptic perangkat, pengubahan preferensi lokal via viewmodel, navigasi balik ke halaman utama
 * =====================================================================
 */
package com.heytayo.whatsappin.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heytayo.whatsappin.util.LanguageMapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    language: String,
    themeColor: String,
    themeMode: String,
    templates: List<String>,
    hapticEnabled: Boolean,
    onLanguageChanged: (String) -> Unit,
    onThemeColorChanged: (String) -> Unit,
    onThemeModeChanged: (String) -> Unit,
    onAddTemplate: (String) -> Unit,
    onRemoveTemplate: (String) -> Unit,
    onToggleHaptic: (Boolean) -> Unit,
    onClearHistory: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var newTemplateText by remember { mutableStateOf("") }
    
    // Inisialisasi Hue Slider berdasarkan warna tema aktif
    val parsedHsv = remember(themeColor) {
        val hsv = FloatArray(3)
        try {
            android.graphics.Color.colorToHSV(android.graphics.Color.parseColor(themeColor), hsv)
        } catch (e: Exception) {
            hsv[0] = 180f // Fallback
        }
        hsv
    }
    
    var hueValue by remember(themeColor) { mutableFloatStateOf(parsedHsv[0]) }
    var customHexInput by remember(themeColor) { mutableStateOf(themeColor) }

    // Grid 12 Aksen Warna Favorit Terkurasi Premium
    val curatedColors = listOf(
        "#008069" to "Teal",
        "#00A884" to "Mint",
        "#10B981" to "Emerald",
        "#22C55E" to "Green",
        "#0F62FE" to "Blue",
        "#6366F1" to "Indigo",
        "#8A3FFC" to "Purple",
        "#EC4899" to "Pink",
        "#FF6F61" to "Coral",
        "#EF4444" to "Red",
        "#F97316" to "Orange",
        "#F59E0B" to "Amber"
    )

    fun triggerHaptic() {
        if (hapticEnabled) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = LanguageMapper.getString("settings_title", language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        triggerHaptic()
                        onBack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            
            // ==========================================================
            // --- 1. KATEGORI UMUM (BAHASA & TAMPILAN) ---
            // ==========================================================
            SettingsCategoryHeader(
                icon = Icons.Default.Language,
                title = LanguageMapper.getString("settings_tab_general", language)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Pilihan Bahasa
                    Text(
                        text = LanguageMapper.getString("settings_language_label", language),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    triggerHaptic()
                                    onLanguageChanged("id")
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = language == "id",
                                onClick = {
                                    triggerHaptic()
                                    onLanguageChanged("id")
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Text("Bahasa Indonesia", style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(modifier = Modifier.width(24.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    triggerHaptic()
                                    onLanguageChanged("en")
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = language == "en",
                                onClick = {
                                    triggerHaptic()
                                    onLanguageChanged("en")
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Text("English", style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Pilihan Mode Tema
                    Text(
                        text = LanguageMapper.getString("settings_theme_mode_label", language),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    val modes = listOf(
                        "System" to LanguageMapper.getString("settings_theme_mode_system", language),
                        "Light" to LanguageMapper.getString("settings_theme_mode_light", language),
                        "Dark" to LanguageMapper.getString("settings_theme_mode_dark", language)
                    )
                    
                    Column(modifier = Modifier.padding(top = 6.dp)) {
                        modes.forEach { (key, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        triggerHaptic()
                                        onThemeModeChanged(key)
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = themeMode == key,
                                    onClick = {
                                        triggerHaptic()
                                        onThemeModeChanged(key)
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                )
                                Text(label, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================================
            // --- 2. KATEGORI PESAN KUSTOM (REORDERED TO 2) ---
            // ==========================================================
            SettingsCategoryHeader(
                icon = Icons.AutoMirrored.Filled.Message,
                title = LanguageMapper.getString("settings_tab_presets", language)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Form Input Preset Baru
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newTemplateText,
                            onValueChange = { newTemplateText = it },
                            placeholder = { Text(LanguageMapper.getString("settings_preset_add_placeholder", language), fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (newTemplateText.trim().isNotEmpty()) {
                                    if (templates.contains(newTemplateText.trim())) {
                                        Toast.makeText(context, LanguageMapper.getString("settings_preset_duplicate", language), Toast.LENGTH_SHORT).show()
                                    } else {
                                        triggerHaptic()
                                        onAddTemplate(newTemplateText.trim())
                                        newTemplateText = ""
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(
                                Icons.Default.Add, 
                                contentDescription = "Tambah",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Daftar Preset
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (templates.isEmpty()) {
                            Text(
                                text = LanguageMapper.getString("settings_preset_empty", language),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(8.dp)
                            )
                        } else {
                            templates.forEachIndexed { index, template ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = template,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 2
                                    )
                                    IconButton(
                                        onClick = {
                                            triggerHaptic()
                                            onRemoveTemplate(template)
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Hapus",
                                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================================
            // --- 3. KATEGORI WARNA AKSEN KUSTOM (REORDERED TO 3 & SIMPLIFIED) ---
            // ==========================================================
            SettingsCategoryHeader(
                icon = Icons.Default.Palette,
                title = LanguageMapper.getString("settings_tab_theme", language)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = LanguageMapper.getString("settings_theme_color_label", language),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    // Lingkaran Aksen Warna Favorit: 1 Baris Bergulir Horisontal (SANGAT SIMPEL & RAPI)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        curatedColors.forEach { (hex, name) ->
                            ThemeColorHexCircle(
                                hexColor = hex,
                                activeColor = themeColor,
                                onClick = {
                                    triggerHaptic()
                                    onThemeColorChanged(hex)
                                }
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                    // RAINBOW HUE SLIDER (SPEKTRUM BEBAS)
                    Text(
                        text = if (language == "id") "Pemilih Spektrum Warna (Rainbow Slider):" else "Free Spectrum Picker (Rainbow Slider):",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    
                    // Box Spektrum Pelangi Horizontal
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red
                                    )
                                )
                            )
                    )
                    
                    // Slider untuk mengubah Hue
                    Slider(
                        value = hueValue,
                        onValueChange = { value ->
                            hueValue = value
                            val hsvColor = Color.hsv(hue = value, saturation = 0.85f, value = 0.85f)
                            val argb = hsvColor.toArgb()
                            val hexString = String.format("#%06X", 0xFFFFFF and argb)
                            onThemeColorChanged(hexString)
                        },
                        valueRange = 0f..360f,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = Color.Transparent,
                            inactiveTrackColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // INPUT KODE HEX KUSTOM
                    Text(
                        text = if (language == "id") "Input Kode HEX Warna kustom:" else "Direct HEX Color Input:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customHexInput,
                            onValueChange = { input ->
                                val filtered = input.uppercase().filter { it.isDigit() || it in 'A'..'F' || it == '#' }
                                customHexInput = if (filtered.startsWith("#")) filtered.take(7) else "#" + filtered.take(6)
                            },
                            placeholder = { Text("#008069") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = {
                                val previewColor = try {
                                    Color(android.graphics.Color.parseColor(customHexInput))
                                } catch (e: Exception) {
                                    MaterialTheme.colorScheme.primary
                                }
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(previewColor)
                                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                try {
                                    if (customHexInput.length == 7) {
                                        android.graphics.Color.parseColor(customHexInput)
                                        triggerHaptic()
                                        onThemeColorChanged(customHexInput)
                                        Toast.makeText(context, if (language == "id") "Warna aksen diterapkan!" else "Accent color applied!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, if (language == "id") "Format HEX tidak valid" else "Invalid HEX format", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, if (language == "id") "Gagal parsing HEX" else "Failed to parse HEX", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (language == "id") "Terapkan" else "Apply", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================================
            // --- 4. KATEGORI ALAT & INFO ---
            // ==========================================================
            SettingsCategoryHeader(
                icon = Icons.Default.Vibration,
                title = LanguageMapper.getString("settings_tab_tools", language)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Sakelar Haptic
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = LanguageMapper.getString("settings_haptic_label", language),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = LanguageMapper.getString("settings_haptic_desc", language),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = hapticEnabled,
                            onCheckedChange = {
                                triggerHaptic()
                                onToggleHaptic(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Bersihkan Riwayat Chat
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = LanguageMapper.getString("settings_clear_history_label", language),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = LanguageMapper.getString("settings_clear_history_desc", language),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = {
                                triggerHaptic()
                                onClearHistory()
                                Toast.makeText(context, LanguageMapper.getString("settings_clear_history_success", language), Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = LanguageMapper.getString("settings_clear_history_button", language), 
                                    fontSize = 11.sp, 
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ThemeColorHexCircle(
    hexColor: String,
    activeColor: String,
    onClick: () -> Unit
) {
    val isSelected = hexColor.equals(activeColor, ignoreCase = true)
    val color = try {
        Color(android.graphics.Color.parseColor(hexColor))
    } catch (e: Exception) {
        Color(0xFF008069)
    }

    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                Icons.Default.Done,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun SettingsCategoryHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
