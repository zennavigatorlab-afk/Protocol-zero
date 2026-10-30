package com.zen.protocolzero.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import com.zen.protocolzero.model.NetworkState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

/**
 * PROTOCOL ZERO Real Android Network & Wi-Fi Monitor
 * Optimized for Redmi 9 (Android 10/11/12 MIUI / Custom ROM, API 23+)
 */
class NetworkMonitor(private val context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val _networkState = MutableStateFlow(NetworkState())
    val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            refreshNetworkMetrics()
        }

        override fun onLost(network: Network) {
            _networkState.value = _networkState.value.copy(
                isConnected = false,
                internetReachable = false
            )
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            val isWifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            val isCellular = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

            _networkState.value = _networkState.value.copy(
                isConnected = true,
                isWifi = isWifi,
                isCellular = isCellular,
                internetReachable = hasInternet
            )
            refreshNetworkMetrics()
        }
    }

    fun startMonitoring() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        try {
            connectivityManager.registerNetworkCallback(request, networkCallback)
        } catch (e: Exception) {
            // Fallback if needed
        }
        refreshNetworkMetrics()
    }

    fun stopMonitoring() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (_: Exception) {}
    }

    fun refreshNetworkMetrics() {
        coroutineScope.launch {
            val wifiInfo: WifiInfo? = try {
                wifiManager.connectionInfo
            } catch (e: SecurityException) {
                null
            }

            val rssi = wifiInfo?.rssi
            val linkSpeed = wifiInfo?.linkSpeed
            val rawSsid = wifiInfo?.ssid?.replace("\"", "")

            // Ping test for real latency measurement
            val latency = measureRealPingLatency()

            withContext(Dispatchers.Main) {
                _networkState.value = _networkState.value.copy(
                    wifiRssiDbm = if (rssi != null && rssi != -127) rssi else null,
                    linkSpeedMbps = if (linkSpeed != null && linkSpeed > 0) linkSpeed else null,
                    ssid = if (!rawSsid.isNullOrBlank() && rawSsid != "<unknown ssid>") rawSsid else "LOCAL_WIFI",
                    pingLatencyMs = latency,
                    internetReachable = latency != null && latency < 5000
                )
            }
        }
    }

    private suspend fun measureRealPingLatency(): Long? = withContext(Dispatchers.IO) {
        val targets = listOf("1.1.1.1", "8.8.8.8")
        for (host in targets) {
            try {
                val start = System.currentTimeMillis()
                Socket().use { socket ->
                    socket.connect(InetSocketAddress(host, 53), 1500)
                    return@withContext System.currentTimeMillis() - start
                }
            } catch (_: Exception) {}
        }
        null
    }
}
