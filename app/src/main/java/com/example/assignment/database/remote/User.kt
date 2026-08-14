package com.example.assignment.database.remote

data class User(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val age: Int = 0,
    val phoneNumber: String = "",
    val gender: String = "",
    val profileImageUrl: String? = null
)