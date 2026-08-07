package com.mzansiconnect.marketplace

// Mzansi Marketplace - Main Activity (technician app)
// SPDX-License-Identifier: Apache-2.0
//
// POPIA: Public technician profiles only; no sensitive PII on device.
// ECTA:  Job applications and payouts via server API.

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mzansiconnect.marketplace.data.Job
import com.mzansiconnect.marketplace.data.Payout
import com.mzansiconnect.marketplace.data.SupabaseRepo
import com.mzansiconnect.marketplace.data.Technician
import kotlinx.coroutines.delay

private val SaGreen = Color(0xFF007A4B)
private val SaGold = Color(0xFFFFB612)
private val SaRed = Color(0xFFDE3831)
private val Dark = Color(0xFF0A0A0A)
private val Panel = Color(0xFF1B1B1B)
private val Line = Color(0xFF2E2E2E)

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
    var jobs by remember { mutableStateOf<List<Job>>(emptyList()) }
    var technicians by remember { mutableStateOf<List<Technician>>(emptyList()) }
    var payouts by remember { mutableStateOf<List<Payout>>(emptyList()) }
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            try {
                jobs = SupabaseRepo.getJobs()
                technicians = SupabaseRepo.getTechnicians()
            } catch (_: Exception) { }
            delay(30_000)
        }
    }

    Scaffold(
        containerColor = Dark,
        topBar = {
            TopAppBar(
                title = { Text("Mzansi Marketplace", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF141414))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF141414)) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.List, contentDescription = "Jobs") },
                    label = { Text("Jobs") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Technicians") },
                    label = { Text("Technicians") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Earnings") },
                    label = { Text("Earnings") }
                )
            }
        }
    ) { padding ->
        when (selectedTab) {
            0 -> JobList(jobs, Modifier.padding(padding))
            1 -> TechnicianList(technicians, Modifier.padding(padding))
            2 -> EarningsScreen(payouts, Modifier.padding(padding))
        }
    }
}

@Composable
fun JobList(jobs: List<Job>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(jobs) { job ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Panel),
                border = androidx.compose.foundation.BorderStroke(2.dp, Line)
            ) {
                Column(Modifier.fillMaxWidth().padding(14.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(job.title, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(
                            text = "R${job.payRand.toInt()}",
                            color = SaGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "📍 ${job.town}, ${job.province ?: ""}",
                        color = Color(0xFF9A9A9A), fontSize = 11.sp
                    )
                    job.description?.let {
                        Text(it, color = Color(0xFF9A9A9A), fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = job.status.replace("_", " ").uppercase(),
                        color = if (job.status == "open") SaGreen else SaGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TechnicianList(technicians: List<Technician>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(technicians) { tech ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Panel),
                border = androidx.compose.foundation.BorderStroke(2.dp, Line)
            ) {
                Column(Modifier.fillMaxWidth().padding(14.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(tech.fullName, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("★ ${tech.ratingAvg}", color = SaGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "📍 ${tech.town ?: ""} - ${tech.jobsDone} jobs done",
                        color = Color(0xFF9A9A9A), fontSize = 11.sp
                    )
                    if (tech.skills.isNotEmpty()) {
                        Text(
                            text = tech.skills.joinToString(" · "),
                            color = SaGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EarningsScreen(payouts: List<Payout>, modifier: Modifier = Modifier) {
    val total = payouts.sumOf { it.amountRand }
    val paid = payouts.filter { it.status == "paid" }.sumOf { it.amountRand }

    LazyColumn(modifier = modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Panel),
                border = androidx.compose.foundation.BorderStroke(2.dp, SaGold)
            ) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("TOTAL EARNINGS", color = Color(0xFF9A9A9A), fontSize = 11.sp)
                    Text(
                        text = "R${total.toInt()}",
                        color = SaGold,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Paid out: R${paid.toInt()}", color = Color(0xFF9A9A9A), fontSize = 12.sp)
                }
            }
        }
        items(payouts) { payout ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Panel),
                border = androidx.compose.foundation.BorderStroke(2.dp, Line)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("R${payout.amountRand.toInt()}", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(payout.createdAt.take(10), color = Color(0xFF9A9A9A), fontSize = 11.sp)
                    }
                    Text(
                        text = payout.status.uppercase(),
                        color = when (payout.status) {
                            "paid" -> SaGreen
                            "failed" -> SaRed
                            else -> SaGold
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
