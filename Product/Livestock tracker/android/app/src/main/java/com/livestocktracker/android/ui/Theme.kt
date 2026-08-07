// LiveStock GPS Tracker — Compose Theme
// SPDX-License-Identifier: Apache-2.0

package com.livestocktracker.android.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFFE94560),
    onPrimary = Color.White,
    surface = Color(0xFF16213E),
    onSurface = Color(0xFFEEEEEE),
    background = Color(0xFF1A1A2E),
    onBackground = Color(0xFFEEEEEE)
)

@Composable
fun LiveStockTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColors, content = content)
}
