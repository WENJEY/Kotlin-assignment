package com.example.assignment.ui.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.R
import com.example.assignment.ui.login.CustomLoginButton
import com.example.assignment.ui.login.CustomPasswordField
import com.example.assignment.ui.login.CustomTextField
import com.example.assignment.ui.theme.CardShape
import com.example.assignment.ui.theme.LinkBlue
import com.example.assignment.ui.theme.MutedText
import com.example.assignment.ui.theme.SurfaceWhite
import com.example.assignment.ui.utils.ScreenHeight

@Composable
internal fun CompactLayout(
    uiState: RegisterUiState,
    onEvent: (RegisterEvent) -> Unit,
    logoSize: Dp,
    titleSize: TextUnit,
    formMaxWidth: Dp,
    stackHeader: Boolean,
    horizontalPadding: Dp,
    bottomPadding: Dp,
    maxHeight: Dp,
    maxWidth: Dp = Dp.Unspecified,
    isLandscape: Boolean = false,
    centerContent: Boolean = false,
) {
    val screenType = when {
        maxHeight < 500.dp -> ScreenHeight.Small
        maxHeight < 700.dp -> ScreenHeight.Medium
        maxHeight < 900.dp -> ScreenHeight.Large
        else -> ScreenHeight.ExtraLarge
    }

    val isShortScreen = maxHeight < 700.dp
    val isVeryShortScreen = maxHeight < 500.dp
    val scrollState = rememberScrollState()

    val verticalArrangement = when {
        centerContent -> Arrangement.Center
        isVeryShortScreen -> Arrangement.Top
        isShortScreen -> Arrangement.Top
        else -> Arrangement.Center
    }

    val adaptiveLogoSize = when {
        isVeryShortScreen -> logoSize * 0.45f
        isShortScreen -> logoSize * 0.60f
        else -> logoSize
    }

    val adaptiveTitleSize = when {
        isVeryShortScreen -> titleSize * 0.70f
        isShortScreen -> titleSize * 0.80f
        else -> titleSize
    }

    val headerSpacer = when {
        isVeryShortScreen -> 4.dp
        isShortScreen -> 8.dp
        centerContent -> 16.dp
        else -> 16.dp
    }

    val topPadding = when {
        isVeryShortScreen -> 4.dp
        isShortScreen -> 8.dp
        centerContent -> 20.dp
        else -> 16.dp
    }

    val contentMaxWidth = if (isLandscape && maxWidth > 600.dp) 560.dp else Dp.Unspecified

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            Row(
                modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = horizontalPadding,
                    end = horizontalPadding
                ),
                horizontalArrangement = Arrangement.Start)
            {
                IconButton(
                    onClick = {
                        onEvent(
                            RegisterEvent.LoginClicked
                        )
                    }
                ) {

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to login",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(scrollState),
            contentAlignment = Alignment.TopCenter
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (contentMaxWidth != Dp.Unspecified) Modifier.widthIn(max = contentMaxWidth) else Modifier)
                    .padding(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        top = topPadding,
                        bottom = bottomPadding + 16.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = verticalArrangement
            ) {
                LoginHeader(
                    logoSize = adaptiveLogoSize,
                    titleSize = adaptiveTitleSize,
                    stackContent = stackHeader
                )

                Spacer(modifier = Modifier.height(headerSpacer))

                RegisterForm(
                    uiState = uiState,
                    onEvent = onEvent,
                    screenHeight = screenType,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (formMaxWidth != Dp.Unspecified) Modifier.widthIn(max = formMaxWidth) else Modifier)
                )

                if (isShortScreen || centerContent) {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}


// ========== MEDIUM LAYOUT ==========

@Composable
internal fun MediumLayout(
    uiState: RegisterUiState,
    onEvent: (RegisterEvent) -> Unit,
    logoSize: Dp,
    titleSize: TextUnit,
    formMaxWidth: Dp,
    bottomPadding: Dp,
    maxHeight: Dp = Dp.Unspecified,
    maxWidth: Dp = Dp.Unspecified,
    centerContent: Boolean = false,  // NEW
) {
    val scrollState = rememberScrollState()
    val isShortScreen = maxHeight != Dp.Unspecified && maxHeight < 600.dp
    val isWide = maxWidth != Dp.Unspecified && maxWidth > 800.dp

    val topPadding = when {
        centerContent -> 48.dp
        isShortScreen -> 16.dp
        else -> 34.dp
    }

    val horizontalPadding = when {
        isShortScreen -> 24.dp
        isWide -> 64.dp
        else -> 48.dp
    }

    val spacerHeight = when {
        centerContent -> 40.dp
        isShortScreen -> 16.dp
        else -> 32.dp
    }

    // NEW: Center vertically when centerContent is true
    val verticalArrangement = if (centerContent) Arrangement.Center else Arrangement.Top
    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = horizontalPadding,
                        end = horizontalPadding
                    ),
                horizontalArrangement = Arrangement.Start)
            {
                IconButton(
                    onClick = {
                        onEvent(
                            RegisterEvent.LoginClicked
                        )
                    }
                ) {

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to login",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(scrollState)
                .padding(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    top = topPadding,
                    bottom = bottomPadding + 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = verticalArrangement
        ) {
            LoginHeader(
                logoSize = if (isShortScreen) logoSize * 0.85f else logoSize,
                titleSize = if (isShortScreen) titleSize * 0.9f else titleSize,
                stackContent = false
            )

            Spacer(modifier = Modifier.height(spacerHeight))

            RegisterForm(
                uiState = uiState,
                onEvent = onEvent,
                screenHeight = if (isShortScreen) ScreenHeight.Small else ScreenHeight.Large,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (formMaxWidth != Dp.Unspecified) Modifier.widthIn(max = formMaxWidth) else Modifier)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


// ========== EXPANDED LAYOUT ==========

@Composable
internal fun ExpandedLayout(
    uiState: RegisterUiState,
    onEvent: (RegisterEvent) -> Unit,
    logoSize: Dp,
    titleSize: TextUnit,
    formMaxWidth: Dp,
    bottomPadding: Dp,
    maxHeight: Dp = Dp.Unspecified,
    maxWidth: Dp = Dp.Unspecified,
    centerContent: Boolean = false,  // NEW
) {
    val scrollState = rememberScrollState()
    val isShortScreen = maxHeight != Dp.Unspecified && maxHeight < 500.dp
    val isVeryWide = maxWidth != Dp.Unspecified && maxWidth > 1200.dp

    val horizontalPadding = when {
        isShortScreen -> 32.dp
        isVeryWide -> 120.dp
        else -> 48.dp
    }

    val spacerWidth = when {
        isShortScreen -> 24.dp
        isVeryWide -> 80.dp
        else -> 40.dp
    }

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = horizontalPadding,
                        end = horizontalPadding
                    ),
                horizontalArrangement = Arrangement.Start)
            {
                IconButton(
                    onClick = {
                        onEvent(
                            RegisterEvent.LoginClicked
                        )
                    }
                ) {

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to login",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    top = if (isShortScreen) 16.dp else 34.dp,
                    bottom = bottomPadding + 24.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = if (centerContent) Alignment.Center else Alignment.CenterStart
            ) {
                LoginHeader(
                    logoSize = if (isShortScreen) logoSize * 0.8f else logoSize,
                    titleSize = if (isShortScreen) titleSize * 0.85f else titleSize,
                    stackContent = false
                )
            }

            Spacer(modifier = Modifier.width(spacerWidth))

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                RegisterForm(
                    uiState = uiState,
                    onEvent = onEvent,
                    screenHeight = if (isShortScreen) ScreenHeight.Small else ScreenHeight.Large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (formMaxWidth != Dp.Unspecified) Modifier.widthIn(max = formMaxWidth) else Modifier)
                )
            }
        }
    }
}

// ========== UI COMPONENTS (Unchanged from your original) ==========

@Composable
private fun RegisterForm(
    uiState: RegisterUiState,
    onEvent: (RegisterEvent) -> Unit,
    screenHeight: ScreenHeight,
    modifier: Modifier = Modifier
) {
    val cardPadding = when (screenHeight) {
        ScreenHeight.Small -> 16.dp
        ScreenHeight.Medium -> 20.dp
        ScreenHeight.Large -> 22.dp
        ScreenHeight.ExtraLarge -> 28.dp
    }
    val largeSpacer = when (screenHeight) {
        ScreenHeight.Small -> 12.dp
        ScreenHeight.Medium -> 16.dp
        ScreenHeight.Large -> 18.dp
        ScreenHeight.ExtraLarge -> 24.dp
    }
    val mediumSpacer = when (screenHeight) {
        ScreenHeight.Small -> 8.dp
        ScreenHeight.Medium -> 12.dp
        ScreenHeight.Large -> 14.dp
        ScreenHeight.ExtraLarge -> 18.dp
    }
    val smallSpacer = when (screenHeight) {
        ScreenHeight.Small -> 4.dp
        ScreenHeight.Medium -> 8.dp
        ScreenHeight.Large -> 10.dp
        ScreenHeight.ExtraLarge -> 12.dp
    }
    val tinySpacer = when (screenHeight) {
        ScreenHeight.Small -> 4.dp
        ScreenHeight.Medium -> 5.dp
        ScreenHeight.Large -> 6.dp
        ScreenHeight.ExtraLarge -> 8.dp
    }
    val buttonHeight = when (screenHeight) {
        ScreenHeight.Small -> 44.dp
        ScreenHeight.Medium -> 48.dp
        ScreenHeight.Large -> 50.dp
        ScreenHeight.ExtraLarge -> 56.dp
    }
    val titleSize = when (screenHeight) {
        ScreenHeight.Small -> 20.sp
        ScreenHeight.Medium -> 24.sp
        ScreenHeight.Large -> 26.sp
        ScreenHeight.ExtraLarge -> 30.sp
    }
    val bodySize = when (screenHeight) {
        ScreenHeight.Small -> 12.sp
        ScreenHeight.Medium -> 13.sp
        ScreenHeight.Large -> 14.sp
        ScreenHeight.ExtraLarge -> 16.sp
    }
    val buttonTextSize = when (screenHeight) {
        ScreenHeight.Small -> 15.sp
        ScreenHeight.Medium -> 16.sp
        ScreenHeight.Large -> 17.sp
        ScreenHeight.ExtraLarge -> 18.sp
    }

    val subtitleLineHeight = when (screenHeight) {
        ScreenHeight.Small -> 16.sp
        ScreenHeight.Medium -> 18.sp
        ScreenHeight.Large -> 20.sp
        ScreenHeight.ExtraLarge -> 24.sp
    }

    Box(
        modifier = modifier
            .wrapContentHeight()
            .clip(CardShape)
            .background(SurfaceWhite)
            .border(width = 1.dp, color = Color(0xFFE8EEE9), shape = CardShape)
            .padding(cardPadding)
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Text(
                text = "Sign Up",
                color = Color(0xFF0B6DB3),
                fontSize = titleSize,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(tinySpacer))
            Text(
                text = "Create your own account to get started",
                color = MutedText,
                fontSize = bodySize,
                lineHeight = subtitleLineHeight
            )
            Spacer(modifier = Modifier.height(largeSpacer))

            CustomTextField(
                label = "Username",
                value = uiState.username,
                onValueChange = { onEvent(RegisterEvent.UsernameChanged(it)) },
                placeholder = "Username",
                keyboardType = KeyboardType.Text,
                error = uiState.usernameError,
                bodySize = bodySize,
                buttonHeight = buttonHeight
            )

            Spacer(modifier = Modifier.height(mediumSpacer))

            CustomTextField(
                label = "Email",
                value = uiState.email,
                onValueChange = { onEvent(RegisterEvent.EmailChanged(it)) },
                placeholder = "abc123@example.com",
                keyboardType = KeyboardType.Email,
                error = uiState.emailError,
                bodySize = bodySize,
                buttonHeight = buttonHeight
            )
            Spacer(modifier = Modifier.height(mediumSpacer))

            CustomPasswordField(
                label = "Password",
                value = uiState.password,
                onValueChange = { onEvent(RegisterEvent.PasswordChanged(it)) },
                placeholder = "Password",
                error = uiState.passwordError,
                bodySize = bodySize,
                buttonHeight = buttonHeight
            )
            Spacer(modifier = Modifier.height(mediumSpacer))

            CustomPasswordField(
                label = "Confirm Password",
                value = uiState.confirmPassword,
                onValueChange = { onEvent(RegisterEvent.ConfirmPasswordChanged(it)) },
                placeholder = "Password",
                error = uiState.passwordError,
                bodySize = bodySize,
                buttonHeight = buttonHeight
            )

            Spacer(modifier = Modifier.height(largeSpacer))

            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = bodySize,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(tinySpacer))
            }

            CustomLoginButton(
                text = "Sign Up",
                isLoading = uiState.isLoading,
                buttonHeight = buttonHeight,
                bodySize = buttonTextSize,
                onClick = {
                    onEvent(RegisterEvent.SignUpClicked)
                }
            )
            Spacer(modifier = Modifier.height(tinySpacer))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = "Already have an account? ", color = MutedText, fontSize = bodySize)
                Text(
                    text = "Login",
                    color = LinkBlue,
                    fontSize = bodySize,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onEvent(RegisterEvent.LoginClicked) }
                )
            }
        }
    }
}

@Composable
private fun LoginHeader(
    logoSize: Dp,
    titleSize: TextUnit,
    stackContent: Boolean,
    modifier: Modifier = Modifier
) {
    val logo = @Composable {
        Box(
            modifier = Modifier
                .size(logoSize)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .border(1.dp, Color(0x45FFFFFF), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(logoSize * 1.1f)
            )
        }
    }

    if (stackContent) {
        Spacer(modifier = Modifier.width(32.dp))
        Row(modifier = modifier, verticalAlignment = Alignment.Top) {
            logo()
            HeaderTextBlock(titleSize = titleSize, titleAlignment = Alignment.CenterHorizontally)
        }
    } else {
        Column(
            modifier = modifier.padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            logo()
            Spacer(modifier = Modifier.height(18.dp))
            HeaderTextBlock(titleSize = titleSize, titleAlignment = Alignment.CenterHorizontally)
        }
    }
}

@Composable
private fun HeaderTextBlock(
    titleSize: TextUnit,
    titleAlignment: Alignment.Horizontal
) {
    Column(horizontalAlignment = titleAlignment) {
        Text(
            text = "Sign Up",
            fontSize = titleSize,
            fontFamily = FontFamily.Serif,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            lineHeight = titleSize * 1.05f
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}
