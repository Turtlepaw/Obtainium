package dev.imranr.obtainium.appsources

/**
 * Base interface for all app sources
 */
interface AppSource {
    /**
     * Get the name of this app source
     */
    fun getName(): String
    
    /**
     * Check if a URL is compatible with this app source
     */
    fun isUrlCompatible(url: String): Boolean
    
    /**
     * Get release information from the app source
     * @param url The URL to fetch release info from
     * @return ReleaseInfo containing the latest release details
     */
    suspend fun getReleaseInfo(url: String): ReleaseInfo
    
    /**
     * Get the download URL for the latest APK
     * @param releaseInfo The release information
     * @return Direct download URL for the APK
     */
    suspend fun getDownloadUrl(releaseInfo: ReleaseInfo): String
}

/**
 * Data class containing release information
 */
data class ReleaseInfo(
    val version: String,
    val releaseDate: Long,
    val releaseNotes: String = "",
    val downloadUrl: String = "",
    val fileSize: Long = 0,
    val author: String = "",
    val appName: String = ""
)
