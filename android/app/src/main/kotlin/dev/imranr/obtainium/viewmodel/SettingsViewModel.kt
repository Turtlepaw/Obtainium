package dev.imranr.obtainium.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class Settings(
    val theme: String = "system",
    val enableBackgroundUpdates: Boolean = true,
    val updateCheckInterval: Int = 12,
    val showNotifications: Boolean = true,
    val autoDownload: Boolean = false,
    val autoInstall: Boolean = false,
    val language: String = "en"
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _settings = MutableStateFlow(Settings())
    val settings: StateFlow<Settings> = _settings.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // TODO: Load settings from DataStore
                _settings.value = Settings()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateTheme(theme: String) {
        viewModelScope.launch {
            _settings.value = _settings.value.copy(theme = theme)
            saveSettings()
        }
    }
    
    fun updateBackgroundUpdates(enabled: Boolean) {
        viewModelScope.launch {
            _settings.value = _settings.value.copy(enableBackgroundUpdates = enabled)
            saveSettings()
        }
    }
    
    fun updateCheckInterval(interval: Int) {
        viewModelScope.launch {
            _settings.value = _settings.value.copy(updateCheckInterval = interval)
            saveSettings()
        }
    }
    
    fun updateNotifications(enabled: Boolean) {
        viewModelScope.launch {
            _settings.value = _settings.value.copy(showNotifications = enabled)
            saveSettings()
        }
    }
    
    fun updateAutoDownload(enabled: Boolean) {
        viewModelScope.launch {
            _settings.value = _settings.value.copy(autoDownload = enabled)
            saveSettings()
        }
    }
    
    fun updateAutoInstall(enabled: Boolean) {
        viewModelScope.launch {
            _settings.value = _settings.value.copy(autoInstall = enabled)
            saveSettings()
        }
    }
    
    fun updateLanguage(language: String) {
        viewModelScope.launch {
            _settings.value = _settings.value.copy(language = language)
            saveSettings()
        }
    }
    
    private fun saveSettings() {
        viewModelScope.launch {
            try {
                // TODO: Save settings to DataStore
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
