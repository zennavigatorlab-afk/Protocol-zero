package com.zen.protocolzero.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zen.protocolzero.data.esp32.Esp32IntegrationLayer
import com.zen.protocolzero.data.network.NetworkMonitor
import com.zen.protocolzero.model.DeviceTelemetry
import com.zen.protocolzero.model.Esp32ConnectionState
import com.zen.protocolzero.model.EventSeverity
import com.zen.protocolzero.model.NetworkEvent
import com.zen.protocolzero.model.NetworkState
import com.zen.protocolzero.model.ProtocolZeroConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

class ProtocolZeroViewModel(application: Application) : AndroidViewModel(application) {

    private val networkMonitor = NetworkMonitor(application)
    private val esp32Layer = Esp32IntegrationLayer()

    val networkState: StateFlow<NetworkState> = networkMonitor.networkState

    private val _esp32State = MutableStateFlow<Esp32ConnectionState>(
        Esp32ConnectionState.Disconnected("DEVICE NOT CONNECTED")
    )
    val esp32State: StateFlow<Esp32ConnectionState> = _esp32State.asStateFlow()

    private val _telemetry = MutableStateFlow(DeviceTelemetry())
    val telemetry: StateFlow<DeviceTelemetry> = _telemetry.asStateFlow()

    private val _config = MutableStateFlow(ProtocolZeroConfig())
    val config: StateFlow<ProtocolZeroConfig> = _config.asStateFlow()

    private val _events = MutableStateFlow<List<NetworkEvent>>(emptyList())
    val events: StateFlow<List<NetworkEvent>> = _events.asStateFlow()

    private val _isBoosting = MutableStateFlow(false)
    val isBoosting: StateFlow<Boolean> = _isBoosting.asStateFlow()

    private var pollingJob: Job? = null

    init {
        networkMonitor.startMonitoring()
        logEvent("SYSTEM_INIT", "PROTOCOL ZERO Mobile Engine Started on Redmi 9", EventSeverity.INFO)
        startPeriodicPolling()
    }

    fun updateConfig(newConfig: ProtocolZeroConfig) {
        _config.value = newConfig
        logEvent("CONFIG_UPDATE", "ESP32 Target set to ${newConfig.esp32Ip}:${newConfig.telemetryPort}", EventSeverity.INFO)
        testEsp32Connection()
    }

    fun testEsp32Connection() {
        viewModelScope.launch {
            _esp32State.value = Esp32ConnectionState.Connecting
            val state = esp32Layer.checkDeviceReachability(_config.value)
            _esp32State.value = state

            when (state) {
                is Esp32ConnectionState.Connected -> {
                    logEvent("ESP32_LINK", "Connected to ESP32-CAM (${state.pingTimeMs}ms)", EventSeverity.SUCCESS)
                }
                is Esp32ConnectionState.Disconnected -> {
                    logEvent("ESP32_OFFLINE", state.reason, EventSeverity.WARNING)
                }
                is Esp32ConnectionState.Error -> {
                    logEvent("ESP32_ERR", state.message, EventSeverity.ALERT)
                }
                else -> {}
            }
        }
    }

    fun triggerBoost() {
        viewModelScope.launch {
            _isBoosting.value = true
            logEvent("BOOST_TRIGGER", "Aggressive network refresh initiated", EventSeverity.BOOST)
            networkMonitor.refreshNetworkMetrics()
            testEsp32Connection()
            delay(1200)
            _isBoosting.value = false
            logEvent("BOOST_COMPLETE", "Network scan calibrated", EventSeverity.SUCCESS)
        }
    }

    private fun startPeriodicPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                val currentConfig = _config.value
                val currentState = _esp32State.value

                // Periodically probe target if connected or autoBoost enabled
                if (currentConfig.autoBoost || currentState is Esp32ConnectionState.Connected) {
                    val reachability = esp32Layer.checkDeviceReachability(currentConfig)
                    _esp32State.value = reachability
                }

                // Strictly fetch hardware telemetry (which returns DATA UNAVAILABLE until firmware uploaded)
                _telemetry.value = esp32Layer.fetchTelemetry(currentConfig)

                delay(currentConfig.pollIntervalMs)
            }
        }
    }

    fun logEvent(title: String, detail: String, severity: EventSeverity) {
        val newEvent = NetworkEvent(
            id = UUID.randomUUID().toString().take(8),
            title = title,
            details = detail,
            severity = severity
        )
        val updated = listOf(newEvent) + _events.value.take(49)
        _events.value = updated
    }

    fun clearEvents() {
        _events.value = emptyList()
    }

    fun getStreamUrl(): String {
        return esp32Layer.buildStreamUrl(_config.value)
    }

    override fun onCleared() {
        super.onCleared()
        networkMonitor.stopMonitoring()
        pollingJob?.cancel()
    }
}
