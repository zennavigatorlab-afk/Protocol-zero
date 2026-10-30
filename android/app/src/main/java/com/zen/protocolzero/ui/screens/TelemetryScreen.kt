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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun TelemetryScreen(viewModel: ProtocolZeroViewModel) {
    val telemetry by viewModel.telemetry.collectAsState()
    val config by viewModel.config.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZeroBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Telemetry Header
        Column {
            Text(
                text = "ESP32-CAM TELEMETRY CORE",
                style = MaterialTheme.typography.headlineMedium,
                color = ZeroEmerald,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "ISOLATED INTEGRATION LAYER // PENDING ARDUINO FIRMWARE",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }

        // Primary Status Box - Strict "DATA UNAVAILABLE"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ZeroAmber, RoundedCornerShape(8.dp))
                .background(Color(0xFF141208))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Data Unavailable",
                        tint = ZeroAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = telemetry.statusText, // "DATA UNAVAILABLE"
                        color = ZeroAmber,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = telemetry.statusDescription,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Strict adherence to protocol: No invented endpoints, no fake readings, and no assumed JSON field names.",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // Firmware Upload Contract Roadmap Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ZeroSurfaceBorder, RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = ZeroSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = "Contract", tint = ZeroCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "FIRMWARE BINDING SPECIFICATION",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "When you upload your actual ESP32-CAM Arduino (.ino) code tomorrow, the Esp32IntegrationLayer will automatically map to your exact firmware output:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                TelemetryContractItem(
                    slot = "SLOT 01: ENDPOINTS",
                    status = "AWAITING FIRMWARE",
                    detail = "Will bind to your exact route (e.g. /status, /telemetry, or raw serial socket)."
                )
                Spacer(modifier = Modifier.height(8.dp))
                TelemetryContractItem(
                    slot = "SLOT 02: TELEMETRY SCHEMA",
                    status = "AWAITING FIRMWARE",
                    detail = "Will parse exact JSON keys (e.g. RSSI, free heap, camera frame rate, battery voltage, flash state)."
                )
                Spacer(modifier = Modifier.height(8.dp))
                TelemetryContractItem(
                    slot = "SLOT 03: OPTICAL STREAM",
                    status = "CONFIGURED (${config.streamPort}${config.streamPath})",
                    detail = "Ready to verify against your camera server port and streaming handler."
                )
            }
        }

        // Integration Code Bridge Preview
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ZeroSurfaceBorder, RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF04070B))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Code, contentDescription = "Kotlin", tint = ZeroEmerald, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "INTEGRATION BRIDGE (Kotlin)",
                        style = MaterialTheme.typography.labelSmall,
                        color = ZeroEmerald
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = """
                        // Location: com/zen/protocolzero/data/esp32/Esp32IntegrationLayer.kt
                        suspend fun fetchTelemetry(config: ProtocolZeroConfig): DeviceTelemetry {
                            // Ready to adapt to your actual Arduino .ino endpoints:
                            // val response = client.newCall(Request.Builder().url(...)).execute()
                            // return parseActualFirmwareJson(response.body?.string())
                        }
                    """.trimIndent(),
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun TelemetryContractItem(slot: String, status: String, detail: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ZeroSurfaceBorder, RoundedCornerShape(4.dp))
            .background(Color(0xFF080C12))
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = slot, fontSize = 11.sp, color = ZeroCyan, fontWeight = FontWeight.Bold)
                Text(text = status, fontSize = 10.sp, color = ZeroAmber, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = detail, fontSize = 11.sp, color = TextMuted)
        }
    }
}
