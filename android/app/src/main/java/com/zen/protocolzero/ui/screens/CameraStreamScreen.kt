package com.zen.protocolzero.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zen.protocolzero.model.Esp32ConnectionState
import com.zen.protocolzero.ui.theme.TextMuted
import com.zen.protocolzero.ui.theme.TextPrimary
import com.zen.protocolzero.ui.theme.TextSecondary
import com.zen.protocolzero.ui.theme.ZeroBackground
import com.zen.protocolzero.ui.theme.ZeroCyan
import com.zen.protocolzero.ui.theme.ZeroEmerald
import com.zen.protocolzero.ui.theme.ZeroRed
import com.zen.protocolzero.ui.theme.ZeroSurface
import com.zen.protocolzero.ui.theme.ZeroSurfaceBorder
import com.zen.protocolzero.ui.viewmodel.ProtocolZeroViewModel

@Composable
fun CameraStreamScreen(
    viewModel: ProtocolZeroViewModel
) {
    val esp32State by viewModel.esp32State.collectAsState()
    val config by viewModel.config.collectAsState()
    val streamUrl = viewModel.getStreamUrl()
    var latestSnapshot by remember { mutableStateOf<Bitmap?>(null) }
    var hudCrosshairsEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZeroBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Stream Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ESP32-CAM OPTICAL HUD",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ZeroCyan,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "STREAM TARGET: $streamUrl",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Box(
                modifier = Modifier
                    .border(
                        1.dp,
                        if (esp32State is Esp32ConnectionState.Connected) ZeroEmerald else ZeroRed,
                        RoundedCornerShape(4.dp)
                    )
                    .background(ZeroSurface)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (esp32State is Esp32ConnectionState.Connected) "LIVE" else "STREAM OFFLINE",
                    fontSize = 10.sp,
                    color = if (esp32State is Esp32ConnectionState.Connected) ZeroEmerald else ZeroRed,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Tactical Viewport Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .background(Color(0xFF040609))
                .border(1.dp, ZeroSurfaceBorder, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (latestSnapshot != null) {
                Image(
                    bitmap = latestSnapshot!!.asImageBitmap(),
                    contentDescription = "Live Camera Frame",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Honest Offline / Not Connected state as instructed
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VideocamOff,
                        contentDescription = "Stream Offline",
                        tint = ZeroRed,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "DEVICE NOT CONNECTED",
                        color = ZeroRed,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "No active HTTP stream detected at $streamUrl",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.testEsp32Connection() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF161F2C)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = ZeroCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("RETRY STREAM LINK", fontSize = 11.sp, color = ZeroCyan)
                    }
                }
            }

            // HUD Crosshairs Canvas
            if (hudCrosshairsEnabled) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val cx = w / 2f
                    val cy = h / 2f

                    // Center crosshairs
                    drawLine(ZeroCyan.copy(alpha = 0.4f), Offset(cx - 30f, cy), Offset(cx + 30f, cy), strokeWidth = 1.5f)
                    drawLine(ZeroCyan.copy(alpha = 0.4f), Offset(cx, cy - 30f), Offset(cx, cy + 30f), strokeWidth = 1.5f)

                    // Corner brackets
                    val bracketLen = 20f
                    // Top-Left
                    drawLine(ZeroEmerald.copy(alpha = 0.5f), Offset(16f, 16f), Offset(16f + bracketLen, 16f), 2f)
                    drawLine(ZeroEmerald.copy(alpha = 0.5f), Offset(16f, 16f), Offset(16f, 16f + bracketLen), 2f)
                    // Top-Right
                    drawLine(ZeroEmerald.copy(alpha = 0.5f), Offset(w - 16f, 16f), Offset(w - 16f - bracketLen, 16f), 2f)
                    drawLine(ZeroEmerald.copy(alpha = 0.5f), Offset(w - 16f, 16f), Offset(w - 16f, 16f + bracketLen), 2f)
                    // Bottom-Left
                    drawLine(ZeroEmerald.copy(alpha = 0.5f), Offset(16f, h - 16f), Offset(16f + bracketLen, h - 16f), 2f)
                    drawLine(ZeroEmerald.copy(alpha = 0.5f), Offset(16f, h - 16f), Offset(16f, h - 16f - bracketLen), 2f)
                    // Bottom-Right
                    drawLine(ZeroEmerald.copy(alpha = 0.5f), Offset(w - 16f, h - 16f), Offset(w - 16f - bracketLen, h - 16f), 2f)
                    drawLine(ZeroEmerald.copy(alpha = 0.5f), Offset(w - 16f, h - 16f), Offset(w - 16f, h - 16f - bracketLen), 2f)
                }
            }
        }

        // Stream Controls & Diagnostics
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ZeroSurfaceBorder, RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = ZeroSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "OPTICAL STREAM DIAGNOSTICS",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DiagnosticRow(label = "PROTOCOL", value = "HTTP/MJPEG")
                    DiagnosticRow(label = "DEFAULT PORT", value = "${config.streamPort}")
                    DiagnosticRow(label = "FPS (LIVE)", value = if (esp32State is Esp32ConnectionState.Connected) "PROBING" else "0.0")
                }
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { hudCrosshairsEnabled = !hudCrosshairsEnabled },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2836)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = "HUD", modifier = Modifier.size(16.dp), tint = ZeroCyan)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (hudCrosshairsEnabled) "HUD: ON" else "HUD: OFF", fontSize = 11.sp, color = ZeroCyan)
                    }
                    Button(
                        onClick = {
                            viewModel.logEvent("OPTICAL_SNAP", "Snapshot trigger requested for $streamUrl", com.zen.protocolzero.model.EventSeverity.INFO)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2836)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Snap", modifier = Modifier.size(16.dp), tint = ZeroEmerald)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("GRAB FRAME", fontSize = 11.sp, color = ZeroEmerald)
                    }
                }
            }
        }
    }
}

@Composable
fun DiagnosticRow(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 9.sp, color = TextMuted)
        Text(text = value, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
    }
}
