package com.mzansiconnect.app

// Mzansi Connect App - Main Activity
// SPDX-License-Identifier: Apache-2.0
//
// POPIA: No personal information stored on device.
// ECTA:  Electronic transactions via server API.

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mzansiconnect.app.data.Alert
import com.mzansiconnect.app.data.Device
import com.mzansiconnect.app.data.SupabaseRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val SaGreen = Color(0xFF007A4B)
private val SaGold = Color(0xFFFFB612)
private val SaRed = Color(0xFFDE3831)
private val Dark = Color(0xFF0A0A0A)
private val Panel = Color(0xFF1B1B1B)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(primary = SaGreen, secondary = SaGold)) {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    var devices by remember { mutableStateOf<List<Device>>(emptyList()) }
    var alerts by remember { mutableStateOf<List<Alert>>(emptyList()) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (true) {
            try {
                devices = SupabaseRepo.getDevices()
                alerts = SupabaseRepo.getAlerts()
            } catch (_: Exception) { }
            delay(15_000)
        }
    }

    Scaffold(
        containerColor = Dark,
        topBar = {
            TopAppBar(
                title = { Text("Mzansi Connect", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF141414))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF141414)) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Phone, contentDescription = "Devices") },
                    label = { Text("Devices") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        if (alerts.isNotEmpty()) {
                            BadgedBox(badge = { Badge { Text("${alerts.size}") } }) {
                                Icon(Icons.Default.Warning, contentDescription = "Alerts")
                            }
                        } else {
                            Icon(Icons.Default.Notifications, contentDescription = "Alerts")
                        }
                    },
                    label = { Text("Alerts") }
                )
            }
        }
    ) { padding ->
        when (selectedTab) {
            0 -> DeviceList(devices, Modifier.padding(padding))
            1 -> AlertList(alerts, Modifier.padding(padding))
        }
    }
}

@Composable
fun DeviceList(devices: List<Device>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(devices) { device ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Panel),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF2E2E2E))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(device.name ?: device.serial, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${device.serial} - ${device.deviceType.replace("_", " ")}",
                            color = Color(0xFF9A9A9A), fontSize = 11.sp
                        )
                        Text(
                            text = device.batteryPct?.let { "Battery: ${it.toInt()}%" } ?: "Battery: --",
                            color = Color(0xFF9A9A9A), fontSize = 11.sp
                        )
                    }
                    Text(
                        text = device.status.replace("_", " ").uppercase(),
                        color = when (device.status) {
                            "online" -> SaGreen
                            "low_battery" -> SaGold
                            "error" -> SaRed
                            else -> Color(0xFF9A9A9A)
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AlertList(alerts: List<Alert>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(alerts) { alert ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Panel),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    when (alert.severity) {
                        "critical" -> SaRed
                        "warning" -> SaGold
                        else -> Color(0xFF002395)
                    }
                )
            ) {
                Column(Modifier.padding(14.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(alert.title, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(alert.createdAt.take(10), color = Color(0xFF9A9A9A), fontSize = 11.sp)
                    }
                    Text(
                        text = (alert.deviceName ?: "Device") + " - " + (alert.message ?: ""),
                        color = Color(0xFF9A9A9A), fontSize = 12.sp
                    )
                }
            }
        }
    }
}
