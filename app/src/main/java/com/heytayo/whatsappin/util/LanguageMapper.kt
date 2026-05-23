/**
 * =====================================================================
 * File: LanguageMapper.kt
 * Tujuan: Memetakan string/teks aplikasi secara dinamis mendukung multi-bahasa (Indonesia/Inggris)
 * Dipakai oleh: MainActivity, SettingsDialog, OcrSection, PhoneInputSection, HistorySection
 * Dependensi Utama: None
 * Daftar Fungsi: getString
 * Side Effect: None (Pure Utility)
 * =====================================================================
 */
package com.heytayo.whatsappin.util

object LanguageMapper {

    private val translations = mapOf(
        "id" to mapOf(
            "app_title" to "WhatsappIn",
            "chats_tab" to "CHATS",
            "direct_tab" to "DIRECT",
            "scan_tab" to "SCAN",
            
            // Top Bar & Search
            "search_placeholder" to "Cari obrolan atau nomor...",
            "menu_about" to "Tentang Aplikasi",
            "menu_settings" to "Pengaturan",
            "menu_permission" to "Aktifkan Izin Kontak",
            "toast_no_wa" to "WhatsApp tidak ditemukan",
            
            // History Section (CHATS)
            "history_title" to "Daftar Chat Terakhir",
            "history_clear" to "Bersihkan",
            "history_empty_title" to "Belum Ada Riwayat Chat",
            "history_empty_desc" to "Kirim pesan instan langsung tanpa menyimpan kontak terlebih dahulu.",
            "history_start_button" to "Mulai Kirim Pesan",
            "history_item_active" to "Aktif",
            "history_item_desc" to "Ketuk untuk membuka percakapan",
            
            // Phone Input Section (DIRECT)
            "direct_title" to "Kirim Pesan Baru",
            "direct_phone_label" to "Nomor Telepon",
            "direct_phone_placeholder" to "08xx atau +62xxx",
            "direct_phone_format" to "Format WhatsApp: +",
            "direct_phone_invalid" to "Nomor tidak valid",
            "direct_message_label" to "Pesan Pembuka (Opsional)",
            "direct_message_placeholder" to "Tulis pesan otomatis di sini...",
            "direct_quick_messages" to "Pesan Cepat Populer:",
            "direct_send_button" to "Kirim via WhatsApp",
            
            // OCR Section (SCAN)
            "scan_button" to "Pindai Nomor dari Gambar",
            "scan_processing" to "Sedang memproses & memindai gambar...",
            "scan_results_title" to "Hasil Pemindaian",
            "scan_guide_title" to "Ekstraksi Nomor dari Gambar",
            "scan_guide_desc" to "Punya gambar, screenshot, atau foto kartu nama berisi nomor telepon? Pindai gambarnya secara instan dan mulai kirim pesan langsung!",
            
            "scan_step_1_title" to "Pilih atau Ambil Gambar",
            "scan_step_1_desc" to "Ketuk tombol pindai di bawah, pilih gambar screenshot, dokumen, atau foto kartu nama dari galeri.",
            "scan_step_2_title" to "Proses Deteksi AI",
            "scan_step_2_desc" to "Sistem ML Kit cerdas akan membaca teks pada gambar secara offline dan mendeteksi nomor WhatsApp secara otomatis.",
            "scan_step_3_title" to "Mulai Percakapan",
            "scan_step_3_desc" to "Ketuk gelembung nomor yang terdeteksi untuk mengisi formulir kirim pesan tanpa simpan nomor.",
            
            // Settings Dialog
            "settings_title" to "Pengaturan",
            "settings_tab_general" to "Umum",
            "settings_tab_theme" to "Tema Accent",
            "settings_tab_presets" to "Pesan Kustom",
            "settings_tab_tools" to "Alat & Info",
            
            "settings_language_label" to "Bahasa Aplikasi (Language)",
            "settings_theme_mode_label" to "Mode Tampilan Tema",
            "settings_theme_mode_system" to "Mengikuti Sistem",
            "settings_theme_mode_light" to "Mode Terang",
            "settings_theme_mode_dark" to "Mode Gelap",
            
            "settings_theme_color_label" to "Pilih Warna Aksen Aplikasi",
            "settings_haptic_label" to "Getaran Haptic (Umpan Balik Getar)",
            "settings_haptic_desc" to "Memberikan getaran halus saat mengetuk tombol aksi",
            "settings_clear_history_label" to "Hapus Riwayat Kontak",
            "settings_clear_history_desc" to "Menghapus semua nomor telepon dari tab CHATS",
            "settings_clear_history_button" to "Bersihkan Sekarang",
            "settings_clear_history_success" to "Riwayat berhasil dibersihkan",
            
            "settings_preset_add_placeholder" to "Ketik pesan cepat baru...",
            "settings_preset_empty" to "Belum ada pesan cepat kustom.",
            "settings_preset_duplicate" to "Pesan cepat sudah ada!",
            
            "settings_close_button" to "Simpan & Tutup",
            
            // About Dialog
            "about_dialog_title" to "Tentang WhatsappIn",
            "about_dialog_version" to "Versi 1.0.0",
            "about_dialog_desc" to "Aplikasi utilitas instan untuk mengirim pesan WhatsApp tanpa harus menyimpan nomor telepon ke daftar kontak internal HP Anda.",
            "about_dialog_credits" to "Kredit & Pengembang:",
            "about_dialog_by" to "Tio (Ajit Prasetiyo)",
            "about_dialog_footer" to "Dikembangkan dengan ❤️ di Indonesia",
            "about_dialog_close" to "Tutup"
        ),
        "en" to mapOf(
            "app_title" to "WhatsappIn",
            "chats_tab" to "CHATS",
            "direct_tab" to "DIRECT",
            "scan_tab" to "SCAN",
            
            // Top Bar & Search
            "search_placeholder" to "Search chats or number...",
            "menu_about" to "About Application",
            "menu_settings" to "Settings",
            "menu_permission" to "Enable Contacts Permission",
            "toast_no_wa" to "WhatsApp not found",
            
            // History Section (CHATS)
            "history_title" to "Recent Chat History",
            "history_clear" to "Clear",
            "history_empty_title" to "No Chat History Yet",
            "history_empty_desc" to "Send instant messages directly without saving the contact first.",
            "history_start_button" to "Start Sending Message",
            "history_item_active" to "Active",
            "history_item_desc" to "Tap to open conversation",
            
            // Phone Input Section (DIRECT)
            "direct_title" to "Send New Message",
            "direct_phone_label" to "Phone Number",
            "direct_phone_placeholder" to "08xx or +62xxx",
            "direct_phone_format" to "WhatsApp Format: +",
            "direct_phone_invalid" to "Invalid number",
            "direct_message_label" to "Intro Message (Optional)",
            "direct_message_placeholder" to "Write automatic message here...",
            "direct_quick_messages" to "Popular Quick Messages:",
            "direct_send_button" to "Send via WhatsApp",
            
            // OCR Section (SCAN)
            "scan_button" to "Scan Number from Image",
            "scan_processing" to "Processing & scanning image...",
            "scan_results_title" to "Scanning Results",
            "scan_guide_title" to "Extract Numbers from Image",
            "scan_guide_desc" to "Have an image, screenshot, or business card photo containing a phone number? Scan it instantly and start chat immediately!",
            
            "scan_step_1_title" to "Select or Capture Image",
            "scan_step_1_desc" to "Tap the scan button below, choose a screenshot, document, or business card photo from your gallery.",
            "scan_step_2_title" to "AI Detection Process",
            "scan_step_2_desc" to "Our smart ML Kit reads text in the image offline and automatically extracts WhatsApp numbers.",
            "scan_step_3_title" to "Start Conversation",
            "scan_step_3_desc" to "Tap any detected phone number bubble to fill the message form without saving the contact.",
            
            // Settings Dialog
            "settings_title" to "Settings",
            "settings_tab_general" to "General",
            "settings_tab_theme" to "Accent Theme",
            "settings_tab_presets" to "Custom Presets",
            "settings_tab_tools" to "Tools & Info",
            
            "settings_language_label" to "App Language (Bahasa)",
            "settings_theme_mode_label" to "Theme Display Mode",
            "settings_theme_mode_system" to "Follow System Default",
            "settings_theme_mode_light" to "Light Mode",
            "settings_theme_mode_dark" to "Dark Mode",
            
            "settings_theme_color_label" to "Select App Accent Color",
            "settings_haptic_label" to "Haptic Feedback (Vibration)",
            "settings_haptic_desc" to "Provides a subtle vibration when tapping action buttons",
            "settings_clear_history_label" to "Clear Contact History",
            "settings_clear_history_desc" to "Removes all recent phone numbers from the CHATS tab",
            "settings_clear_history_button" to "Clear Now",
            "settings_clear_history_success" to "History cleared successfully",
            
            "settings_preset_add_placeholder" to "Type new quick message...",
            "settings_preset_empty" to "No custom quick messages yet.",
            "settings_preset_duplicate" to "Quick message already exists!",
            
            "settings_close_button" to "Save & Close",
            
            // About Dialog
            "about_dialog_title" to "About WhatsappIn",
            "about_dialog_version" to "Version 1.0.0",
            "about_dialog_desc" to "An instant utility app to send WhatsApp messages without having to save phone numbers to your device's contacts list.",
            "about_dialog_credits" to "Credits & Developer:",
            "about_dialog_by" to "Tio (Ajit Prasetiyo)",
            "about_dialog_footer" to "Developed with ❤️ in Indonesia",
            "about_dialog_close" to "Close"
        )
    )

    fun getString(key: String, language: String): String {
        val langMap = translations[language] ?: translations["id"]!!
        return langMap[key] ?: key
    }
}
