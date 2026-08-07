// LiveStock GPS Tracker — Animal List Screen
// SPDX-License-Identifier: Apache-2.0

package com.livestocktracker.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.livestocktracker.android.data.Animal
import com.livestocktracker.android.data.PanicEvent

@Composable
fun AnimalListScreen(
    animals: List<Animal>,
    panics: List<PanicEvent> = emptyList(),
    onAnimalClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val panickedIds = remember(panics) { panics.map { it.animalId }.toSet() }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF16213E))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(animals) { animal ->
            val isPanicking = panickedIds.contains(animal.id)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAnimalClick(animal.id) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isPanicking) Color(0xFF2A0A0A) else Color(0xFF1A1A2E)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (isPanicking) Color(0xFFE74C3C) else Color(0xFF0F3460)
                    )
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = animal.tag,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isPanicking) Color(0xFFE74C3C) else Color.White
                        )
                        if (isPanicking) {
                            Text(
                                text = "🚨 PANIC",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF0)
                            )
                        }
                    }
                    Text(
                        text = animal.species ?: "—",
                        fontSize = 11.sp,
                        color = Color(0xFFA0A0B8)
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = animal.speedKmh?.let { "%.1f km/h".format(it) } ?: "—",
                            fontSize = 11.sp,
                            color = Color(0xFF7A7A9A)
                        )
                        Text(
                            text = animal.batteryV?.let { "%.2fV".format(it) } ?: "—",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE94560)
                        )
                    }
                }
            }
        }
    }
}
