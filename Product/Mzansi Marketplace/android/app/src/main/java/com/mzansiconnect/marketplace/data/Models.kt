package com.mzansiconnect.marketplace.data

// Mzansi Marketplace - Data Models
// SPDX-License-Identifier: Apache-2.0
// POPIA: Technician public profile data only; no sensitive PII in these models.

data class Job(
    val id: String,
    val title: String,
    val description: String?,
    val deviceType: String?,
    val town: String,
    val province: String?,
    val payRand: Double,
    val status: String
) {
    companion object {
        fun fromMap(m: Map<String, Any?>): Job = Job(
            id = m["id"] as? String ?: "",
            title = m["title"] as? String ?: "",
            description = m["description"] as? String,
            deviceType = m["device_type"] as? String,
            town = m["town"] as? String ?: "",
            province = m["province"] as? String,
            payRand = (m["pay_rand"] as? Number)?.toDouble() ?: 0.0,
            status = m["status"] as? String ?: "open"
        )
    }
}

data class Technician(
    val id: String,
    val fullName: String,
    val town: String?,
    val province: String?,
    val bio: String?,
    val skills: List<String>,
    val ratingAvg: Float,
    val jobsDone: Int,
    val verified: Boolean
) {
    companion object {
        fun fromMap(m: Map<String, Any?>): Technician = Technician(
            id = m["id"] as? String ?: "",
            fullName = m["full_name"] as? String ?: "",
            town = m["town"] as? String,
            province = m["province"] as? String,
            bio = m["bio"] as? String,
            skills = (m["skills"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            ratingAvg = (m["rating_avg"] as? Number)?.toFloat() ?: 0f,
            jobsDone = (m["jobs_done"] as? Number)?.toInt() ?: 0,
            verified = m["verified"] as? Boolean ?: false
        )
    }
}

data class Payout(
    val id: String,
    val amountRand: Double,
    val status: String,
    val createdAt: String
) {
    companion object {
        fun fromMap(m: Map<String, Any?>): Payout = Payout(
            id = m["id"] as? String ?: "",
            amountRand = (m["amount_rand"] as? Number)?.toDouble() ?: 0.0,
            status = m["status"] as? String ?: "pending",
            createdAt = m["created_at"] as? String ?: ""
        )
    }
}
