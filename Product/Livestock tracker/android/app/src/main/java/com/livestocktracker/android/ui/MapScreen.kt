// LiveStock GPS Tracker — Map Screen
// SPDX-License-Identifier: Apache-2.0

package com.livestocktracker.android.ui

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.livestocktracker.android.data.Animal
import com.livestocktracker.android.data.PanicEvent
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen(
    animals: List<Animal>,
    panics: List<PanicEvent> = emptyList(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val panickedIds = remember(panics) { panics.map { it.animalId }.toSet() }

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(5.0)
                controller.setCenter(GeoPoint(-25.0, 134.0))
                mapView = this
            }
        },
        update = { mv ->
            mv.overlays.clear()
            animals.forEach { animal ->
                val lat = animal.latitude ?: return@forEach
                val lng = animal.longitude ?: return@forEach
                val isPanicking = panickedIds.contains(animal.id)
                val marker = Marker(mv)
                marker.position = GeoPoint(lat, lng)
                marker.title = if (isPanicking) "🚨 ${animal.tag}" else animal.tag
                marker.snippet = buildString {
                    append(animal.species ?: "")
                    append("\nSpeed: ${animal.speedKmh?.let { "%.1f km/h".format(it) } ?: "—"}")
                    append("\nBattery: ${animal.batteryV?.let { "%.2fV".format(it) } ?: "—"}")
                    if (isPanicking) {
                        val panic = panics.find { it.animalId == animal.id }
                        append("\n\n🚨 PANIC: ${panic?.trigger}")
                    }
                }
                if (isPanicking) {
                    marker.icon = org.osmdroid.views.overlay.Marker(
                        mv
                    ).icon // fallback — osmdroid uses default marker
                }
                mv.overlays.add(marker)
            }
            mv.invalidate()
        }
    )
}
