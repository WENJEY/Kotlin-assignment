package com.example.assignment.ui.forgotPassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.LocalAutofillHighlightColor
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.ui.theme.BluePrimary
import com.example.assignment.ui.theme.ErrorRed
import com.example.assignment.ui.theme.SuccessGreen
import com.example.assignment.ui.theme.SurfaceWhite
import com.example.assignment.ui.theme.TextGray

// ==================== COMPACT (Phone) ====================

@Composable
fun CompactLayout(
    uiState: ForgotPasswordUiState,
    onEvent: (ForgotPasswordEvent) -> Unit,
    logoSize: Dp,
    titleSize: TextUnit,
    formMaxWidth: Dp,
    horizontalPadding: Dp,
    bottomPadding: Dp,
    centerContent: Boolean,
    isLandscape: Boolean = false
) {
    val scrollState = rememberScrollState()
    val isKeyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val showBottomLogin = !isLandscape && !isKeyboardVisible

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            ForgotPasswordHeader(
                onBack = { onEvent(ForgotPasswordEvent.BackToLoginClicked) }
            )
        },
        bottomBar = {
            if (showBottomLogin) {
                ForgotPasswordLoginRow(
                    onEvent = onEvent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = horizontalPadding,
                            end = horizontalPadding,
                            bottom = bottomPadding + 8.dp
                        )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(scrollState)
                .padding(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    top = if (isLandscape) 8.dp else 12.dp,
                    bottom = if (isLandscape) bottomPadding + 16.dp else 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (centerContent) Arrangement.Center else Arrangement.Top
        ) {
            ForgotPasswordHero(
                logoSize = logoSize,
                titleSize = titleSize,
                extraTitleLineSpacing = isLandscape
            )
            Spacer(modifier = Modifier.height(if (isLandscape) 16.dp else 28.dp))
            ForgotPasswordFormCard(
                uiState = uiState,
                onEvent = onEvent,
                modifier = Modifier.widthIn(
                    max = if (formMaxWidth == Dp.Unspecified) Dp.Infinity else formMaxWidth
                )
            )
            if (isLandscape) {
                Spacer(modifier = Modifier.height(8.dp))
                ForgotPasswordLoginRow(
                    onEvent = onEvent,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// ==================== MEDIUM (Small tablet) ====================

@Composable
fun MediumLayout(
    uiState: ForgotPasswordUiState,
    onEvent: (ForgotPasswordEvent) -> Unit,
    logoSize: Dp,
    titleSize: TextUnit,
    formMaxWidth: Dp,
    horizontalPadding: Dp,
    bottomPadding: Dp,
    centerContent: Boolean
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            ForgotPasswordHeader(
                onBack = { onEvent(ForgotPasswordEvent.BackToLoginClicked) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 640.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        top = if (centerContent) 32.dp else 16.dp,
                        bottom = bottomPadding + 24.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ForgotPasswordHero(
                    logoSize = logoSize,
                    titleSize = titleSize
                )
                Spacer(modifier = Modifier.height(32.dp))
                ForgotPasswordFormCard(
                    uiState = uiState,
                    onEvent = onEvent,
                    modifier = Modifier.widthIn(
                        max = if (formMaxWidth == Dp.Unspecified) Dp.Infinity else formMaxWidth
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                ForgotPasswordLoginRow(
                    onEvent = onEvent,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ==================== EXPANDED (Large tablet / desktop) ====================

@Composable
fun ExpandedLayout(
    uiState: ForgotPasswordUiState,
    onEvent: (ForgotPasswordEvent) -> Unit,
    logoSize: Dp,
    titleSize: TextUnit,
    formMaxWidth: Dp,
    horizontalPadding: Dp,
    bottomPadding: Dp,
    centerContent: Boolean
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            ForgotPasswordHeader(
                onBack = { onEvent(ForgotPasswordEvent.BackToLoginClicked) }
            )
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    top = 8.dp,
                    bottom = bottomPadding + 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(36.dp)
        ) {
            Column(
                modifier = Modifier.weight(0.38f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ForgotPasswordHero(
                    logoSize = logoSize,
                    titleSize = titleSize,
                    extraTitleLineSpacing = true
                )
            }

            Column(
                modifier = Modifier.weight(0.62f),
                verticalArrangement = Arrangement.Center
            ) {
                ForgotPasswordFormCard(
                    uiState = uiState,
                    onEvent = onEvent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(
                            max = if (formMaxWidth == Dp.Unspecified) Dp.Infinity else formMaxWidth
                        )
                )
                Spacer(modifier = Modifier.height(12.dp))
                ForgotPasswordLoginRow(
                    onEvent = onEvent,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ForgotPasswordHero(
    logoSize: Dp,
    titleSize: TextUnit,
    extraTitleLineSpacing: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(logoSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(logoSize * 0.72f)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Forgot password",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(logoSize * 0.43f)
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "Forgot Password?",
        modifier = Modifier.fillMaxWidth(),
        fontSize = titleSize,
        lineHeight = if (extraTitleLineSpacing) titleSize * 1.28f else titleSize * 1.15f,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = "Don't worry! Enter your email address\n" +
            "and we'll send you a verification code.",
        fontSize = 16.sp,
        lineHeight = 23.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun ForgotPasswordFormCard(
    uiState: ForgotPasswordUiState,
    onEvent: (ForgotPasswordEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 24.dp)
        ) {
            EmailForm(uiState = uiState, onEvent = onEvent)
        }
    }
}

@Composable
private fun ForgotPasswordLoginRow(
    onEvent: (ForgotPasswordEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Remember your password?",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
        TextButton(
            onClick = { onEvent(ForgotPasswordEvent.BackToLoginClicked) }
        ) {
            Text(
                text = "Back to login",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmailForm(
    uiState: ForgotPasswordUiState,
    onEvent: (ForgotPasswordEvent) -> Unit
) {
    FormHeader()

    Spacer(modifier = Modifier.height(22.dp))

    CompositionLocalProvider(
        LocalAutofillHighlightColor provides Color.Transparent
    ) {
        OutlinedTextField(
            value = uiState.email,
            onValueChange = {
                onEvent(ForgotPasswordEvent.EmailChanged(it))
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading,
            placeholder = {
                Text(text = "Email address", color = TextGray)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = TextGray
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            shape = RoundedCornerShape(14.dp),
            colors = formFieldColors(),
            isError = uiState.error != null
        )
    }

    FormError(uiState.error)

    Spacer(modifier = Modifier.height(18.dp))

    FormButton(
        isLoading = uiState.isLoading,
        enabled = !uiState.isLoading && uiState.resendCooldownSeconds == 0,
        icon = Icons.AutoMirrored.Filled.Send,
        text = if (uiState.resendCooldownSeconds > 0) {
            "Send again in ${uiState.resendCooldownSeconds}s"
        } else {
            "Send Verification Code"
        },
        onClick = { onEvent(ForgotPasswordEvent.SendVerificationCodeClicked) }
    )

    FormMessage(uiState.message)
}

@Composable
private fun FormHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(BluePrimary.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = BluePrimary,
                modifier = Modifier.size(27.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Enter your email address",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "We'll send a verification code to your email.",
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = TextGray
            )
        }
    }
}

@Composable
private fun FormButton(
    isLoading: Boolean,
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = !isLoading
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BluePrimary,
            disabledContainerColor = BluePrimary.copy(alpha = 0.6f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = SurfaceWhite,
                strokeWidth = 2.5.dp
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FormError(error: String?) {
    error?.let {
        Spacer(modifier = Modifier.height(7.dp))
        Text(
            text = it,
            color = ErrorRed,
            fontSize = 13.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun FormMessage(message: String?) {
    message?.let {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = it,
            color = SuccessGreen,
            fontSize = 13.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun formFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = BluePrimary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    cursorColor = BluePrimary
)

@Composable
private fun ForgotPasswordHeader(onBack: () -> Unit) {
    Surface(
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .heightIn(min = 64.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to login",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "Forgot Password",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 48.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 21.sp,
                lineHeight = 25.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}
