package com.zen.protocolzero.data.esp32

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
 *
 * ARCHITECTURAL NOTICE:
 * This layer is cleanly isolated as a dedicated bridge for the ESP32-CAM hardware.
 * Endpoints and JSON parsing contracts are deliberately unbound until the actual
 * Arduino firmware (.ino) is uploaded by the user.
 *
 * Current State:
 * - Ping test tests real TCP socket connectivity to the configured IP:Port.
 * - HTTP stream test checks if an MJPEG or JPEG endpoint is responding.
 * - Telemetry is marked as [DATA UNAVAILABLE] without simulated or fake data.
 */
class Esp32IntegrationLayer(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()
) {

    /**
     * Checks if the ESP32-CAM device is reachable on the local Wi-Fi subnet.
     * Uses real non-blocking socket connection test without guessing firmware API.
     */
    suspend fun checkDeviceReachability(config: ProtocolZeroConfig): Esp32ConnectionState = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        try {
            Socket().use { socket ->
                // Tests raw TCP reachability at configured target IP and port
                socket.connect(InetSocketAddress(config.esp32Ip, config.telemetryPort), config.timeoutMs.toInt())
                val latency = System.currentTimeMillis() - startTime
                Esp32ConnectionState.Connected(
                    ipAddress = config.esp32Ip,
                    pingTimeMs = latency,
                    streamActive = false // Stream checked independently
                )
            }
        } catch (e: IOException) {
            Esp32ConnectionState.Disconnected(
                reason = "DEVICE NOT CONNECTED: ${e.message ?: "Host unreachable at ${config.esp32Ip}"}"
            )
        } catch (e: Exception) {
            Esp32ConnectionState.Error("Connection check failed: ${e.localizedMessage}")
        }
    }

    /**
     * Fetches live telemetry from the ESP32 once the firmware is uploaded.
     * STRICT CONTRACT:
     * Returns "DATA UNAVAILABLE" until user supplies the actual Arduino firmware endpoints.
     */
    suspend fun fetchTelemetry(config: ProtocolZeroConfig): DeviceTelemetry = withContext(Dispatchers.IO) {
        // [PENDING FIRMWARE INTEGRATION]
        // This section will be adapted immediately after the firmware is uploaded.
        // It will make an HTTP GET to the actual endpoint (e.g., http://${config.esp32Ip}/status)
        // and parse the concrete JSON fields.
        DeviceTelemetry(
            isConfigured = false,
            statusText = "DATA UNAVAILABLE",
            statusDescription = "Awaiting ESP32-CAM Arduino firmware upload. No mock data injected.",
            lastSyncTimestamp = null,
            rawPayload = null
        )
    }

    /**
     * Constructs the stream URL from user configuration.
     * Default convention for AI-Thinker ESP32-CAM is usually http://<ip>:81/stream
     * or http://<ip>/stream. This is fully configurable via the settings screen.
     */
    fun buildStreamUrl(config: ProtocolZeroConfig): String {
        val portSuffix = if (config.streamPort == 80) "" else ":${config.streamPort}"
        val sanitizedPath = if (config.streamPath.startsWith("/")) config.streamPath else "/${config.streamPath}"
        return "http://${config.esp32Ip}$portSuffix$sanitizedPath"
    }

    /**
     * Attempts to grab a single snapshot frame from the camera to verify stream health.
     */
    suspend fun fetchSingleFrame(frameUrl: String): Result<Bitmap> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(frameUrl).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IOException("HTTP Error: ${response.code}"))
                }
                val bytes = response.body?.bytes()
                    ?: return@withContext Result.failure(IOException("Empty response body"))
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    ?: return@withContext Result.failure(IOException("Failed to decode image frame"))
                Result.success(bitmap)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
