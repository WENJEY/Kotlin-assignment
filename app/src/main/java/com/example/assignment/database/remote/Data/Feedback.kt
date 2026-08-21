package com.example.assignment.database.remote.Data

data class Feedback(
    val rating: Int,
    val category: String,
    val message: String,
    val contactEmail: String?
)