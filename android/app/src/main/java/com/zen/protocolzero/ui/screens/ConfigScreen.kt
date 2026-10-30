package com.zen.protocolzero.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WifiFind
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zen.protocolzero.model.ProtocolZeroConfig
import com.zen.protocolzero.ui.theme.TextMuted
import com.zen.protocolzero.ui.theme.TextPrimary
import com.zen.protocolzero.ui.theme.TextSecondary
import com.zen.protocolzero.ui.theme.ZeroBackground
import com.zen.protocolzero.ui.theme.ZeroCyan
import com.zen.protocolzero.ui.theme.ZeroEmerald
import com.zen.protocolzero.ui.theme.ZeroSurface
import com.zen.protocolzero.ui.theme.ZeroSurfaceBorder
import com.zen.protocolzero.ui.viewmodel.ProtocolZeroViewModel

@Composable
fun ConfigScreen(viewModel: ProtocolZeroViewModel) {
    val config by viewModel.config.collectAsState()

    var esp32Ip by remember(config.esp32Ip) { mutableStateOf(config.esp32Ip) }
    var streamPort by remember(config.streamPort) { mutableStateOf(config.streamPort.toString()) }
    var telemetryPort by remember(config.telemetryPort) { mutableStateOf(config.telemetryPort.toString()) }
    var streamPath by remember(config.streamPath) { mutableStateOf(config.streamPath) }
    var pollInterval by remember(config.pollIntervalMs) { mutableStateOf(config.pollIntervalMs.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZeroBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "ESP32 DEVICE CONFIGURATION",
                style = MaterialTheme.typography.headlineMedium,
                color = ZeroCyan,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "LOCAL WI-FI SUBNET PARAMETERS",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ZeroSurfaceBorder, RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = ZeroSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ConfigInput(
                    label = "ESP32-CAM IP ADDRESS",
                    value = esp32Ip,
                    onValueChange = { esp32Ip = it },
                    placeholder = "e.g., 192.168.4.1 or 192.168.1.150"
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        ConfigInput(
                            label = "STREAM PORT",
                            value = streamPort,
                            onValueChange = { streamPort = it },
                            placeholder = "81",
                            keyboardType = KeyboardType.Number
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        ConfigInput(
                            label = "HTTP PORT",
                            value = telemetryPort,
                            onValueChange = { telemetryPort = it },
                            placeholder = "80",
                            keyboardType = KeyboardType.Number
                        )
                    }
                }

                ConfigInput(
                    label = "CAMERA STREAM PATH",
                    value = streamPath,
                    onValueChange = { streamPath = it },
                    placeholder = "/stream"
                )

                ConfigInput(
                    label = "POLLING INTERVAL (MS)",
                    value = pollInterval,
                    onValueChange = { pollInterval = it },
                    placeholder = "2000",
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            val newConfig = config.copy(
                                esp32Ip = esp32Ip.trim(),
                                streamPort = streamPort.toIntOrNull() ?: 81,
                                telemetryPort = telemetryPort.toIntOrNull() ?: 80,
                                streamPath = streamPath.trim(),
                                pollIntervalMs = pollInterval.toLongOrNull() ?: 2000L
                            )
                            viewModel.updateConfig(newConfig)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = ZeroEmerald),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save", tint = ZeroBackground)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SAVE CONFIG", color = ZeroBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { viewModel.testEsp32Connection() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2836)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Icon(Icons.Default.WifiFind, contentDescription = "Test", tint = ZeroCyan)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("TEST LINK", color = ZeroCyan, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ConfigInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(text = label, fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFF475569), fontSize = 13.sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ZeroCyan,
                unfocusedBorderColor = ZeroSurfaceBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = ZeroEmerald
            ),
            shape = RoundedCornerShape(4.dp)
        )
    }
}
