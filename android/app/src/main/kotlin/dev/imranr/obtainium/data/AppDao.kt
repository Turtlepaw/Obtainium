package dev.imranr.obtainium.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM apps ORDER BY name ASC")
    fun getAllApps(): Flow<List<App>>
    
    @Query("SELECT * FROM apps WHERE id = :id")
    suspend fun getAppById(id: String): App?
    
    @Query("SELECT * FROM apps WHERE updateAvailable = 1")
    fun getAppsWithUpdates(): Flow<List<App>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: App)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<App>)
    
    @Update
    suspend fun updateApp(app: App)
    
    @Delete
    suspend fun deleteApp(app: App)
    
    @Query("DELETE FROM apps WHERE id = :id")
    suspend fun deleteAppById(id: String)
    
    @Query("DELETE FROM apps")
    suspend fun deleteAllApps()
    
    @Query("SELECT COUNT(*) FROM apps")
    suspend fun getAppCount(): Int
    
    @Query("SELECT * FROM apps WHERE name LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    fun searchApps(query: String): Flow<List<App>>
}
