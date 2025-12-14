package dev.imranr.obtainium.repository

import dev.imranr.obtainium.data.App
import dev.imranr.obtainium.data.AppDao
import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {
    
    fun getAllApps(): Flow<List<App>> {
        return appDao.getAllApps()
    }
    
    fun getAppsWithUpdates(): Flow<List<App>> {
        return appDao.getAppsWithUpdates()
    }
    
    suspend fun getAppById(id: String): App? {
        return appDao.getAppById(id)
    }
    
    suspend fun insertApp(app: App) {
        appDao.insertApp(app)
    }
    
    suspend fun insertApps(apps: List<App>) {
        appDao.insertApps(apps)
    }
    
    suspend fun updateApp(app: App) {
        appDao.updateApp(app)
    }
    
    suspend fun deleteApp(app: App) {
        appDao.deleteApp(app)
    }
    
    suspend fun deleteAppById(id: String) {
        appDao.deleteAppById(id)
    }
    
    suspend fun deleteAllApps() {
        appDao.deleteAllApps()
    }
    
    suspend fun getAppCount(): Int {
        return appDao.getAppCount()
    }
    
    fun searchApps(query: String): Flow<List<App>> {
        return appDao.searchApps(query)
    }
}
