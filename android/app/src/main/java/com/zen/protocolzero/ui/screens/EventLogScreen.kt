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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.zen.protocolzero.model.EventSeverity
import com.zen.protocolzero.model.NetworkEvent
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EventLogScreen(viewModel: ProtocolZeroViewModel) {
    val events by viewModel.events.collectAsState()
    val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZeroBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "NETWORK EVENT LOG",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ZeroEmerald,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "REAL-TIME DIAGNOSTIC AUDIT TRAIL",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            if (events.isNotEmpty()) {
                Button(
                    onClick = { viewModel.clearEvents() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2836)),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear", tint = TextSecondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("CLEAR", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        if (events.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, ZeroSurfaceBorder, RoundedCornerShape(8.dp))
                    .background(ZeroSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "NO EVENTS RECORDED // LISTENING",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(events, key = { it.id }) { event ->
                    EventCard(event, timeFormat)
                }
            }
        }
    }
}

@Composable
fun EventCard(event: NetworkEvent, timeFormat: SimpleDateFormat) {
    val accentColor = when (event.severity) {
        EventSeverity.INFO -> ZeroCyan
        EventSeverity.SUCCESS -> ZeroEmerald
        EventSeverity.WARNING -> ZeroAmber
        EventSeverity.ALERT -> ZeroRed
        EventSeverity.BOOST -> Color(0xFFE040FB)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ZeroSurfaceBorder, RoundedCornerShape(6.dp))
            .background(ZeroSurface)
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = event.title,
                    fontSize = 12.sp,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = timeFormat.format(Date(event.timestamp)),
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.details,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}
