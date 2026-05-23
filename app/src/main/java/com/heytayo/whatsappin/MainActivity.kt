/**
 * =====================================================================
 * File: MainActivity.kt
 * Tujuan: Activity utama pengontrol UI aplikasi WhatsappIn berbasis tab navigasi mirip WhatsApp asli, mendukung halaman pengaturan full-screen
 * Dipakai oleh: Sistem Operasi Android (Launcher)
 * Dependensi Utama: MainViewModel, WhatsappInTheme, LanguageMapper, SettingsScreen, ContactUtils
 * Daftar Fungsi: onCreate, WADirectApp, WhatsAppTopBar, EmptyChatsState, AboutDialog
 * Side Effect: Menampilkan layar interaktif, meluncurkan photo picker, memicu intent luar ke WhatsApp, membaca kontak, mengubah preferensi pengaturan
 * =====================================================================
 */
package com.heytayo.whatsappin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heytayo.whatsappin.ui.components.HistorySection
import com.heytayo.whatsappin.ui.components.OcrSection
import com.heytayo.whatsappin.ui.components.PhoneInputSection
import com.heytayo.whatsappin.ui.components.SettingsScreen
import com.heytayo.whatsappin.ui.theme.WhatsappInTheme
import com.heytayo.whatsappin.util.ContactUtils
import com.heytayo.whatsappin.util.LanguageMapper
import com.heytayo.whatsappin.viewmodel.MainViewModel
import com.heytayo.whatsappin.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WADirectApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WADirectApp() {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(context)
    )

    // Memantau State dari ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val recentNumbers by viewModel.recentNumbers.collectAsState()
    val settings by viewModel.settingsState.collectAsState()
    
    val language = settings.language
    val themeColor = settings.themeColor
    val themeMode = settings.themeMode
    val customTemplates = settings.templates
    val hapticEnabled = settings.hapticEnabled

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf(
        LanguageMapper.getString("chats_tab", language),
        LanguageMapper.getString("direct_tab", language),
        LanguageMapper.getString("scan_tab", language)
    )

    // State Navigasi & Dialog
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var isSettingsPageActive by remember { mutableStateOf(false) }

    // State Izin Kontak
    var hasContactsPermission by remember {
        mutableStateOf(ContactUtils.hasContactsPermission(context))
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasContactsPermission = isGranted
    }

    // Minta izin secara otomatis saat pertama kali dibuka
    LaunchedEffect(Unit) {
        if (!hasContactsPermission) {
            permissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
        }
    }

    // Photo Picker launcher untuk scan OCR
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { 
            viewModel.processImage(context, it)
            selectedTab = 2 // Langsung pindah ke tab SCAN
        }
    }

    fun triggerHapticFeedback() {
        if (hapticEnabled) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    fun openWhatsApp(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, LanguageMapper.getString("toast_no_wa", language), Toast.LENGTH_SHORT).show()
        }
    }

    // Filter daftar riwayat chat berdasarkan pencarian
    val filteredNumbers = remember(recentNumbers, searchQuery, hasContactsPermission) {
        if (searchQuery.isEmpty()) {
            recentNumbers
        } else {
            recentNumbers.filter { number ->
                val name = ContactUtils.getContactName(context, number)
                number.contains(searchQuery, ignoreCase = true) || 
                        (name != null && name.contains(searchQuery, ignoreCase = true))
            }
        }
    }

    // Integrasi skema warna kustom HEX tak terbatas dan mode gelap/terang
    WhatsappInTheme(
        themeColor = themeColor,
        themeMode = themeMode
    ) {
        if (isSettingsPageActive) {
            // Halaman Pengaturan Halaman Penuh (Settings Screen Full-Screen)
            SettingsScreen(
                language = language,
                themeColor = themeColor,
                themeMode = themeMode,
                templates = customTemplates,
                hapticEnabled = hapticEnabled,
                onLanguageChanged = viewModel::setLanguage,
                onThemeColorChanged = viewModel::setThemeColor,
                onThemeModeChanged = viewModel::setThemeMode,
                onAddTemplate = viewModel::addCustomTemplate,
                onRemoveTemplate = viewModel::removeCustomTemplate,
                onToggleHaptic = viewModel::toggleHapticFeedback,
                onClearHistory = viewModel::clearHistory,
                onBack = { isSettingsPageActive = false }
            )
        } else {
            // Halaman Utama Aplikasi
            Scaffold(
                topBar = {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.96f))
                            .statusBarsPadding()
                    ) {
                        if (isSearchActive) {
                            // Search Top Bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { 
                                    isSearchActive = false
                                    searchQuery = ""
                                }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack, 
                                        contentDescription = "Kembali",
                                        tint = Color.White
                                    )
                                }
                                TextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = { Text(LanguageMapper.getString("search_placeholder", language), color = Color.White.copy(alpha = 0.6f)) },
                                    singleLine = true,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        cursorColor = Color.White,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                    keyboardActions = KeyboardActions(onSearch = { isSearchActive = false }),
                                    modifier = Modifier.weight(1f)
                                )
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            Icons.Default.Clear, 
                                            contentDescription = "Hapus",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        } else {
                            // Standard Top Bar
                            WhatsAppTopBar(
                                language = language,
                                onCameraClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                onSearchClick = { isSearchActive = true },
                                onMenuClick = { showMenu = !showMenu },
                                showMenu = showMenu,
                                onDismissMenu = { showMenu = false },
                                onAboutClick = {
                                    showAboutDialog = true
                                    showMenu = false
                                },
                                onSettingsClick = {
                                    isSettingsPageActive = true
                                    showMenu = false
                                },
                                onRequestPermissionClick = {
                                    permissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
                                    showMenu = false
                                },
                                hasPermission = hasContactsPermission
                            )
                        }
                        
                        // TabRow
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    height = 3.dp,
                                    color = Color.White
                                )
                            }
                        ) {
                            tabTitles.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { 
                                        triggerHapticFeedback()
                                        selectedTab = index 
                                    },
                                    text = {
                                        Text(
                                            text = title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (selectedTab == index) Color.White else Color.White.copy(alpha = 0.7f)
                                        )
                                    }
                                )
                            }
                        }
                    }
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
                    when (selectedTab) {
                        // Tab 0: Chats
                        0 -> {
                            if (recentNumbers.isEmpty()) {
                                EmptyChatsState(language = language, onStartChatClick = { selectedTab = 1 })
                            } else {
                                HistorySection(
                                    recentNumbers = filteredNumbers,
                                    language = language,
                                    onNumberSelected = { number ->
                                        triggerHapticFeedback()
                                        viewModel.onNumberSelected(number)
                                        selectedTab = 1
                                    },
                                    onRemoveNumber = viewModel::removeFromHistory,
                                    onClearHistory = viewModel::clearHistory
                                )
                            }
                        }
                        
                        // Tab 1: Direct Send
                        1 -> {
                            PhoneInputSection(
                                phoneInput = uiState.phoneInput,
                                messageInput = uiState.messageInput,
                                formattedNumber = uiState.formattedNumber,
                                isValid = uiState.isValidNumber,
                                language = language,
                                templates = customTemplates,
                                onInputChanged = viewModel::onPhoneInputChanged,
                                onMessageInputChanged = {
                                    triggerHapticFeedback()
                                    viewModel.onMessageInputChanged(it)
                                },
                                onSendClicked = {
                                    triggerHapticFeedback()
                                    viewModel.onSendClicked()?.let { url -> openWhatsApp(url) }
                                }
                            )
                        }
                        
                        // Tab 2: Scan Nomor (OCR)
                        2 -> {
                            OcrSection(
                                ocrResults = uiState.ocrResults,
                                isProcessing = uiState.isProcessingOcr,
                                ocrError = uiState.ocrError,
                                language = language,
                                onPickImage = {
                                    triggerHapticFeedback()
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                onNumberSelected = { number ->
                                    triggerHapticFeedback()
                                    viewModel.onNumberSelected(number)
                                    selectedTab = 1
                                },
                                onClearResults = viewModel::clearOcrResults
                            )
                        }
                    }
                }
            }
        }

        // Dialog Tentang Aplikasi
        if (showAboutDialog) {
            AboutDialog(language = language, onDismiss = { showAboutDialog = false })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsAppTopBar(
    language: String,
    onCameraClick: () -> Unit,
    onSearchClick: () -> Unit,
    onMenuClick: () -> Unit,
    showMenu: Boolean,
    onDismissMenu: () -> Unit,
    onAboutClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onRequestPermissionClick: () -> Unit,
    hasPermission: Boolean
) {
    TopAppBar(
        title = {
            Text(
                text = LanguageMapper.getString("app_title", language),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        },
        actions = {
            IconButton(onClick = onCameraClick) {
                Icon(Icons.Default.PhotoCamera, contentDescription = "Pindai Gambar")
            }
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, contentDescription = "Cari Chat")
            }
            Box {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = onDismissMenu,
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    DropdownMenuItem(
                        text = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(LanguageMapper.getString("menu_settings", language), color = MaterialTheme.colorScheme.onSurface)
                            }
                        },
                        onClick = onSettingsClick
                    )
                    DropdownMenuItem(
                        text = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(LanguageMapper.getString("menu_about", language), color = MaterialTheme.colorScheme.onSurface)
                            }
                        },
                        onClick = onAboutClick
                    )
                    if (!hasPermission) {
                        DropdownMenuItem(
                            text = { Text(LanguageMapper.getString("menu_permission", language), color = MaterialTheme.colorScheme.onSurface) },
                            onClick = onRequestPermissionClick
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Composable
fun EmptyChatsState(
    language: String,
    onStartChatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Forum,
                    contentDescription = null,
                    modifier = Modifier.size(44.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = LanguageMapper.getString("history_empty_title", language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = LanguageMapper.getString("history_empty_desc", language),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onStartChatClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Chat, 
                        contentDescription = null, 
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = LanguageMapper.getString("history_start_button", language), 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AboutDialog(
    language: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Info, 
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = LanguageMapper.getString("about_dialog_title", language),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = LanguageMapper.getString("about_dialog_version", language),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = LanguageMapper.getString("about_dialog_desc", language),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = LanguageMapper.getString("about_dialog_credits", language),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = LanguageMapper.getString("about_dialog_by", language),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = LanguageMapper.getString("about_dialog_footer", language),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(LanguageMapper.getString("about_dialog_close", language), fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
