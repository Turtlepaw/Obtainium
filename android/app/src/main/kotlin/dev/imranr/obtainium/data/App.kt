package dev.imranr.obtainium.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "apps")
@TypeConverters(Converters::class)
data class App(
    @PrimaryKey val id: String,
    val name: String,
    val url: String,
    val author: String = "",
    val installedVersion: String = "",
    val latestVersion: String = "",
    val apkUrl: String = "",
    val releaseDate: Long = 0,
    val addedDate: Long = System.currentTimeMillis(),
    val lastUpdateCheck: Long = 0,
    val updateAvailable: Boolean = false,
    val additionalSettings: Map<String, String> = emptyMap(),
    val categories: List<String> = emptyList(),
    val allowVersionDowngrade: Boolean = false,
    val exemptFromBackgroundUpdates: Boolean = false,
    val trackOnly: Boolean = false,
    val preferredReleaseType: String = "standard",
    val appSourceType: String = "github",
    val iconUrl: String = ""
)
