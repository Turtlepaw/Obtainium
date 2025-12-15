package dev.imranr.obtainium.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.imranr.obtainium.ObtainiumApplication
import dev.imranr.obtainium.data.App
import dev.imranr.obtainium.data.AppDatabase
import dev.imranr.obtainium.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository
    private val database: AppDatabase = (application as ObtainiumApplication).database
    
    val apps: StateFlow<List<App>>
    val appsWithUpdates: StateFlow<List<App>>
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    init {
        repository = AppRepository(database.appDao())
        
        apps = repository.getAllApps()
            .combine(searchQuery) { apps, query ->
                if (query.isBlank()) {
                    apps
                } else {
                    apps.filter { app ->
                        app.name.contains(query, ignoreCase = true) ||
                        app.author.contains(query, ignoreCase = true)
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
        
        appsWithUpdates = repository.getAppsWithUpdates()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun addApp(app: App) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.insertApp(app)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateApp(app: App) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateApp(app)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteApp(app: App) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteApp(app)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    suspend fun getAppById(id: String): App? {
        return repository.getAppById(id)
    }
    
    fun checkForUpdates() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // TODO: Implement update checking logic
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun exportApps(): List<App> {
        return apps.value
    }
    
    fun importApps(apps: List<App>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.insertApps(apps)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}
