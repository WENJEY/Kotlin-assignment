package com.example.assignment.ui.scanner

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.assignment.ui.profile.ProfileTab
import com.example.assignment.ui.theme.BorderGray
import com.example.assignment.ui.theme.ErrorRed
import com.example.assignment.ui.theme.LogoutRed
import com.example.assignment.ui.theme.NavSelected
import com.example.assignment.ui.theme.NavUnselected
import com.example.assignment.ui.theme.ScannerHeroDocument
import com.example.assignment.ui.theme.ScannerHeroPlatform
import com.example.assignment.ui.theme.ScannerHistoryGreen
import com.example.assignment.ui.theme.ScannerImageBadge
import com.example.assignment.ui.theme.ScannerImportPurple
import com.example.assignment.ui.theme.ScannerOverlayScrim
import com.example.assignment.ui.theme.ScannerPdfRed
import com.example.assignment.ui.theme.ScannerScanBlue
import com.example.assignment.ui.theme.SuccessGreen
import com.example.assignment.ui.theme.SurfaceWhite
import com.example.assignment.ui.theme.appNavigationBarColor
import com.example.assignment.ui.utils.bottomProfileItems
import com.example.assignment.ui.utils.sideProfileItems
import java.io.File

private val HeroCardShape = RoundedCornerShape(28.dp)
private val ActionCardShape = RoundedCornerShape(22.dp)
private val DocumentCardShape = RoundedCornerShape(18.dp)

@Composable
fun ScannerLayout(
    uiState: ScannerUiState,
    windowSize: WindowWidthSizeClass,
    snackbarHostState: SnackbarHostState,
    onEvent: (ScannerEvent) -> Unit
) {
    BackHandler(enabled = uiState.destination != ScannerPage.Home) {
        onEvent(ScannerEvent.BackClicked)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (windowSize) {
            WindowWidthSizeClass.Compact -> ScannerCompactLayout(
                uiState = uiState,
                snackbarHostState = snackbarHostState,
                onEvent = onEvent
            )
            WindowWidthSizeClass.Medium -> ScannerRailLayout(
                uiState = uiState,
                snackbarHostState = snackbarHostState,
                onEvent = onEvent,
                showLabels = false
            )
            else -> ScannerRailLayout(
                uiState = uiState,
                snackbarHostState = snackbarHostState,
                onEvent = onEvent,
                showLabels = true
            )
        }

        if (uiState.isProcessing) {
            ProcessingOverlay(message = uiState.processingMessage)
        }

        if (uiState.confirmDeleteId != null) {
            AlertDialog(
                onDismissRequest = { onEvent(ScannerEvent.DeleteCanceled) },
                title = { Text("Delete document?") },
                text = { Text("This removes the file and its extracted text from this device.") },
                confirmButton = {
                    TextButton(onClick = { onEvent(ScannerEvent.DeleteConfirmed) }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onEvent(ScannerEvent.DeleteCanceled) }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun ScannerCompactLayout(
    uiState: ScannerUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (ScannerEvent) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            ScannerBottomNavigation(
                selectedTab = uiState.selectedTab,
                onTabSelected = { onEvent(ScannerEvent.TabSelected(it)) }
            )
        }
    ) { innerPadding ->
        ScannerContent(
            uiState = uiState,
            onEvent = onEvent,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        )
    }
}

@Composable
private fun ScannerRailLayout(
    uiState: ScannerUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (ScannerEvent) -> Unit,
    showLabels: Boolean
) {
    Row(modifier = Modifier.fillMaxSize()) {
        ScannerNavigationRail(
            selectedTab = uiState.selectedTab,
            onTabSelected = { onEvent(ScannerEvent.TabSelected(it)) },
            showLabels = showLabels
        )
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                contentAlignment = Alignment.TopCenter
            ) {
                ScannerContent(
                    uiState = uiState,
                    onEvent = onEvent,
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 28.dp, vertical = 20.dp)
                )
            }
        }
    }
}

@Composable
private fun ScannerContent(
    uiState: ScannerUiState,
    onEvent: (ScannerEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState.destination) {
        ScannerPage.History -> ScannerHistoryContent(
            uiState = uiState,
            onEvent = onEvent,
            modifier = modifier
        )
        ScannerPage.Detail -> {
            val document = uiState.selectedDocument
            if (document == null) {
                ScannerHomeContent(
                    uiState = uiState,
                    onEvent = onEvent,
                    modifier = modifier
                )
            } else {
                ScannerDetailContent(
                    document = document,
                    onEvent = onEvent,
                    modifier = modifier
                )
            }
        }
        ScannerPage.Home -> ScannerHomeContent(
            uiState = uiState,
            onEvent = onEvent,
            modifier = modifier
        )
    }
}

@Composable
private fun ScannerHomeContent(
    uiState: ScannerUiState,
    onEvent: (ScannerEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        GreetingHero(
            greeting = uiState.greeting,
            subtitle = uiState.subtitle
        )
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                title = "Scan",
                subtitle = "Document",
                icon = Icons.Filled.PhotoCamera,
                iconTint = ScannerScanBlue,
                onClick = { onEvent(ScannerEvent.ScanClicked) },
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                title = "Import",
                subtitle = "from Phone",
                icon = Icons.Filled.Folder,
                iconTint = ScannerImportPurple,
                onClick = { onEvent(ScannerEvent.ImportClicked) },
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                title = "History",
                subtitle = "Files",
                icon = Icons.Filled.History,
                iconTint = ScannerHistoryGreen,
                onClick = { onEvent(ScannerEvent.HistoryClicked) },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Documents",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "View all",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.clickable(
                    role = Role.Button,
                    onClick = { onEvent(ScannerEvent.ViewAllClicked) }
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (uiState.recentDocuments.isEmpty()) {
            EmptyDocumentsState(
                title = "No documents yet",
                subtitle = "Scan a physical page or import a photo or PDF from your phone."
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                uiState.recentDocuments.forEach { document ->
                    RecentDocumentCard(
                        document = document,
                        onClick = { onEvent(ScannerEvent.DocumentClicked(document.id)) },
                        onShare = { onEvent(ScannerEvent.ShareClicked(document.id)) },
                        onDelete = { onEvent(ScannerEvent.DeleteClicked(document.id)) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun GreetingHero(
    greeting: String,
    subtitle: String
) {
    val primary = MaterialTheme.colorScheme.primary
    val heroBrush = Brush.linearGradient(
        colors = listOf(primary, primary.copy(alpha = 0.82f))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(HeroCardShape)
            .background(heroBrush)
            .padding(start = 24.dp, end = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth(0.62f)
        ) {
            Text(
                text = greeting,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                lineHeight = 32.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.92f),
                fontSize = 15.sp
            )
        }
        ScanHeroIllustration(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(110.dp)
        )
    }
}

@Composable
private fun ScanHeroIllustration(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-10).dp)
                .size(width = 78.dp, height = 22.dp)
                .clip(RoundedCornerShape(50))
                .background(ScannerHeroPlatform)
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 4.dp, y = (-8).dp)
                .rotate(-12f)
                .size(width = 54.dp, height = 68.dp)
                .shadow(8.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(ScannerHeroDocument)
                .padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                repeat(5) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (index == 4) 0.55f else 1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(BorderGray)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(124.dp)
            .clickable(role = Role.Button, onClick = onClick),
        shape = ActionCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "$title $subtitle",
                tint = iconTint,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
internal fun RecentDocumentCard(
    document: ScannedDocument,
    onClick: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember(document.id) { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick),
        shape = DocumentCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DocumentThumbnail(
                type = document.type,
                thumbnailPath = document.thumbnailPath
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = document.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = document.metadata,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (document.validationStatus != DocumentValidationStatus.NONE) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = document.validationStatus.label(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when (document.validationStatus) {
                            DocumentValidationStatus.VALID -> SuccessGreen
                            DocumentValidationStatus.INVALID,
                            DocumentValidationStatus.UNREADABLE -> ErrorRed
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                if (document.legalStatus != DocumentLegalStatus.NONE &&
                    document.legalStatus != DocumentLegalStatus.SKIPPED
                ) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = document.legalStatus.label(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when (document.legalStatus) {
                            DocumentLegalStatus.COMPLIANT -> SuccessGreen
                            DocumentLegalStatus.NON_COMPLIANT -> ErrorRed
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Document options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Open") },
                        onClick = {
                            menuExpanded = false
                            onClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Share") },
                        onClick = {
                            menuExpanded = false
                            onShare()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DocumentThumbnail(
    type: DocumentType,
    thumbnailPath: String?
) {
    val context = LocalContext.current
    val isPdf = type == DocumentType.PDF
    val badgeColor = if (isPdf) ScannerPdfRed else ScannerImageBadge
    val thumbnailShape = RoundedCornerShape(12.dp)
    val previewFile = thumbnailPath?.let { File(it) }?.takeIf { it.exists() }

    Box(
        modifier = Modifier
            .size(width = 48.dp, height = 52.dp)
            .clip(thumbnailShape)
            .background(MaterialTheme.colorScheme.background)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.55f),
                shape = thumbnailShape
            )
    ) {
        if (previewFile != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(previewFile)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Column(
                modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (index == 2) 0.55f else 1f)
                            .height(3.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(BorderGray)
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(5.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(badgeColor)
                .padding(horizontal = 5.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (isPdf) "PDF" else "JPG",
                color = SurfaceWhite,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ProcessingOverlay(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScannerOverlayScrim),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CircularProgressIndicator()
                Text(
                    text = message,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun ScannerBottomNavigation(
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
private fun ScannerNavigationRail(
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
                        color = if (isLogout) LogoutRed else contentColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
