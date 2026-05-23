/**
 * =====================================================================
 * File: MainViewModel.kt
 * Tujuan: Mengelola State UI, setelan kustomisasi pengguna, dan logika bisnis utama aplikasi WhatsappIn
 * Dipakai oleh: MainActivity
 * Dependensi Utama: HistoryRepository, SettingsRepository, PhoneNumberUtils, Google ML Kit TextRecognition
 * Daftar Fungsi: onPhoneInputChanged, onMessageInputChanged, onSendClicked, onNumberSelected, processImage, clearOcrResults, setLanguage, setThemeColor, setThemeMode, addCustomTemplate, removeCustomTemplate, toggleHapticFeedback
 * Side Effect: I/O (menulis/membaca riwayat & setelan via DataStore), memicu pemrosesan OCR gambar
 * =====================================================================
 */
package com.heytayo.whatsappin.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.heytayo.whatsappin.data.HistoryRepository
import com.heytayo.whatsappin.data.SettingsRepository
import com.heytayo.whatsappin.data.AppSettings
import com.heytayo.whatsappin.util.PhoneNumberUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MainUiState(
    val phoneInput: String = "",
    val messageInput: String = "", // Pesan preset opsional
    val formattedNumber: String = "",
    val isValidNumber: Boolean = false,
    val ocrResults: List<String> = emptyList(),
    val isProcessingOcr: Boolean = false,
    val ocrError: String? = null,
    val snackbarMessage: String? = null
)

class MainViewModel(
    private val repository: HistoryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    // Flow Riwayat Kontak Terakhir
    val recentNumbers: StateFlow<List<String>> = repository.recentNumbers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flow Pengaturan/Settings Terintegrasi secara Reaktif
    val settingsState: StateFlow<AppSettings> = settingsRepository.settings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings(
                language = "id",
                themeColor = "Teal",
                themeMode = "System",
                templates = listOf("Halo, permisi...", "Boleh minta infonya?", "Saya tertarik dengan produk Anda.", "P"),
                hapticEnabled = true
            )
        )

    fun onPhoneInputChanged(input: String) {
        val formatted = PhoneNumberUtils.formatForWhatsApp(input)
        val isValid = PhoneNumberUtils.isValidIndonesianNumber(formatted)
        _uiState.value = _uiState.value.copy(
            phoneInput = input,
            formattedNumber = formatted,
            isValidNumber = isValid
        )
    }

    fun onMessageInputChanged(input: String) {
        _uiState.value = _uiState.value.copy(
            messageInput = input
        )
    }

    fun onSendClicked(): String? {
        val state = _uiState.value
        if (!state.isValidNumber) return null

        viewModelScope.launch {
            repository.addNumber(state.formattedNumber)
        }

        val baseUrl = "https://api.whatsapp.com/send?phone=${state.formattedNumber}"
        return if (state.messageInput.isNotEmpty()) {
            val encodedMessage = Uri.encode(state.messageInput)
            "$baseUrl&text=$encodedMessage"
        } else {
            baseUrl
        }
    }

    fun onNumberSelected(number: String) {
        val formatted = PhoneNumberUtils.formatForWhatsApp(number)
        _uiState.value = _uiState.value.copy(
            phoneInput = number,
            formattedNumber = formatted,
            isValidNumber = PhoneNumberUtils.isValidIndonesianNumber(formatted)
        )
    }

    fun processImage(context: Context, uri: Uri) {
        _uiState.value = _uiState.value.copy(
            isProcessingOcr = true,
            ocrError = null,
            ocrResults = emptyList()
        )

        try {
            val image = InputImage.fromFilePath(context, uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val numbers = PhoneNumberUtils.extractPhoneNumbers(visionText.text)
                    _uiState.value = _uiState.value.copy(
                        isProcessingOcr = false,
                        ocrResults = numbers,
                        ocrError = if (numbers.isEmpty()) "Tidak ditemukan nomor telepon di gambar" else null
                    )
                }
                .addOnFailureListener { e ->
                    _uiState.value = _uiState.value.copy(
                        isProcessingOcr = false,
                        ocrError = "Gagal membaca gambar: ${e.localizedMessage}"
                    )
                }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isProcessingOcr = false,
                ocrError = "Error: ${e.localizedMessage}"
            )
        }
    }

    fun clearOcrResults() {
        _uiState.value = _uiState.value.copy(ocrResults = emptyList(), ocrError = null)
    }

    fun removeFromHistory(number: String) {
        viewModelScope.launch {
            repository.removeNumber(number)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun dismissSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    // --- LOGIKA AKSI PENGATURAN (SETTINGS ACTIONS) ---

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            settingsRepository.updateLanguage(lang)
        }
    }

    fun setThemeColor(color: String) {
        viewModelScope.launch {
            settingsRepository.updateThemeColor(color)
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            settingsRepository.updateThemeMode(mode)
        }
    }

    fun addCustomTemplate(template: String) {
        viewModelScope.launch {
            settingsRepository.addTemplate(template)
        }
    }

    fun removeCustomTemplate(template: String) {
        viewModelScope.launch {
            settingsRepository.removeTemplate(template)
        }
    }

    fun toggleHapticFeedback(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setHapticEnabled(enabled)
        }
    }
}

class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val appContext = context.applicationContext
        return MainViewModel(
            HistoryRepository(appContext),
            SettingsRepository(appContext)
        ) as T
    }
}
