import java.io.FileInputStream
import java.util.Properties
import com.android.build.api.variant.FilterConfiguration.FilterType.*
import com.android.build.gradle.internal.api.ApkVariantOutputImpl

plugins {
    id("com.android.application") version "8.0.2"
    id("org.jetbrains.kotlin.android") version "1.8.10"
    id("com.google.devtools.ksp") version "1.8.10-1.0.9"
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.reader(Charsets.UTF_8).use { reader ->
        localProperties.load(reader)
    }
}

var appVersionCode = 2325
var appVersionName = "1.2.9"

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("key.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "dev.imranr.obtainium"
    compileSdk = 35
    ndkVersion = "27.0.12077973"

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
        freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    defaultConfig {
        applicationId = "dev.imranr.obtainium"
        minSdk = 24
        targetSdk = 35
        versionCode = appVersionCode
        versionName = appVersionName
        
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    flavorDimensions("flavor")

    productFlavors {
        create("normal") {
            dimension = "flavor"
            applicationIdSuffix = ""
        }
        create("fdroid") {
            dimension = "flavor"
            applicationIdSuffix = ".fdroid"
        }
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"].toString()
            keyPassword = keystoreProperties["keyPassword"].toString()
            storeFile = keystoreProperties["storeFile"]?.let { file(it) }
            storePassword = keystoreProperties["storePassword"].toString()
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }
}

val abiCodes = mapOf("x86_64" to 1, "armeabi-v7a" to 2, "arm64-v8a" to 3)

android.applicationVariants.configureEach {
    val variant = this
    variant.outputs.forEach { output ->
        val abiVersionCode = abiCodes[output.filters.find { it.filterType == "ABI" }?.identifier]
        if (abiVersionCode != null) {
            (output as ApkVariantOutputImpl).versionCodeOverride = variant.versionCode * 10 + abiVersionCode
        }
    }
}


dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
    
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    
    // Jetpack Compose BOM
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    
    // AndroidX Core
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.5")
    
    // ViewModel and StateFlow
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    
    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    
    // DataStore (replaces SharedPreferences)
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // HTML Parsing
    implementation("org.jsoup:jsoup:1.18.3")
    
    // WorkManager for background tasks
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    
    // Material Design 3
    implementation("com.google.android.material:material:1.12.0")
    
    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")
    
    // WebView
    implementation("androidx.webkit:webkit:1.12.1")
    
    // Image loading
    implementation("io.coil-kt:coil-compose:2.7.0")
    
    // JSON
    implementation("com.google.code.gson:gson:2.11.0")
    
    // Crypto
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    
    // File picker
    implementation("androidx.documentfile:documentfile:1.0.1")
    
    // Markdown rendering
    implementation("io.noties.markwon:core:4.6.2")
    implementation("io.noties.markwon:html:4.6.2")
}

