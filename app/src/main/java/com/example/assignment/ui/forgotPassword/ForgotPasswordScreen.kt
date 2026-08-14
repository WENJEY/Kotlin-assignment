package com.example.assignment.ui.forgotPassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment.navigation.ScreenRoutes
import com.example.assignment.ui.theme.pageBackgroundBrush
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass


@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    windowSize: WindowWidthSizeClass,
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(ForgotPasswordEvent.ScreenResumed)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }


    // ---------------------------------------------------------
    // HANDLE BACK NAVIGATION
    // ---------------------------------------------------------

    LaunchedEffect(uiState.navigateBack) {

        if (uiState.navigateBack) {

            navController.popBackStack()

            viewModel.onEvent(
                ForgotPasswordEvent.NavigationHandled
            )
        }
    }

    LaunchedEffect(uiState.navigateToVerifyCode) {
        if (uiState.navigateToVerifyCode) {
            navController.navigate(ScreenRoutes.VerifyCode.createRoute(uiState.email))
            viewModel.onEvent(ForgotPasswordEvent.NavigationHandled)
        }
    }


    // ---------------------------------------------------------
    // SCREEN CONTENT
    // ---------------------------------------------------------

    ForgotPasswordContent(
        uiState = uiState,
        windowSize = windowSize,
        onEvent = viewModel::onEvent
    )
}


@Composable
private fun ForgotPasswordContent(
    uiState: ForgotPasswordUiState,
    windowSize: WindowWidthSizeClass,
    onEvent: (ForgotPasswordEvent) -> Unit
) {

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val isLandscape = maxWidth > maxHeight
        val isNarrowPhone = maxWidth < 360.dp
        val isVeryTallScreen = maxHeight > 1000.dp

        val horizontalPadding = when (windowSize) {
            WindowWidthSizeClass.Compact -> {
                if (isNarrowPhone) {
                    16.dp
                } else {
                    24.dp
                }
            }
            WindowWidthSizeClass.Medium -> 48.dp
            WindowWidthSizeClass.Expanded -> 64.dp
            else -> 24.dp
        }

        val titleSize: TextUnit = when {
            maxWidth < 340.dp -> 27.sp
            windowSize == WindowWidthSizeClass.Compact -> 34.sp
            windowSize == WindowWidthSizeClass.Medium -> 40.sp
            else -> 36.sp
        }

        val logoSize = when (windowSize) {
            WindowWidthSizeClass.Compact -> {
                if (isNarrowPhone) {
                    108.dp
                } else {
                    140.dp
                }
            }
            WindowWidthSizeClass.Medium -> 144.dp
            WindowWidthSizeClass.Expanded -> 188.dp
            else -> 144.dp
        }

        val formMaxWidth = when (windowSize) {
            WindowWidthSizeClass.Compact -> {
                if (isLandscape) {
                    480.dp
                } else {
                    Dp.Unspecified
                }
            }
            WindowWidthSizeClass.Medium -> 520.dp
            WindowWidthSizeClass.Expanded -> 480.dp
            else -> Dp.Unspecified
        }

        val bottomPadding =
            WindowInsets.navigationBars
                .asPaddingValues()
                .calculateBottomPadding()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(pageBackgroundBrush())
        )

        when (windowSize) {

            WindowWidthSizeClass.Compact -> {

                CompactLayout(
                    uiState = uiState,
                    onEvent = onEvent,
                    logoSize = logoSize,
                    titleSize = titleSize,
                    formMaxWidth = formMaxWidth,
                    horizontalPadding = horizontalPadding,
                    bottomPadding = bottomPadding,
                    centerContent = isVeryTallScreen
                )
            }


            /**WindowWidthSizeClass.Medium -> {

                MediumLayout(
                    uiState = uiState,
                    onEvent = onEvent,
                    logoSize = logoSize,
                    titleSize = titleSize,
                    formMaxWidth = formMaxWidth,
                    horizontalPadding = horizontalPadding,
                    bottomPadding = bottomPadding,
                    centerContent = isVeryTallScreen
                )
            }


            WindowWidthSizeClass.Expanded -> {

                ExpandedLayout(
                    uiState = uiState,
                    onEvent = onEvent,
                    logoSize = logoSize,
                    titleSize = titleSize,
                    formMaxWidth = formMaxWidth,
                    horizontalPadding = horizontalPadding,
                    bottomPadding = bottomPadding,
                    centerContent = isVeryTallScreen
                )
            }
            **/
        }
    }
}