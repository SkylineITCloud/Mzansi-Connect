package com.mzansiconnect.app.data

// Mzansi Connect App - Supabase Repository
// SPDX-License-Identifier: Apache-2.0

import com.mzansiconnect.app.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order

object SupabaseRepo {

    private val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)
    }

    suspend fun getDevices(): List<Device> {
        val data = client.from("devices").select().decodeList<Map<String, Any?>>()
        return data.map { Device.fromMap(it) }
    }

    suspend fun getAlerts(): List<Alert> {
        val data = client.from("alerts").select {
            filter { eq("acknowledged", false) }
            order("created_at", Order.DESCENDING)
        }.decodeList<Map<String, Any?>>()
        return data.map { Alert.fromMap(it) }
    }

    suspend fun acknowledgeAlert(id: Long) {
        client.from("alerts").update(mapOf("acknowledged" to true)) {
            filter { eq("id", id) }
        }
    }

    suspend fun getSubscriptions(): List<Subscription> {
        val data = client.from("subscriptions")
            .select(Columns.raw("*, plan:subscription_plans(name)"))
            .decodeList<Map<String, Any?>>()
        return data.map { Subscription.fromMap(it) }
    }
}
