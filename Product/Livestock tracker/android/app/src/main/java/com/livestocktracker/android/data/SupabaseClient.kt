// LiveStock GPS Tracker — Supabase Repository
// SPDX-License-Identifier: Apache-2.0
//
// POPIA: All queries filtered through Supabase RLS (auth.uid()).
//        No PII cached locally without consent.

package com.livestocktracker.android.data

import com.livestocktracker.android.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.PostgresAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SupabaseRepo {
    // Keys come from BuildConfig (set in app/build.gradle.kts via local.properties or CI env)
    // ⚠ NEVER hardcode supabaseUrl or supabaseKey here.
    private val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
        install(Realtime)
    }

    suspend fun getLatestLocations(): List<Animal> = withContext(Dispatchers.IO) {
        client.from("latest_locations").select().decodeList<Animal>()
    }

    suspend fun getAnimalLocations(animalId: String, limit: Int = 50): List<Location> =
        withContext(Dispatchers.IO) {
            client.from("locations").select {
                filter("animal_id" eq animalId)
                order("recorded_at", ascending = false)
                limit(limit)
            }.decodeList<Location>()
        }

    suspend fun getAllAnimals(): List<Animal> = withContext(Dispatchers.IO) {
        client.from("animals").select { filter("active" eq true) }.decodeList<Animal>()
    }

    suspend fun getActivePanics(): List<PanicEvent> = withContext(Dispatchers.IO) {
        client.from("active_panics").select().decodeList<PanicEvent>()
    }

    fun subscribeToLocations(onInsert: () -> Unit) {
        client.from("locations").subscribe(
            action = PostgresAction.Insert,
            onEvent = { onInsert() }
        )
    }

    fun subscribeToPanics(onPanic: () -> Unit) {
        client.from("panic_events").subscribe(
            action = PostgresAction.Insert,
            onEvent = { onPanic() }
        )
    }
}
