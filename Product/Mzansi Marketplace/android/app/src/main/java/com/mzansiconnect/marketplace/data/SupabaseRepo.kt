package com.mzansiconnect.marketplace.data

// Mzansi Marketplace - Supabase Repository
// SPDX-License-Identifier: Apache-2.0

import com.mzansiconnect.marketplace.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

object SupabaseRepo {

    private val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)
    }

    suspend fun getJobs(): List<Job> {
        val data = client.from("jobs").select {
            order("created_at", Order.DESCENDING)
            limit(50)
        }.decodeList<Map<String, Any?>>()
        return data.map { Job.fromMap(it) }
    }

    suspend fun getTechnicians(): List<Technician> {
        val data = client.from("technicians").select {
            filter { eq("verified", true) }
            order("rating_avg", Order.DESCENDING)
            limit(50)
        }.decodeList<Map<String, Any?>>()
        return data.map { Technician.fromMap(it) }
    }

    suspend fun getPayouts(technicianId: String): List<Payout> {
        val data = client.from("payouts").select {
            filter { eq("technician_id", technicianId) }
            order("created_at", Order.DESCENDING)
        }.decodeList<Map<String, Any?>>()
        return data.map { Payout.fromMap(it) }
    }
}
