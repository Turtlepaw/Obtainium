package dev.imranr.obtainium.appsources

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * GitHub app source implementation
 * Supports GitHub releases for apps
 */
class GitHubAppSource(private val httpClient: OkHttpClient) : AppSource {
    
    companion object {
        private const val GITHUB_API_BASE = "https://api.github.com"
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }
    
    override fun getName(): String = "GitHub"
    
    override fun isUrlCompatible(url: String): Boolean {
        return url.contains("github.com") || url.contains("api.github.com")
    }
    
    override suspend fun getReleaseInfo(url: String): ReleaseInfo {
        // Parse GitHub URL to get owner and repo
        val (owner, repo) = parseGitHubUrl(url)
        
        // Fetch latest release from GitHub API
        val apiUrl = "$GITHUB_API_BASE/repos/$owner/$repo/releases/latest"
        val request = Request.Builder()
            .url(apiUrl)
            .header("Accept", "application/vnd.github.v3+json")
            .build()
        
        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception("Failed to fetch release info: ${response.code}")
        }
        
        val jsonBody = response.body?.string() ?: throw Exception("Empty response body")
        val json = JSONObject(jsonBody)
        
        // Extract release information
        val version = json.optString("tag_name", "").removePrefix("v")
        val publishedAt = json.optString("published_at", "")
        val releaseDate = try {
            DATE_FORMAT.parse(publishedAt)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
        val releaseNotes = json.optString("body", "")
        val author = json.optJSONObject("author")?.optString("login", "") ?: ""
        
        // Find APK asset
        val assets = json.optJSONArray("assets")
        var downloadUrl = ""
        var fileSize = 0L
        
        if (assets != null) {
            for (i in 0 until assets.length()) {
                val asset = assets.getJSONObject(i)
                val name = asset.optString("name", "")
                if (name.endsWith(".apk", ignoreCase = true)) {
                    downloadUrl = asset.optString("browser_download_url", "")
                    fileSize = asset.optLong("size", 0L)
                    break
                }
            }
        }
        
        return ReleaseInfo(
            version = version,
            releaseDate = releaseDate,
            releaseNotes = releaseNotes,
            downloadUrl = downloadUrl,
            fileSize = fileSize,
            author = author,
            appName = repo
        )
    }
    
    override suspend fun getDownloadUrl(releaseInfo: ReleaseInfo): String {
        return releaseInfo.downloadUrl
    }
    
    /**
     * Parse GitHub URL to extract owner and repo name
     * Supports: github.com/owner/repo or api.github.com/repos/owner/repo
     */
    private fun parseGitHubUrl(url: String): Pair<String, String> {
        val cleanUrl = url.removeSuffix("/")
        
        // Match github.com/owner/repo pattern
        val githubPattern = Regex("github\\.com/([^/]+)/([^/]+)")
        val match = githubPattern.find(cleanUrl)
        
        if (match != null) {
            val owner = match.groupValues[1]
            val repo = match.groupValues[2]
            return Pair(owner, repo)
        }
        
        // Match api.github.com/repos/owner/repo pattern
        val apiPattern = Regex("api\\.github\\.com/repos/([^/]+)/([^/]+)")
        val apiMatch = apiPattern.find(cleanUrl)
        
        if (apiMatch != null) {
            val owner = apiMatch.groupValues[1]
            val repo = apiMatch.groupValues[2]
            return Pair(owner, repo)
        }
        
        throw IllegalArgumentException("Invalid GitHub URL: $url")
    }
}
