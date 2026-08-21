package com.example.assignment.ui.appearance

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.assignment.ui.theme.ThemeMode

private data class ThemeOption(
    val mode: ThemeMode,
    val title: String,
    val description: String,
    val icon: ImageVector
)

private val themeOptions = listOf(
    ThemeOption(
        mode = ThemeMode.LIGHT,
        title = "Light Mode",
        description = "Always use the light appearance",
        icon = Icons.Default.LightMode
    ),
    ThemeOption(
        mode = ThemeMode.DARK,
        title = "Dark Mode",
        description = "Always use the dark appearance",
        icon = Icons.Default.DarkMode
    ),
    ThemeOption(
        mode = ThemeMode.SYSTEM,
        title = "System Default",
        description = "Match your device appearance setting",
        icon = Icons.Default.SettingsBrightness
    )
)

@Composable
fun AppearanceLayout(
    uiState: AppearanceUiState,
    windowSize: WindowWidthSizeClass,
    snackbarHostState: SnackbarHostState,
    onEvent: (AppearanceEvent) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isLandscape = maxWidth > maxHeight
        when (windowSize) {
            WindowWidthSizeClass.Compact -> AppearanceCompactLayout(
                uiState = uiState,
                snackbarHostState = snackbarHostState,
                onEvent = onEvent,
                isLandscape = isLandscape
            )
            WindowWidthSizeClass.Medium -> AppearanceMediumLayout(
                uiState = uiState,
                snackbarHostState = snackbarHostState,
                onEvent = onEvent
            )
            else -> AppearanceExpandedLayout(
                uiState = uiState,
                snackbarHostState = snackbarHostState,
                onEvent = onEvent
            )
        }
    }
}

// ==================== COMPACT (Phone) ====================

@Composable
private fun AppearanceCompactLayout(
    uiState: AppearanceUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (AppearanceEvent) -> Unit,
    isLandscape: Boolean
) {
    AppearanceFrame(
        snackbarHostState = snackbarHostState,
        onEvent = onEvent
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(
                    horizontal = 20.dp,
                    vertical = if (isLandscape) 16.dp else 28.dp
                ),
            verticalArrangement = Arrangement.spacedBy(if (isLandscape) 12.dp else 16.dp)
        ) {
            AppearanceIntro()
            AppearanceOptions(uiState = uiState, onEvent = onEvent)
            AppearanceSaveButton(uiState = uiState, onEvent = onEvent)
        }
    }
}

// ==================== MEDIUM (Small tablet) ====================

@Composable
private fun AppearanceMediumLayout(
    uiState: AppearanceUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (AppearanceEvent) -> Unit
) {
    AppearanceFrame(
        snackbarHostState = snackbarHostState,
        onEvent = onEvent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 640.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 48.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppearanceIntro()
                AppearanceOptions(uiState = uiState, onEvent = onEvent)
                AppearanceSaveButton(uiState = uiState, onEvent = onEvent)
            }
        }
    }
}

// ==================== EXPANDED (Large tablet / desktop) ====================

@Composable
private fun AppearanceExpandedLayout(
    uiState: AppearanceUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (AppearanceEvent) -> Unit
) {
    AppearanceFrame(
        snackbarHostState = snackbarHostState,
        onEvent = onEvent
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 64.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(36.dp)
        ) {
            Column(
                modifier = Modifier.weight(0.38f),
                verticalArrangement = Arrangement.Center
            ) {
                AppearanceIntro()
            }
            Column(
                modifier = Modifier.weight(0.62f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppearanceOptions(uiState = uiState, onEvent = onEvent)
                AppearanceSaveButton(uiState = uiState, onEvent = onEvent)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppearanceFrame(
    snackbarHostState: SnackbarHostState,
    onEvent: (AppearanceEvent) -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Appearance Settings",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onEvent(AppearanceEvent.BackClicked) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            content()
        }
    }
}

@Composable
private fun AppearanceIntro() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Choose your theme",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Select how the app should look. Your choice is saved on this device.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AppearanceOptions(
    uiState: AppearanceUiState,
    onEvent: (AppearanceEvent) -> Unit
) {
    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(36.dp))
        }
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        themeOptions.forEach { option ->
            ThemeOptionCard(
                option = option,
                selected = uiState.selectedMode == option.mode,
                onClick = { onEvent(AppearanceEvent.ThemeSelected(option.mode)) }
            )
        }
    }
}

@Composable
private fun AppearanceSaveButton(
    uiState: AppearanceUiState,
    onEvent: (AppearanceEvent) -> Unit
) {
    if (uiState.isLoading) return

    Button(
        onClick = { onEvent(AppearanceEvent.SaveClicked) },
        enabled = uiState.hasUnsavedChanges && !uiState.isSaving,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        if (uiState.isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = "Save",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ThemeOptionCard(
    option: ThemeOption,
    selected: Boolean,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "theme card color"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
        },
        label = "theme card border"
    )
    val elevation by animateDpAsState(
        targetValue = if (selected) 10.dp else 8.dp,
        label = "theme card elevation"
    )

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                tint = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(30.dp)
            )
            Spacer(Modifier.width(18.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = option.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = option.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
