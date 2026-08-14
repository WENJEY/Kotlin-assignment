package com.example.assignment.database.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClientProvider {

    val client: SupabaseClient by lazy {
        require(SupabaseSecrets.url.isNotBlank()) {
            "Supabase URL is missing. Create SupabaseSecrets.kt from the example file."
        }
        require(SupabaseSecrets.apiKey.isNotBlank()) {
            "Supabase publishable key is missing. Create SupabaseSecrets.kt from the example file."
        }

        createSupabaseClient(
            supabaseUrl = SupabaseSecrets.url,
            supabaseKey = SupabaseSecrets.apiKey
        ) {
            install(Auth.Companion) {
                scheme = "com.example.assignment"
                host = "reset-password"
            }
            install(Postgrest.Companion)
            install(Storage.Companion)
        }
    }
}