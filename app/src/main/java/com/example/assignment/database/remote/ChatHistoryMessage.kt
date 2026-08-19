package com.example.assignment.database.remote

data class ChatHistoryMessage(
    val id: String,
    val text: String,
    val isFromUser: Boolean
)
