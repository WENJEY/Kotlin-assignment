package com.example.assignment.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.assignment.ui.utils.bottomProfileItems
import com.example.assignment.ui.utils.sideProfileItems

private val LogoutRed = Color(0xFFFF4D4F)
private val BottomSelected = Color(0xFF0077D9)
private val BottomUnselected = Color(0xFF8AA0B5)


// ==================== COMPACT (Phone) ====================

@Composable
fun ProfileCompactLayout(
    uiState: ProfileUiState,
    onEvent: (ProfileEvent) -> Unit,
    snackBarHostState: SnackbarHostState,
    horizontalPadding: Dp,
    bottomPadding: Dp,
    isLandscape: Boolean,
    centerContent: Boolean,
    modifier: Modifier = Modifier
) {

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        bottomBar = {
            ProfileBottomNavigation(
                selectedTab = uiState.selectedTab,
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
                    color = Color.White
                )
                return@Box
            }

            ProfileContent(
                uiState = uiState,
                onEvent = onEvent,
                horizontalPadding = horizontalPadding,
                isLandscape = isLandscape,
                centerContent = centerContent,
                windowSize = WindowWidthSizeClass.Compact
            )
        }
    }
}

// ==================== MEDIUM (Small Tablet / Landscape) ====================

@Composable
fun ProfileMediumLayout(
    uiState: ProfileUiState,
    onEvent: (ProfileEvent) -> Unit,
    snackBarHostState: SnackbarHostState,
    horizontalPadding: Dp,
    isLandscape: Boolean,
    centerContent: Boolean,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(modifier = modifier.fillMaxSize()) {
        // Compact rail on the left
        ProfileNavigationRail(
            selectedTab = uiState.selectedTab,
            onEvent = onEvent,
            showLabels = false, // icons only for medium
            modifier = Modifier.fillMaxHeight()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
                return@Box
            }

            ProfileContent(
                uiState = uiState,
                onEvent = onEvent,
                horizontalPadding = horizontalPadding,
                isLandscape = isLandscape,
                centerContent = centerContent,
                windowSize = WindowWidthSizeClass.Medium
            )
        }
    }
}

// ==================== EXPANDED (Large Tablet / Desktop) ====================

@Composable
fun ProfileExpandedLayout(
    uiState: ProfileUiState,
    onEvent: (ProfileEvent) -> Unit,
    snackBarHostState: SnackbarHostState,
    horizontalPadding: Dp,
    isLandscape: Boolean,
    centerContent: Boolean,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(modifier = modifier.fillMaxSize()) {
        // Wide rail with labels always visible
        ProfileNavigationRail(
            selectedTab = uiState.selectedTab,
            onEvent = onEvent,
            showLabels = true, // labels visible for expanded
            modifier = Modifier.fillMaxHeight()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)

        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
                return@Box
            }

            // Center content on large screens
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(modifier = Modifier.width(600.dp)) {
                    ProfileContent(
                        uiState = uiState,
                        onEvent = onEvent,
                        horizontalPadding = horizontalPadding,
                        isLandscape = isLandscape,
                        centerContent = centerContent,
                        windowSize = WindowWidthSizeClass.Expanded
                    )
                }
            }
        }
    }
}

// ==================== SHARED CONTENT ====================

@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    onEvent: (ProfileEvent) -> Unit,
    horizontalPadding: Dp,
    isLandscape: Boolean,
    centerContent: Boolean,
    windowSize : WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(if (centerContent) 56.dp else 20.dp))

        ProfileHeader(
            uiState = uiState,
            avatarSize = if (isLandscape) 104.dp else 160.dp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(if (isLandscape) 24.dp else 32.dp))

        ProfileMenu(
            onEvent = onEvent,
            modifier = Modifier.fillMaxWidth(),
            windowSize = windowSize
        )
    }
}

// ==================== PROFILE SECTIONS ====================

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
            color = Color.White,
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
            color = Color.White,
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
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = username.ifBlank { "User" },
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProfileMenu(
    onEvent: (ProfileEvent) -> Unit,
    modifier: Modifier = Modifier,
    windowSize: WindowWidthSizeClass
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ProfileMenuItem(
            icon = Icons.Filled.Person,
            description = "profile",
            title = "User Profile",
            onClick = { onEvent(ProfileEvent.UserProfileClicked) }
        )
        ProfileMenuItem(
            icon = Icons.Filled.Lock,
            description = "password",
            title = "Change Password",
            onClick = { onEvent(ProfileEvent.ChangePasswordClicked) },
        )
        ProfileMenuItem(
            icon = Icons.Filled.Feedback,
            description = "feedback",
            title = "Feedback",
            onClick = { onEvent(ProfileEvent.FeedbackClicked) },
        )
        if (windowSize == WindowWidthSizeClass.Compact) {
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                description = "logout",
                title = "Logout",
                onClick = { onEvent(ProfileEvent.LogoutClicked) },
                tint = LogoutRed,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    description: String,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(horizontal = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = tint
            )

            Spacer(modifier = Modifier.width(24.dp))

            Text(
                text = title,
                modifier = Modifier.weight(1f),
                color = tint,
                fontWeight = fontWeight,
                style = MaterialTheme.typography.titleLarge
            )
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.55f))
    }
}

// ==================== NAVIGATION COMPONENTS ====================

@Composable
private fun ProfileBottomNavigation(
    selectedTab: ProfileTab,
    onEvent: (ProfileEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = Color.White
    ) {
        bottomProfileItems.forEach { item ->
            NavigationBarItem(
                selected = selectedTab == item.tab,
                onClick = { onEvent(ProfileEvent.TabSelected(item.tab)) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.description,
                    )
                },
                label = { Text(text = item.iconText) },
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

@Composable
private fun ProfileNavigationRail(
    selectedTab: ProfileTab,
    onEvent: (ProfileEvent) -> Unit,
    showLabels: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(if (showLabels) 280.dp else 100.dp)
            .background(Color.White)
            .padding(vertical = 32.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        sideProfileItems.forEach { item ->
            val selected = selectedTab == item.tab
            val bgColor = if (selected) Color(0xFF0077D9) else Color.Transparent
            val contentColor = if (selected) Color.White else Color(0xFF5A6B7C)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(bgColor)
                    .clickable { onEvent(ProfileEvent.TabSelected(item.tab)) }
                    .padding(horizontal = if (showLabels) 20.dp else 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (showLabels) Arrangement.Start else Arrangement.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.description,
                    tint = if(item.icon == Icons.AutoMirrored.Filled.Logout) LogoutRed else contentColor,
                    modifier = Modifier.size(30.dp)
                )
                if (showLabels) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = item.iconText,
                        color = if (item.iconText == "Logout") LogoutRed else contentColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}