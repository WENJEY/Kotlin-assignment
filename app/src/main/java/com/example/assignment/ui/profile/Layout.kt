package com.example.assignment.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val TextOnGradient = Color.White
private val DividerOnGradient = Color.White.copy(alpha = 0.55f)
private val LogoutRed = Color(0xFFFF4D4F)
private val BottomSelected = Color(0xFF0077D9)
private val BottomUnselected = Color(0xFF8AA0B5)

@Composable
fun ProfileCompactLayout(
    uiState: ProfileUiState,
    onEvent: (ProfileEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    horizontalPadding: Dp,
    bottomPadding: Dp,
    isLandscape: Boolean,
    centerContent: Boolean,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            ProfileBottomNavigation(
                selectedTab = uiState.selectedBottomTab,
                onEvent = onEvent
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = bottomPadding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = TextOnGradient
                )
                return@Box
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(if (centerContent) 56.dp else 20.dp))

                ProfileHeader(
                    uiState = uiState,
                    avatarSize = if (isLandscape) 120.dp else 176.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(if (isLandscape) 24.dp else 42.dp))

                ProfileMenu(
                    onEvent = onEvent,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    uiState: ProfileUiState,
    avatarSize: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile",
            color = TextOnGradient,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(28.dp))

        ProfileAvatar(
            username = uiState.username,
            size = avatarSize
        )

        Spacer(modifier = Modifier.height(18.dp))

        ProfileGreeting(username = uiState.username)
    }
}

@Composable
private fun ProfileAvatar(
    username: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = username.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
            color = TextOnGradient,
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProfileGreeting(
    username: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome",
            color = TextOnGradient,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = username.ifBlank { "User" },
            color = TextOnGradient,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProfileMenu(
    onEvent: (ProfileEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ProfileMenuItem(
            iconText = "P",
            title = "User Profile",
            onClick = { onEvent(ProfileEvent.UserProfileClicked) }
        )
        ProfileMenuItem(
            iconText = "L",
            title = "Change Password",
            onClick = { onEvent(ProfileEvent.ChangePasswordClicked) }
        )
        ProfileMenuItem(
            iconText = "F",
            title = "Feedback",
            onClick = { onEvent(ProfileEvent.FeedbackClicked) }
        )
        ProfileMenuItem(
            iconText = "!",
            title = "Logout",
            tint = LogoutRed,
            onClick = { onEvent(ProfileEvent.LogoutClicked) }
        )
    }
}

@Composable
private fun ProfileMenuItem(
    iconText: String,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = TextOnGradient
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .clickable(
                    role = Role.Button,
                    onClick = onClick
                )
                .padding(horizontal = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = iconText,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.16f)),
                color = tint,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.width(24.dp))

            Text(
                text = title,
                modifier = Modifier.weight(1f),
                color = TextOnGradient,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = ">",
                color = tint.copy(alpha = 0.82f),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(color = DividerOnGradient)
    }
}

@Composable
private fun ProfileBottomNavigation(
    selectedTab: ProfileBottomTab,
    onEvent: (ProfileEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .navigationBarsPadding(),
        containerColor = Color.White
    ) {
        bottomItems.forEach { item ->
            NavigationBarItem(
                selected = selectedTab == item.tab,
                onClick = { onEvent(ProfileEvent.BottomTabSelected(item.tab)) },
                icon = {
                    Text(
                        text = item.iconText,
                        fontWeight = FontWeight.Bold
                    )
                },
                label = { Text(text = item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BottomSelected,
                    selectedTextColor = BottomSelected,
                    indicatorColor = BottomSelected.copy(alpha = 0.12f),
                    unselectedIconColor = BottomUnselected,
                    unselectedTextColor = BottomUnselected
                )
            )
        }
    }
}

private data class BottomItem(
    val tab: ProfileBottomTab,
    val label: String,
    val iconText: String
)

private val bottomItems = listOf(
    BottomItem(ProfileBottomTab.Home, "Home", "H"),
    BottomItem(ProfileBottomTab.Profile, "Profile", "P")
)

