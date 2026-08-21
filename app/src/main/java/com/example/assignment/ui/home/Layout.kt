package com.example.assignment.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.assignment.ui.profile.ProfileTab
import com.example.assignment.ui.theme.BrandBlue
import com.example.assignment.ui.theme.HomeChatAccent
import com.example.assignment.ui.theme.HomeHistoryAccent
import com.example.assignment.ui.theme.HomeImportAccent
import com.example.assignment.ui.theme.HomeScanAccent
import com.example.assignment.ui.theme.LogoutRed
import com.example.assignment.ui.theme.NavSelected
import com.example.assignment.ui.theme.NavUnselected
import com.example.assignment.ui.theme.SurfaceWhite
import com.example.assignment.ui.theme.appNavigationBarColor
import com.example.assignment.ui.utils.bottomProfileItems
import com.example.assignment.ui.utils.sideProfileItems

private val CardShape = RoundedCornerShape(16.dp)
private val ActionCardShape = RoundedCornerShape(20.dp)
private val ActionIconShape = RoundedCornerShape(16.dp)

@Composable
fun HomeLayout(
    uiState: HomeUiState,
    windowSize: WindowWidthSizeClass,
    snackbarHostState: SnackbarHostState,
    onEvent: (HomeEvent) -> Unit
) {
    when (windowSize) {
        WindowWidthSizeClass.Compact -> HomeCompactLayout(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onEvent = onEvent
        )
        WindowWidthSizeClass.Medium -> HomeRailLayout(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onEvent = onEvent,
            showLabels = false
        )
        else -> HomeRailLayout(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onEvent = onEvent,
            showLabels = true
        )
    }
}

@Composable
private fun HomeCompactLayout(
    uiState: HomeUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (HomeEvent) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            HomeBottomNavigation(
                selectedTab = uiState.selectedTab,
                onTabSelected = { onEvent(HomeEvent.TabSelected(it)) }
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isLandscape = maxWidth > maxHeight
            val scrollState = rememberScrollState()
            HomeContent(
                uiState = uiState,
                onEvent = onEvent,
                compactActions = true,
                fillRemaining = !isLandscape,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isLandscape) {
                            Modifier.verticalScroll(scrollState)
                        } else {
                            Modifier.fillMaxSize()
                        }
                    )
                    .padding(
                        horizontal = 20.dp,
                        vertical = if (isLandscape) 8.dp else 12.dp
                    )
            )
        }
    }
}

@Composable
private fun HomeRailLayout(
    uiState: HomeUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (HomeEvent) -> Unit,
    showLabels: Boolean
) {
    Row(modifier = Modifier.fillMaxSize()) {
        HomeNavigationRail(
            selectedTab = uiState.selectedTab,
            onTabSelected = { onEvent(HomeEvent.TabSelected(it)) },
            showLabels = showLabels
        )
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            val scroll = rememberScrollState()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .statusBarsPadding()
                    .verticalScroll(scroll)
                    .navigationBarsPadding(),
                contentAlignment = Alignment.TopCenter
            ) {
                HomeContent(
                    uiState = uiState,
                    onEvent = onEvent,
                    compactActions = false,
                    fillRemaining = false,
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    compactActions: Boolean,
    fillRemaining: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = if (fillRemaining) modifier.fillMaxSize() else modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(if (compactActions) 12.dp else 16.dp)
    ) {
        HomeGreetingHeader(
            username = uiState.username,
            profileImageUrl = uiState.profileImageUrl,
            onProfileClick = { onEvent(HomeEvent.ProfileClicked) }
        )

        Column {
            SectionTitle(text = "Quick Actions")
            Spacer(modifier = Modifier.height(10.dp))
            QuickActionsRow(
                compact = compactActions,
                onScan = { onEvent(HomeEvent.ScanDocumentClicked) },
                onImport = { onEvent(HomeEvent.ImportPdfClicked) },
                onNewChat = { onEvent(HomeEvent.NewChatClicked) },
                onHistory = { onEvent(HomeEvent.HistoryClicked) }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (fillRemaining) Modifier.weight(1f) else Modifier)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionTitle(
                    text = "Recent Chats",
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "View all",
                    color = BrandBlue,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable(
                        role = Role.Button,
                        onClick = { onEvent(HomeEvent.ViewAllChatsClicked) }
                    )
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (fillRemaining) Modifier.weight(1f) else Modifier.height(120.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = BrandBlue,
                        strokeWidth = 3.dp
                    )
                }
            } else if (uiState.recentChats.isEmpty()) {
                EmptyChatsCard(
                    onStartChat = { onEvent(HomeEvent.NewChatClicked) }
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.recentChats.forEach { chat ->
                        RecentChatCard(
                            chat = chat,
                            onClick = { onEvent(HomeEvent.ConversationClicked(chat.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeGreetingHeader(
    username: String,
    profileImageUrl: String?,
    onProfileClick: () -> Unit
) {
    val displayName = username.trim().ifBlank { "there" }.substringBefore(" ")
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        HomeAvatar(
            profileImageUrl = profileImageUrl,
            onClick = onProfileClick
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "Hi, $displayName 👋",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "How can I help you today?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HomeAvatar(
    profileImageUrl: String?,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(BrandBlue)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (!profileImageUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(profileImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Profile",
                tint = SurfaceWhite,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun QuickActionsRow(
    compact: Boolean,
    onScan: () -> Unit,
    onImport: () -> Unit,
    onNewChat: () -> Unit,
    onHistory: () -> Unit
) {
    val actions = listOf(
        QuickAction(
            title = "Scan Document",
            caption = "Scan any physical documents",
            icon = Icons.Filled.DocumentScanner,
            accent = HomeScanAccent,
            onClick = onScan
        ),
        QuickAction(
            title = "Import PDF",
            caption = "Import PDF from your phone",
            icon = Icons.Filled.Description,
            accent = HomeImportAccent,
            onClick = onImport
        ),
        QuickAction(
            title = "New Chat",
            caption = "Start a new conversation",
            icon = Icons.AutoMirrored.Filled.Chat,
            accent = HomeChatAccent,
            onClick = onNewChat
        ),
        QuickAction(
            title = "History",
            caption = "View your scanned documents",
            icon = Icons.Filled.History,
            accent = HomeHistoryAccent,
            onClick = onHistory
        )
    )

    val spacing = if (compact) 12.dp else 14.dp
    if (compact) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
            actions.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    rowItems.forEach { action ->
                        QuickActionCard(
                            action = action,
                            compact = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            actions.forEach { action ->
                QuickActionCard(
                    action = action,
                    compact = false,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private data class QuickAction(
    val title: String,
    val caption: String,
    val icon: ImageVector,
    val accent: Color,
    val onClick: () -> Unit
)

@Composable
private fun QuickActionCard(
    action: QuickAction,
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    val isLight = MaterialTheme.colorScheme.background.luminance() > 0.5f
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "quickActionScale"
    )
    val cardColor = if (isLight) {
        action.accent.copy(alpha = 0.10f)
    } else {
        action.accent.copy(alpha = 0.18f)
    }

    Card(
        modifier = modifier
            .height(if (compact) 118.dp else 132.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (isLight) 8.dp else 0.dp,
                shape = ActionCardShape,
                ambientColor = action.accent.copy(alpha = 0.18f),
                spotColor = action.accent.copy(alpha = 0.22f)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = action.accent.copy(alpha = 0.24f)),
                role = Role.Button,
                onClick = action.onClick
            ),
        shape = ActionCardShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            cardColor,
                            if (isLight) SurfaceWhite else MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(ActionIconShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                action.accent,
                                action.accent.copy(alpha = 0.82f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.title,
                    tint = SurfaceWhite,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = action.title,
                fontWeight = FontWeight.Bold,
                fontSize = if (compact) 14.sp else 15.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = action.caption,
                fontSize = 12.sp,
                lineHeight = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun RecentChatCard(
    chat: HomeRecentChat,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLight = MaterialTheme.colorScheme.background.luminance() > 0.5f
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLight) 1.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(BrandBlue.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chat.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = chat.preview,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (chat.timeLabel.isNotBlank()) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = chat.timeLabel,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyChatsCard(
    onStartChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLight = MaterialTheme.colorScheme.background.luminance() > 0.5f
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onStartChat),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLight) 1.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No conversations yet",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Start a new chat to see it here.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun HomeBottomNavigation(
    selectedTab: ProfileTab,
    onTabSelected: (ProfileTab) -> Unit
) {
    NavigationBar(containerColor = appNavigationBarColor()) {
        bottomProfileItems.forEach { item ->
            NavigationBarItem(
                selected = selectedTab == item.tab,
                onClick = { onTabSelected(item.tab) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.description
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
private fun HomeNavigationRail(
    selectedTab: ProfileTab,
    onTabSelected: (ProfileTab) -> Unit,
    showLabels: Boolean
) {
    Column(
        modifier = Modifier
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
            val isLogout = item.icon == Icons.AutoMirrored.Filled.Logout

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(bgColor)
                    .clickable { onTabSelected(item.tab) }
                    .padding(horizontal = if (showLabels) 20.dp else 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (showLabels) Arrangement.Start else Arrangement.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.description,
                    tint = if (isLogout) LogoutRed else contentColor,
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
