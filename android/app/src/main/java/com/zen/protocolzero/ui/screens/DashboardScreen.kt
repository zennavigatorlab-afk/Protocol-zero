package com.zen.protocolzero.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zen.protocolzero.model.Esp32ConnectionState
import com.zen.protocolzero.ui.theme.TextMuted
import com.zen.protocolzero.ui.theme.TextPrimary
import com.zen.protocolzero.ui.theme.TextSecondary
import com.zen.protocolzero.ui.theme.ZeroAmber
import com.zen.protocolzero.ui.theme.ZeroBackground
import com.zen.protocolzero.ui.theme.ZeroCyan
import com.zen.protocolzero.ui.theme.ZeroEmerald
import com.zen.protocolzero.ui.theme.ZeroRed
import com.zen.protocolzero.ui.theme.ZeroSurface
import com.zen.protocolzero.ui.theme.ZeroSurfaceBorder
import com.zen.protocolzero.ui.viewmodel.ProtocolZeroViewModel

@Composable
fun DashboardScreen(
    viewModel: ProtocolZeroViewModel,
    onNavigateToCamera: () -> Unit,
    onNavigateToTelemetry: () -> Unit
) {
    val networkState by viewModel.networkState.collectAsState()
    val esp32State by viewModel.esp32State.collectAsState()
    val telemetry by viewModel.telemetry.collectAsState()
    val config by viewModel.config.collectAsState()
    val isBoosting by viewModel.isBoosting.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZeroBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PROTOCOL ZERO",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ZeroEmerald,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "TACTICAL IOT & NETWORK ENGINE // REDMI 9",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
            Box(
                modifier = Modifier
                    .border(1.dp, if (networkState.isConnected) ZeroEmerald else ZeroRed, RoundedCornerShape(4.dp))
                    .background(ZeroSurface)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (networkState.isConnected) "SYS.READY" else "OFFLINE",
                    fontSize = 11.sp,
                    color = if (networkState.isConnected) ZeroEmerald else ZeroRed,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Live Wi-Fi & RSSI Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ZeroSurfaceBorder, RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = ZeroSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (networkState.isWifi) Icons.Default.Wifi else Icons.Default.SignalCellularAlt,
                            contentDescription = "Wi-Fi",
                            tint = ZeroCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "WI-FI RSSI & LINK STATUS",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = networkState.ssid ?: "SCANNING",
                        style = MaterialTheme.typography.labelSmall,
                        color = ZeroCyan
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricBox(
                        label = "SIGNAL STRENGTH",
                        value = if (networkState.wifiRssiDbm != null) "${networkState.wifiRssiDbm} dBm" else "N/A",
                        accentColor = when {
                            networkState.wifiRssiDbm == null -> TextMuted
                            networkState.wifiRssiDbm!! > -60 -> ZeroEmerald
                            networkState.wifiRssiDbm!! > -75 -> ZeroAmber
                            else -> ZeroRed
                        }
                    )
                    MetricBox(
                        label = "LINK SPEED",
                        value = if (networkState.linkSpeedMbps != null) "${networkState.linkSpeedMbps} Mbps" else "N/A",
                        accentColor = ZeroCyan
                    )
                    MetricBox(
                        label = "LATENCY (PING)",
                        value = if (networkState.pingLatencyMs != null) "${networkState.pingLatencyMs} ms" else "ERR",
                        accentColor = if (networkState.internetReachable) ZeroEmerald else ZeroRed
                    )
                }
            }
        }

        // ESP32-CAM Connection State Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    when (esp32State) {
                        is Esp32ConnectionState.Connected -> ZeroEmerald
                        is Esp32ConnectionState.Connecting -> ZeroAmber
                        else -> ZeroRed
                    },
                    RoundedCornerShape(8.dp)
                ),
            colors = CardDefaults.cardColors(containerColor = ZeroSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Router,
                            contentDescription = "ESP32",
                            tint = when (esp32State) {
                                is Esp32ConnectionState.Connected -> ZeroEmerald
                                is Esp32ConnectionState.Connecting -> ZeroAmber
                                else -> ZeroRed
                            },
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ESP32-CAM STATUS",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                    }

                    Text(
                        text = "${config.esp32Ip}:${config.telemetryPort}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Connection status display
                when (val state = esp32State) {
                    is Esp32ConnectionState.Connected -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Online", tint = ZeroEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "CONNECTED // ${state.pingTimeMs}ms ping", color = ZeroEmerald, fontSize = 13.sp)
                        }
                    }
                    is Esp32ConnectionState.Connecting -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = ZeroAmber)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "PROBING ESP32-CAM LINK...", color = ZeroAmber, fontSize = 13.sp)
                        }
                    }
                    is Esp32ConnectionState.Disconnected -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = "Offline", tint = ZeroRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = state.reason, color = ZeroRed, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    is Esp32ConnectionState.Error -> {
                        Text(text = "ERROR: ${state.message}", color = ZeroRed, fontSize = 13.sp)
                    }
                    is Esp32ConnectionState.Idle -> {
                        Text(text = "STANDBY", color = TextMuted, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.testEsp32Connection() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2836)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Probe", modifier = Modifier.size(16.dp), tint = ZeroCyan)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PROBE", fontSize = 12.sp, color = ZeroCyan)
                    }
                    Button(
                        onClick = onNavigateToCamera,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2836)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Stream", modifier = Modifier.size(16.dp), tint = ZeroEmerald)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("STREAM", fontSize = 12.sp, color = ZeroEmerald)
                    }
                }
            }
        }

        // Automatic Boost & Reconnect Switch
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ZeroSurfaceBorder, RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = ZeroSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AUTOMATIC BOOST & RECONNECT",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "Auto-reestablishes dropped sockets & accelerates ping polling",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                Switch(
                    checked = config.autoBoost,
                    onCheckedChange = { viewModel.updateConfig(config.copy(autoBoost = it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = ZeroEmerald,
                        checkedTrackColor = Color(0xFF0F3D24),
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = Color(0xFF1E2836)
                    )
                )
            }
        }

        // Real-Time Hardware Telemetry Notice (Strict Contract)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ZeroSurfaceBorder, RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = ZeroSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HARDWARE TELEMETRY",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = telemetry.statusText,
                        style = MaterialTheme.typography.labelSmall,
                        color = ZeroAmber,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = telemetry.statusDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onNavigateToTelemetry,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF161F2C)),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "VIEW FIRMWARE INTEGRATION SCHEMAS",
                        fontSize = 11.sp,
                        color = ZeroCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Big BOOST Trigger Button
        Button(
            onClick = { viewModel.triggerBoost() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !isBoosting,
            colors = ButtonDefaults.buttonColors(containerColor = ZeroEmerald),
            shape = RoundedCornerShape(6.dp)
        ) {
            if (isBoosting) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = ZeroBackground)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "BOOSTING LINK...", color = ZeroBackground, fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.Bolt, contentDescription = "Boost", tint = ZeroBackground)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "EXECUTE SIGNAL BOOST", color = ZeroBackground, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun MetricBox(
    label: String,
    value: String,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .border(1.dp, ZeroSurfaceBorder, RoundedCornerShape(4.dp))
            .background(Color(0xFF080C12))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column {
            Text(text = label, fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 14.sp, color = accentColor, fontWeight = FontWeight.Bold)
        }
    }
}
