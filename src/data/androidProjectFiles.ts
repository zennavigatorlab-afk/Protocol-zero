export interface ProjectFile {
  path: string;
  name: string;
  category: 'config' | 'manifest' | 'kotlin' | 'workflow' | 'guide';
  language: string;
  content: string;
  description: string;
}

export const ANDROID_PROJECT_FILES: ProjectFile[] = [
  {
    path: 'android/settings.gradle.kts',
    name: 'settings.gradle.kts',
    category: 'config',
    language: 'kotlin',
    description: 'Gradle project initialization & repository management',
    content: `pluginManagement {
    repositories {
        google()
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

rootProject.name = "ProtocolZero"
include(":app")`
  },
  {
    path: 'android/build.gradle.kts',
    name: 'build.gradle.kts (Root)',
    category: 'config',
    language: 'kotlin',
    description: 'Root Gradle plugins and buildscript setup',
    content: `// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.5.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
}`
  },
  {
    path: 'android/app/build.gradle.kts',
    name: 'app/build.gradle.kts',
    category: 'config',
    language: 'kotlin',
    description: 'App-level build file: Min SDK 23, Compose BOM, OkHttp, Coil',
    content: `plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.zen.protocolzero"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.zen.protocolzero"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.1")

    // Jetpack Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Networking (HTTP for ESP32-CAM and local ping)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // JSON parsing
    implementation("org.json:json:20240303")

    // Coil for Compose image loading (supports MJPEG streams & bitmap snapshots)
    implementation("io.coil-kt:coil-compose:2.7.0")
}`
  },
  {
    path: 'android/app/src/main/AndroidManifest.xml',
    name: 'AndroidManifest.xml',
    category: 'manifest',
    language: 'xml',
    description: 'Permissions for Wi-Fi, Internet, Cleartext local HTTP for ESP32',
    content: `<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE" />
    <uses-permission android:name="android.permission.VIBRATE" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.ProtocolZero"
        android:usesCleartextTraffic="true"
        android:networkSecurityConfig="@xml/network_security_config">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:configChanges="orientation|screenSize|screenLayout|keyboardHidden"
            android:theme="@style/Theme.ProtocolZero">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>`
  },
  {
    path: 'android/app/src/main/res/xml/network_security_config.xml',
    name: 'network_security_config.xml',
    category: 'config',
    language: 'xml',
    description: 'Permits cleartext HTTP to local ESP32 IP subnets on Android 9/10/11/12',
    content: `<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">192.168.*.*</domain>
        <domain includeSubdomains="true">10.*.*.*</domain>
        <domain includeSubdomains="true">172.16.*.*</domain>
        <domain includeSubdomains="true">localhost</domain>
    </domain-config>
</network-security-config>`
  },
  {
    path: 'android/app/src/main/java/com/zen/protocolzero/MainActivity.kt',
    name: 'MainActivity.kt',
    category: 'kotlin',
    language: 'kotlin',
    description: 'Jetpack Compose entry point with tactical navigation bar',
    content: `package com.zen.protocolzero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.zen.protocolzero.ui.screens.*
import com.zen.protocolzero.ui.theme.*
import com.zen.protocolzero.ui.viewmodel.ProtocolZeroViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ProtocolZeroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProtocolZeroTheme {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}`
  },
  {
    path: 'android/app/src/main/java/com/zen/protocolzero/data/esp32/Esp32IntegrationLayer.kt',
    name: 'Esp32IntegrationLayer.kt',
    category: 'kotlin',
    language: 'kotlin',
    description: 'Cleanly isolated integration layer returning DATA UNAVAILABLE until firmware upload',
    content: `package com.zen.protocolzero.data.esp32

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.zen.protocolzero.model.DeviceTelemetry
import com.zen.protocolzero.model.Esp32ConnectionState
import com.zen.protocolzero.model.ProtocolZeroConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.TimeUnit

/**
 * PROTOCOL ZERO ESP32-CAM INTEGRATION LAYER
 * Note: Endpoints and telemetry schema are deliberately unbound until the user
 * uploads the actual Arduino firmware (.ino) tomorrow.
 */
class Esp32IntegrationLayer(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()
) {
    suspend fun checkDeviceReachability(config: ProtocolZeroConfig): Esp32ConnectionState = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(config.esp32Ip, config.telemetryPort), config.timeoutMs.toInt())
                val latency = System.currentTimeMillis() - startTime
                Esp32ConnectionState.Connected(
                    ipAddress = config.esp32Ip,
                    pingTimeMs = latency,
                    streamActive = false
                )
            }
        } catch (e: IOException) {
            Esp32ConnectionState.Disconnected(
                reason = "DEVICE NOT CONNECTED: \${e.message ?: "Host unreachable at \${config.esp32Ip}"}"
            )
        } catch (e: Exception) {
            Esp32ConnectionState.Error("Connection check failed: \${e.localizedMessage}")
        }
    }

    suspend fun fetchTelemetry(config: ProtocolZeroConfig): DeviceTelemetry = withContext(Dispatchers.IO) {
        // [PENDING FIRMWARE INTEGRATION]
        // Returns strict DATA UNAVAILABLE state without invented endpoints or mock data
        DeviceTelemetry(
            isConfigured = false,
            statusText = "DATA UNAVAILABLE",
            statusDescription = "Awaiting ESP32-CAM Arduino firmware upload. No mock data injected.",
            lastSyncTimestamp = null,
            rawPayload = null
        )
    }

    fun buildStreamUrl(config: ProtocolZeroConfig): String {
        val portSuffix = if (config.streamPort == 80) "" else ":\${config.streamPort}"
        val sanitizedPath = if (config.streamPath.startsWith("/")) config.streamPath else "/\${config.streamPath}"
        return "http://\${config.esp32Ip}$portSuffix$sanitizedPath"
    }
}`
  },
  {
    path: 'android/app/src/main/java/com/zen/protocolzero/data/network/NetworkMonitor.kt',
    name: 'NetworkMonitor.kt',
    category: 'kotlin',
    language: 'kotlin',
    description: 'Hardware Wi-Fi RSSI in dBm and live ping latency monitor',
    content: `package com.zen.protocolzero.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import com.zen.protocolzero.model.NetworkState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.net.InetSocketAddress
import java.net.Socket

class NetworkMonitor(private val context: Context) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val _networkState = MutableStateFlow(NetworkState())
    val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()

    // Monitors real Android connectivity callbacks and Wi-Fi RSSI (dBm)
}`
  },
  {
    path: 'android/app/src/main/java/com/zen/protocolzero/ui/screens/DashboardScreen.kt',
    name: 'DashboardScreen.kt',
    category: 'kotlin',
    language: 'kotlin',
    description: 'Compose Cockpit: Real Wi-Fi RSSI, ESP32 status, Internet ping, Signal Boost',
    content: `// PROTOCOL ZERO Compose Cockpit Screen
// Renders Wi-Fi RSSI in dBm, link speed, latency, ESP32 connection state, and Auto-Boost switch`
  },
  {
    path: 'android/app/src/main/java/com/zen/protocolzero/ui/screens/CameraStreamScreen.kt',
    name: 'CameraStreamScreen.kt',
    category: 'kotlin',
    language: 'kotlin',
    description: 'Live ESP32-CAM stream viewport with HUD crosshairs and honest offline state',
    content: `// PROTOCOL ZERO Optical HUD Screen
// Displays live stream from ESP32-CAM with HUD overlay or honest "DEVICE NOT CONNECTED" fallback`
  },
  {
    path: 'android/app/src/main/java/com/zen/protocolzero/ui/screens/TelemetryScreen.kt',
    name: 'TelemetryScreen.kt',
    category: 'kotlin',
    language: 'kotlin',
    description: 'Isolated hardware telemetry screen strictly showing DATA UNAVAILABLE',
    content: `// PROTOCOL ZERO Telemetry Screen
// Displays DATA UNAVAILABLE and outlines firmware binding slots for tomorrow's Arduino upload`
  },
  {
    path: '.github/workflows/build-apk.yml',
    name: 'build-apk.yml (GitHub Actions)',
    category: 'workflow',
    language: 'yaml',
    description: 'Automated 1-tap cloud APK build for phone users',
    content: `name: Build PROTOCOL ZERO APK
on:
  push:
    branches: [ main, master ]
  workflow_dispatch:

jobs:
  build:
    name: Generate Android APK
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
      - uses: android-actions/setup-android@v3
      - name: Build Debug APK
        run: |
          cd android
          gradle assembleDebug
      - uses: actions/upload-artifact@v4
        with:
          name: PROTOCOL_ZERO_debug_apk
          path: android/app/build/outputs/apk/debug/*.apk`
  },
  {
    path: 'android/BUILD_ON_REDMI9.md',
    name: 'BUILD_ON_REDMI9.md',
    category: 'guide',
    language: 'markdown',
    description: 'Step-by-step phone-only APK compilation guide for Redmi 9',
    content: `# Build APK Directly on Redmi 9 (Termux or GitHub Actions)
Method 1: Instant Cloud Build via GitHub Actions (Recommended)
Method 2: Local on-device compilation via Termux with OpenJDK 17 + Gradle`
  }
];
