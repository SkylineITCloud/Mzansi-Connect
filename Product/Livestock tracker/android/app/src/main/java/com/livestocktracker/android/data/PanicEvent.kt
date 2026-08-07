// LiveStock GPS Tracker — Panic Event data model
// SPDX-License-Identifier: Apache-2.0

package com.livestocktracker.android.data

data class PanicEvent(
    val id: Long?,
    val animalId: String,
    val tag: String?,
    val name: String?,
    val species: String?,
    val subCollarId: String?,
    val trigger: String,
    val severity: String,
    val latitude: Double,
    val longitude: Double,
    val speedKmh: Double?,
    val batteryV: Double?,
    val message: String?,
    val createdAt: String?
)
