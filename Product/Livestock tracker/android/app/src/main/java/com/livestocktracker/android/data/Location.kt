// LiveStock GPS Tracker — Location data model
// SPDX-License-Identifier: Apache-2.0

package com.livestocktracker.android.data

data class Location(
    val id: Long?,
    val animalId: String,
    val latitude: Double,
    val longitude: Double,
    val speedKmh: Double?,
    val batteryV: Double?,
    val recordedAt: String?
)
