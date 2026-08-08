package com.example.assignment.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.assignment.ui.profile.ProfileTab

internal data class ProfileItem(
    val tab: ProfileTab,
    val icon: ImageVector,
    val description : String,
    val iconText: String
)

internal val profileItems = listOf(
    ProfileItem(ProfileTab.Home, Icons.Filled.Home, "home", "Home"),
    ProfileItem(ProfileTab.Scanner, Icons.Filled.DocumentScanner, "scanner","Scanner"),
    ProfileItem(ProfileTab.ChatBox, Icons.AutoMirrored.Filled.Chat, "chatbox","ChatBox"),
    ProfileItem(ProfileTab.Profile, Icons.Filled.Person, "profile","Profile")
)