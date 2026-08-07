package com.mzansiconnect.cloud.data

// Mzansi Cloud - Supabase Repository
// SPDX-License-Identifier: Apache-2.0

import com.mzansiconnect.cloud.BuildConfig
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

    suspend fun getStats(): PlatformStats {
        val data = client.from("platform_stats").select().decodeSingle<Map<String, Any?>>()
        return PlatformStats.fromMap(data)
    }

    suspend fun getDevices(): List<Device> {
        val data = client.from("devices").select(Columns.raw("*, customer:customers(full_name)")) {
            order("created_at", Order.DESCENDING)
            limit(100)
        }.decodeList<Map<String, Any?>>()
        return data.map { Device.fromMap(it) }
    }

    suspend fun getAlerts(): List<CloudAlert> {
        val data = client.from("alerts").select(Columns.raw("*, device:devices(name)")) {
            order("created_at", Order.DESCENDING)
            limit(50)
        }.decodeList<Map<String, Any?>>()
        return data.map { CloudAlert.fromMap(it) }
    }
}
