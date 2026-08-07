// LiveStock GPS Tracker — Android App
// SPDX-License-Identifier: Apache-2.0
//
// POPIA: No personal information stored on device.
// ECTA:  Electronic transactions via server API.
// ICASA: Device uses WiFi/BLE (2.4 GHz ISM, licence-exempt).

package com.livestocktracker.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.livestocktracker.android.data.Animal
import com.livestocktracker.android.data.PanicEvent
import com.livestocktracker.android.data.SupabaseRepo
import com.livestocktracker.android.ui.AnimalListScreen
import com.livestocktracker.android.ui.LiveStockTheme
import com.livestocktracker.android.ui.MapScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiveStockTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    var animals by remember { mutableStateOf<List<Animal>>(emptyList()) }
    var panics by remember { mutableStateOf<List<PanicEvent>>(emptyList()) }
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            try {
                animals = SupabaseRepo.getLatestLocations()
                panics = SupabaseRepo.getActivePanics()
            } catch (_: Exception) { }
            delay(15_000)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LiveStock Tracker") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF16213E)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF16213E)
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Map, contentDescription = "Map") },
                    label = { Text("Map") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        if (panics.isNotEmpty()) {
                            BadgedBox(badge = {
                                Badge { Text("${panics.size}") }
                            }) {
                                Icon(Icons.Default.Warning, contentDescription = "Panics")
                            }
                        } else {
                            Icon(Icons.Default.List, contentDescription = "Herd")
                        }
                    },
                    label = { Text("Herd") }
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Panic banner
            if (panics.isNotEmpty()) {
                PanicBanner(panics = panics)
            }
            when (selectedTab) {
                0 -> MapScreen(animals = animals, panics = panics, modifier = Modifier.fillMaxSize())
                1 -> AnimalListScreen(animals = animals, panics = panics, onAnimalClick = { })
            }
        }
    }
}

@Composable
fun PanicBanner(panics: List<PanicEvent>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2C0A0A))
            .padding(8.dp)
    ) {
        panics.forEach { panic ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF3A0A0A), RoundedCornerShape(6.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                    Column {
                        Text(
                            text = "🚨 ${panic.tag ?: "Unknown"}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE74C3C)
                        )
                        Text(
                            text = panic.trigger + (panic.subCollarId?.let { " · $it" } ?: ""),
                            fontSize = 11.sp,
                            color = Color(0xFFFF0)
                        )
                    }
                Text(
                    text = panic.severity.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (panic.severity == "critical") Color(0xFFFF0) else Color(0xFFE74C3C)
                )
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}
