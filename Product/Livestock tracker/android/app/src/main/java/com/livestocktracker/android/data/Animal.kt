// LiveStock GPS Tracker — Animal data model
// SPDX-License-Identifier: Apache-2.0

package com.livestocktracker.android.data

data class Animal(
    val id: String,
    val tag: String,
    val name: String?,
    val species: String?,
    val breed: String?,
    val color: String?,
    val active: Boolean?,
    val latitude: Double?,
    val longitude: Double?,
    val speedKmh: Double?,
    val batteryV: Double?,
    val recordedAt: String?
)
