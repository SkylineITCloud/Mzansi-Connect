package com.mzansiconnect.cloud.data

// Mzansi Cloud - Data Models
// SPDX-License-Identifier: Apache-2.0
// POPIA: Aggregate analytics; no PII in these models.

data class PlatformStats(
    val totalCustomers: Int,
    val totalDevices: Int,
    val onlineDevices: Int,
    val openAlerts: Int
) {
    companion object {
        fun fromMap(m: Map<String, Any?>): PlatformStats = PlatformStats(
            totalCustomers = (m["total_customers"] as? Number)?.toInt() ?: 0,
            totalDevices = (m["total_devices"] as? Number)?.toInt() ?: 0,
            onlineDevices = (m["online_devices"] as? Number)?.toInt() ?: 0,
            openAlerts = (m["open_alerts"] as? Number)?.toInt() ?: 0
        )
    }
}

data class Device(
    val id: String,
    val name: String?,
    val serial: String,
    val deviceType: String,
    val status: String,
    val batteryPct: Float?,
    val customerName: String?
) {
    companion object {
        fun fromMap(m: Map<String, Any?>): Device = Device(
            id = m["id"] as? String ?: "",
            name = m["name"] as? String,
            serial = m["serial"] as? String ?: "",
            deviceType = m["device_type"] as? String ?: "unknown",
            status = m["status"] as? String ?: "offline",
            batteryPct = (m["battery_pct"] as? Number)?.toFloat(),
            customerName = (m["customer"] as? Map<*, *>)?.get("full_name") as? String
        )
    }
}

data class CloudAlert(
    val id: Long,
    val severity: String,
    val title: String,
    val message: String?,
    val deviceName: String?,
    val createdAt: String
) {
    companion object {
        fun fromMap(m: Map<String, Any?>): CloudAlert = CloudAlert(
            id = (m["id"] as? Number)?.toLong() ?: 0,
            severity = m["severity"] as? String ?: "warning",
            title = m["title"] as? String ?: "Alert",
            message = m["message"] as? String,
            deviceName = (m["device"] as? Map<*, *>)?.get("name") as? String,
            createdAt = m["created_at"] as? String ?: ""
        )
    }
}
