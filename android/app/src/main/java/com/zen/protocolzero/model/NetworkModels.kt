package com.zen.protocolzero.model

/**
 * PROTOCOL ZERO Core Models
 * Note: Hardware telemetry is strictly set to UNAVAILABLE until the user uploads
 * their actual ESP32-CAM firmware tomorrow.
 */

data class NetworkState(
    val isConnected: Boolean = false,
    val isWifi: Boolean = false,
    val isCellular: Boolean = false,
    val wifiRssiDbm: Int? = null,
    val linkSpeedMbps: Int? = null,
    val ssid: String? = null,
    val localIp: String? = null,
    val internetReachable: Boolean = false,
    val pingLatencyMs: Long? = null,
    val packetLossPct: Int = 0
)

sealed class Esp32ConnectionState {
    object Idle : Esp32ConnectionState()
    object Connecting : Esp32ConnectionState()
    data class Connected(
        val ipAddress: String,
        val pingTimeMs: Long,
        val streamActive: Boolean
    ) : Esp32ConnectionState()
    data class Disconnected(
        val reason: String = "DEVICE NOT CONNECTED"
    ) : Esp32ConnectionState()
    data class Error(
        val message: String
    ) : Esp32ConnectionState()
}

/**
 * Isolated integration model for ESP32 hardware telemetry.
 * As instructed: "Do not invent API endpoints. Do not assume JSON field names.
 * Do not create fake hardware readings. Display 'DATA UNAVAILABLE' when actual data is unavailable."
 */
data class DeviceTelemetry(
    val isConfigured: Boolean = false,
    val statusText: String = "DATA UNAVAILABLE",
    val statusDescription: String = "ESP32-CAM Arduino firmware pending upload",
    val lastSyncTimestamp: Long? = null,
    val rawPayload: String? = null
)

enum class EventSeverity {
    INFO,
    SUCCESS,
    WARNING,
    ALERT,
    BOOST
}

data class NetworkEvent(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val title: String,
    val details: String,
    val severity: EventSeverity
)

data class ProtocolZeroConfig(
    val esp32Ip: String = "192.168.4.1", // Standard ESP32 AP or local network IP
    val streamPort: Int = 81,            // Typical ESP32-CAM stream port
    val telemetryPort: Int = 80,         // Typical webserver port
    val streamPath: String = "/stream",  // Stream endpoint (to be verified with firmware)
    val telemetryPath: String = "/status",// Telemetry endpoint (unbound until firmware upload)
    val autoBoost: Boolean = true,
    val pollIntervalMs: Long = 2000L,
    val timeoutMs: Long = 3000L
)
