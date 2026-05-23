// =====================================================================
// File: settings.gradle.kts
// Tujuan: Mengonfigurasi modul dan setelan proyek Gradle tingkat root
// Dipakai oleh: Gradle Build System
// Dependensi Utama: None
// Daftar Fungsi: Konfigurasi repository pluginManagement & dependencyResolutionManagement, rootProject.name, include
// Side Effect: Menentukan struktur modul proyek dan repository download dependensi
// =====================================================================

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "WhatsappIn"
include(":app")
