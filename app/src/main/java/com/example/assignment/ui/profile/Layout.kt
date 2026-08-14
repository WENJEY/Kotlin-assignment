package com.example.assignment.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.assignment.ui.theme.appNavigationBarColor
import com.example.assignment.ui.utils.bottomProfileItems
import com.example.assignment.ui.utils.sideProfileItems
import com.example.assignment.ui.theme.LogoutRed
import com.example.assignment.ui.theme.NavSelected
import com.example.assignment.ui.theme.NavUnselected


// ==================== COMPACT (Phone) ====================

@Composable
fun ProfileCompactLayout(
    uiState: ProfileUiState,
    onEvent: (ProfileEvent) -> Unit,
    onAvatarSelected: (ByteArray) -> Unit,
    snackBarHostState: SnackbarHostState,
    horizontalPadding: Dp,
    avatarSize: Dp,
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
                    color = MaterialTheme.colorScheme.primary
                )
                return@Box
            }

            ProfileContent(
                uiState = uiState,
                onEvent = onEvent,
                onAvatarSelected = onAvatarSelected,
                horizontalPadding = horizontalPadding,
                avatarSize = avatarSize,
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
    onAvatarSelected: (ByteArray) -> Unit,
    snackBarHostState: SnackbarHostState,
    horizontalPadding: Dp,
    avatarSize : Dp,
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
                    color = MaterialTheme.colorScheme.primary
                )
                return@Box
            }

            ProfileContent(
                uiState = uiState,
                onEvent = onEvent,
                onAvatarSelected = onAvatarSelected,
                horizontalPadding = horizontalPadding,
                avatarSize = avatarSize,
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
    onAvatarSelected: (ByteArray) -> Unit,
    snackBarHostState: SnackbarHostState,
    horizontalPadding: Dp,
    avatarSize : Dp,
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
                    color = MaterialTheme.colorScheme.primary
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
                        onAvatarSelected = onAvatarSelected,
                        horizontalPadding = horizontalPadding,
                        avatarSize = avatarSize,
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
    onAvatarSelected: (ByteArray) -> Unit,
    horizontalPadding: Dp,
    isLandscape: Boolean,
    avatarSize : Dp,
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
            avatarSize = avatarSize,
            onAvatarSelected = onAvatarSelected
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
    onAvatarSelected: (ByteArray) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var imageToCrop by remember { mutableStateOf<Uri?>(null) }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageToCrop = uri
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(28.dp))

        ProfileAvatar(
            profileImageUrl = uiState.profileImageUrl,
            previewBytes = uiState.avatarPreviewBytes,
            size = avatarSize,
            onEditClick = { imagePicker.launch("image/*") }
        )

        Spacer(modifier = Modifier.height(18.dp))

        ProfileGreeting(username = uiState.username)
    }
    imageToCrop?.let { uri ->
        CropAvatarDialog(
            imageUri = uri,
            onDismiss = { imageToCrop = null },
            onCropConfirmed = { croppedUri ->
                imageToCrop = null
                context.contentResolver.openInputStream(croppedUri)?.use { stream ->
                    onAvatarSelected(stream.readBytes())
                }
            }
        )
    }
}

@Composable
fun ProfileAvatar(
    profileImageUrl: String?,
    size: Dp,
    modifier: Modifier = Modifier,
    previewBytes: ByteArray? = null,
    onEditClick: () -> Unit
) {
    val context = LocalContext.current
    // Prefer in-memory cropped bytes so the avatar never blanks after a successful upload.
    val imageModel = previewBytes ?: profileImageUrl
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.BottomEnd
    ) {

        if (imageModel != null && !(imageModel is String && imageModel.isBlank())) {
            key(previewBytes?.size, profileImageUrl) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageModel)
                        .memoryCacheKey(profileImageUrl)
                        .diskCacheKey(profileImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(width = 2.dp, color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                        .clickable { onEditClick() }
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(onClick = onEditClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(size * 0.5f)
                )
            }
        }

        Surface(
            modifier = Modifier
                .size(34.dp)
                .clickable(onClick = onEditClick),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 4.dp
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Edit",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(6.dp)
            )
        }
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
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = username.ifBlank { "User" },
            color = MaterialTheme.colorScheme.onBackground,
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
        ProfileMenuItem(
            icon = Icons.Filled.Palette,
            description = "appearance",
            title = "Appearance",
            onClick = { onEvent(ProfileEvent.AppearanceClicked) },
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
    tint: Color? = null,
    fontWeight: FontWeight = FontWeight.Normal
) {
    val contentColor = tint ?: MaterialTheme.colorScheme.onBackground
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
                tint = contentColor
            )

            Spacer(modifier = Modifier.width(24.dp))

            Text(
                text = title,
                modifier = Modifier.weight(1f),
                color = contentColor,
                fontWeight = fontWeight,
                style = MaterialTheme.typography.titleLarge
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
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
        containerColor = appNavigationBarColor()
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
                    selectedIconColor = NavSelected,
                    selectedTextColor = NavSelected,
                    indicatorColor = NavSelected.copy(alpha = 0.12f),
                    unselectedIconColor = NavUnselected,
                    unselectedTextColor = NavUnselected
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
            .background(appNavigationBarColor())
            .padding(vertical = 32.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        sideProfileItems.forEach { item ->
            val selected = selectedTab == item.tab
            val bgColor = if (selected) NavSelected else Color.Transparent
            val contentColor = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

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