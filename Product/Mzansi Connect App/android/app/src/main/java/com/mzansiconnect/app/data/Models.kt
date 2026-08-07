package com.mzansiconnect.app.data

// Mzansi Connect App - Data Models
// SPDX-License-Identifier: Apache-2.0
// POPIA: Device metadata only; no PII in these models.

data class Device(
    val id: String,
    val serial: String,
    val name: String?,
    val deviceType: String,
    val firmwareVer: String?,
    val batteryPct: Float?,
    val status: String,
    val lat: Double? = null,
    val lon: Double? = null
) {
    companion object {
        fun fromMap(m: Map<String, Any?>): Device = Device(
            id = m["id"] as? String ?: "",
            serial = m["serial"] as? String ?: "",
            name = m["name"] as? String,
            deviceType = m["device_type"] as? String ?: "unknown",
            firmwareVer = m["firmware_ver"] as? String,
            batteryPct = (m["battery_pct"] as? Number)?.toFloat(),
            status = m["status"] as? String ?: "offline",
            lat = (m["lat"] as? Number)?.toDouble(),
            lon = (m["lon"] as? Number)?.toDouble()
        )
    }
}

data class Alert(
    val id: Long,
    val deviceId: String,
    val deviceName: String?,
    val alertType: String,
    val severity: String,
    val title: String,
    val message: String?,
    val acknowledged: Boolean,
    val createdAt: String
) {
    companion object {
        fun fromMap(m: Map<String, Any?>): Alert = Alert(
            id = (m["id"] as? Number)?.toLong() ?: 0,
            deviceId = m["device_id"] as? String ?: "",
            deviceName = m["device_name"] as? String,
            alertType = m["alert_type"] as? String ?: "",
            severity = m["severity"] as? String ?: "warning",
            title = m["title"] as? String ?: "Alert",
            message = m["message"] as? String,
            acknowledged = m["acknowledged"] as? Boolean ?: false,
            createdAt = m["created_at"] as? String ?: ""
        )
    }
}

data class Subscription(
    val id: String,
    val planName: String?,
    val status: String,
    val currentPeriodEnd: String?,
    val cancelAtPeriodEnd: Boolean
) {
    companion object {
        fun fromMap(m: Map<String, Any?>): Subscription = Subscription(
            id = m["id"] as? String ?: "",
            planName = (m["plan"] as? Map<*, *>)?.get("name") as? String,
            status = m["status"] as? String ?: "active",
            currentPeriodEnd = m["current_period_end"] as? String,
            cancelAtPeriodEnd = m["cancel_at_period_end"] as? Boolean ?: false
        )
    }
}
