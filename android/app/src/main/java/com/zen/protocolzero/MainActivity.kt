package com.zen.protocolzero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.zen.protocolzero.ui.screens.CameraStreamScreen
import com.zen.protocolzero.ui.screens.ConfigScreen
import com.zen.protocolzero.ui.screens.DashboardScreen
import com.zen.protocolzero.ui.screens.EventLogScreen
import com.zen.protocolzero.ui.screens.TelemetryScreen
import com.zen.protocolzero.ui.theme.ProtocolZeroTheme
import com.zen.protocolzero.ui.theme.TextMuted
import com.zen.protocolzero.ui.theme.ZeroBackground
import com.zen.protocolzero.ui.theme.ZeroCyan
import com.zen.protocolzero.ui.theme.ZeroEmerald
import com.zen.protocolzero.ui.theme.ZeroSurface
import com.zen.protocolzero.ui.theme.ZeroSurfaceBorder
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
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "COCKPIT", Icons.Default.Dashboard)
    object Camera : Screen("camera", "OPTICAL", Icons.Default.CameraAlt)
    object Telemetry : Screen("telemetry", "SENSORS", Icons.Default.Memory)
    object Logs : Screen("logs", "EVENTS", Icons.Default.ListAlt)
    object Config : Screen("config", "CONFIG", Icons.Default.Settings)
}

@Composable
fun MainAppContainer(viewModel: ProtocolZeroViewModel) {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Dashboard,
        Screen.Camera,
        Screen.Telemetry,
        Screen.Logs,
        Screen.Config
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            NavigationBar(
                containerColor = ZeroSurface,
                modifier = Modifier.border(width = 1.dp, color = ZeroSurfaceBorder)
            ) {
                items.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = {
                            Icon(
                                screen.icon,
                                contentDescription = screen.title,
                                tint = if (selected) ZeroEmerald else TextMuted
                            )
                        },
                        label = {
                            Text(
                                screen.title,
                                fontSize = 9.sp,
                                color = if (selected) ZeroEmerald else TextMuted
                            )
                        },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFF14241B)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier
                .fillMaxSize()
                .background(ZeroBackground)
                .padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToCamera = { navController.navigate(Screen.Camera.route) },
                    onNavigateToTelemetry = { navController.navigate(Screen.Telemetry.route) }
                )
            }
            composable(Screen.Camera.route) {
                CameraStreamScreen(viewModel = viewModel)
            }
            composable(Screen.Telemetry.route) {
                TelemetryScreen(viewModel = viewModel)
            }
            composable(Screen.Logs.route) {
                EventLogScreen(viewModel = viewModel)
            }
            composable(Screen.Config.route) {
                ConfigScreen(viewModel = viewModel)
            }
        }
    }
}
