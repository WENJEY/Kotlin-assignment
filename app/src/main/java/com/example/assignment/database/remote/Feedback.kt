package com.example.assignment.database.remote

data class Feedback(
    val rating: Int,
    val category: String,
    val message: String,
    val contactEmail: String?
)