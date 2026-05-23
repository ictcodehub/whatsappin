Mega-Prompt: Pembuatan Aplikasi Android Native "WA Direct"Salin seluruh teks di bawah ini dan berikan ke AI asisten pemrogramanmu untuk mulai membuat aplikasinya.Bertindaklah sebagai Senior Android Developer yang ahli dalam Kotlin, Jetpack Compose, Material Design 3, dan Google ML Kit. Saya ingin membuat aplikasi Android Native bernama "WA Direct" (aplikasi kirim pesan WhatsApp tanpa simpan nomor kontak). 

Tolong buatkan panduan langkah demi langkah beserta kode lengkap dan bersih (clean code) untuk membuat aplikasi ini dari awal di Android Studio.

### KEBUTUHAN UTAMA APLIKASI
1. **Bahasa & Framework:** Kotlin, Jetpack Compose (UI modern), Material 3.
2. **Minimum SDK:** Android 26 (Android 8.0) atau lebih tinggi.
3. **Fitur 1: Input Manual Pintar**
   - Kolom input untuk nomor telepon dengan auto-format.
   - Mengubah otomatis angka "0" di depan menjadi "+62" (atau kode negara default Indonesia).
   - Menghapus otomatis karakter non-angka seperti spasi, tanda hubung (`-`), atau tanda kurung.
   - Tombol "Kirim" yang memicu Intent untuk langsung membuka chat WhatsApp menggunakan skema `https://api.whatsapp.com/send?phone=NOMOR`.
4. **Fitur 2: Deteksi Nomor dari Screenshot/Gambar (OCR Offline)**
   - Tombol untuk memilih gambar dari galeri menggunakan Photo Picker Android modern (`ActivityResultContracts.PickVisualMedia`).
   - Ekstraksi teks dari gambar menggunakan **Google ML Kit Text Recognition** (berjalan 100% offline dan lokal di perangkat).
   - Filter teks hasil OCR menggunakan Regular Expression (Regex) khusus nomor telepon Indonesia (misal mencari pola yang berawalan `+62`, `62`, atau `08` dengan panjang 9-14 digit).
   - Menampilkan daftar nomor yang ditemukan dalam bentuk daftar (List) interaktif. Saat salah satu nomor diklik, nomor tersebut akan otomatis terisi ke input manual atau langsung membuka WhatsApp.
5. **Fitur 3: Riwayat Kontak Terakhir (Recent History)**
   - Menyimpan daftar nomor terakhir yang dihubungi menggunakan `Jetpack DataStore` (ringan dan aman) agar pengguna bisa mengklik ulang nomor tersebut tanpa mengetik lagi.
   - Batasi riwayat maksimal 10 nomor terakhir saja.

### FORMAT OUTPUT YANG SAYA BUTUHKAN

Tolong berikan instruksi dan kode yang terstruktur sebagai berikut:

1. **Konfigurasi `build.gradle.kts` (Project & App Level):**
   - Tunjukkan dependensi apa saja yang perlu ditambahkan (seperti dependencies untuk Google ML Kit Text Recognition, Jetpack DataStore, dan Compose Activity).
2. **Konfigurasi `AndroidManifest.xml`:**
   - Izin (Permissions) apa saja yang perlu dimasukkan.
   - Konfigurasi Intent Filter dasar jika ada.
3. **Arsitektur Kode & File Utama:**
   - Berikan kode lengkap untuk `MainActivity.kt` yang mengimplementasikan UI Jetpack Compose, logika validasi Regex, integrasi ML Kit, dan sistem Intent ke WhatsApp.
   - Pisahkan logika dengan rapi (bisa menggunakan pendekatan MVVM sederhana atau State Management Compose yang bersih dalam satu berkas atau beberapa berkas terstruktur).
4. **Petunjuk Pengujian:**
   - Jelaskan singkat cara menjalankan dan menguji fitur OCR-nya di Emulator atau perangkat HP fisik.

Gunakan standar koding modern Kotlin, penanganan error (exception handling) yang aman untuk pembacaan gambar, serta desain UI yang bersih, minimalis, dan ramah pengguna (user-friendly).
