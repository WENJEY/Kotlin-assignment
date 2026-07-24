package com.example.assignment.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.assignment.ui.profile.ProfileBottomTab

internal data class BottomItem(
    val tab: ProfileBottomTab,
    val icon: ImageVector,
    val description : String,
    val iconText: String
)

internal val bottomItems = listOf(
    BottomItem(ProfileBottomTab.Home, Icons.Filled.Home, "home", "Home"),
    BottomItem(ProfileBottomTab.Scanner, Icons.Filled.DocumentScanner, "scanner","Scanner"),
    BottomItem(ProfileBottomTab.ChatBox, Icons.AutoMirrored.Filled.Chat, "chatbox","ChatBox"),
    BottomItem(ProfileBottomTab.Profile, Icons.Filled.Person, "profile","Profile")
)