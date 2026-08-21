package com.example.assignment.ui.verifyCode

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.ui.theme.BluePrimary
import com.example.assignment.ui.theme.BorderGray
import com.example.assignment.ui.theme.ErrorRed
import com.example.assignment.ui.theme.SuccessGreen
import com.example.assignment.ui.theme.SurfaceWhite
import com.example.assignment.ui.theme.TextGray

// ==================== COMPACT (Phone) ====================

@Composable
fun CompactLayout(
    uiState: VerifyCodeUiState,
    onEvent: (VerifyCodeEvent) -> Unit,
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
    val showBottomResend = !isLandscape && !isKeyboardVisible

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            VerifyCodeHeader(onBack = { onEvent(VerifyCodeEvent.BackClicked) })
        },
        bottomBar = {
            if (showBottomResend) {
                VerifyCodeResendRow(
                    uiState = uiState,
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
            VerifyCodeHero(
                logoSize = logoSize,
                titleSize = titleSize,
                email = uiState.email
            )
            Spacer(modifier = Modifier.height(if (isLandscape) 16.dp else 28.dp))
            VerifyCodeFormCard(
                uiState = uiState,
                onEvent = onEvent,
                modifier = Modifier.widthIn(
                    max = if (formMaxWidth == Dp.Unspecified) Dp.Infinity else formMaxWidth
                )
            )
            if (isLandscape) {
                Spacer(modifier = Modifier.height(8.dp))
                VerifyCodeResendRow(
                    uiState = uiState,
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
    uiState: VerifyCodeUiState,
    onEvent: (VerifyCodeEvent) -> Unit,
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
            VerifyCodeHeader(onBack = { onEvent(VerifyCodeEvent.BackClicked) })
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
                VerifyCodeHero(
                    logoSize = logoSize,
                    titleSize = titleSize,
                    email = uiState.email
                )
                Spacer(modifier = Modifier.height(32.dp))
                VerifyCodeFormCard(
                    uiState = uiState,
                    onEvent = onEvent,
                    modifier = Modifier.widthIn(
                        max = if (formMaxWidth == Dp.Unspecified) Dp.Infinity else formMaxWidth
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                VerifyCodeResendRow(
                    uiState = uiState,
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
    uiState: VerifyCodeUiState,
    onEvent: (VerifyCodeEvent) -> Unit,
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
            VerifyCodeHeader(onBack = { onEvent(VerifyCodeEvent.BackClicked) })
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
                VerifyCodeHero(
                    logoSize = logoSize,
                    titleSize = titleSize,
                    email = uiState.email
                )
            }

            Column(
                modifier = Modifier.weight(0.62f),
                verticalArrangement = Arrangement.Center
            ) {
                VerifyCodeFormCard(
                    uiState = uiState,
                    onEvent = onEvent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(
                            max = if (formMaxWidth == Dp.Unspecified) Dp.Infinity else formMaxWidth
                        )
                )
                Spacer(modifier = Modifier.height(12.dp))
                VerifyCodeResendRow(
                    uiState = uiState,
                    onEvent = onEvent,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun VerifyCodeHero(
    logoSize: Dp,
    titleSize: TextUnit,
    email: String
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
                imageVector = Icons.Default.Password,
                contentDescription = "Verify code",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(logoSize * 0.43f)
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "Verify Code",
        fontSize = titleSize,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = "Enter the verification code we sent to\n$email",
        fontSize = 16.sp,
        lineHeight = 23.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun VerifyCodeFormCard(
    uiState: VerifyCodeUiState,
    onEvent: (VerifyCodeEvent) -> Unit,
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
                        imageVector = Icons.Default.Password,
                        contentDescription = null,
                        tint = BluePrimary,
                        modifier = Modifier.size(27.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Enter verification code",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Type the code from your email to continue.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            CompositionLocalProvider(
                LocalAutofillHighlightColor provides Color.Transparent
            ) {
                OutlinedTextField(
                    value = uiState.code,
                    onValueChange = { onEvent(VerifyCodeEvent.CodeChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !uiState.isLoading,
                    placeholder = {
                        Text(text = "6-digit code", color = TextGray)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Password,
                            contentDescription = null,
                            tint = TextGray
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = BorderGray,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = BluePrimary
                    ),
                    isError = uiState.error != null
                )
            }

            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = error,
                    color = ErrorRed,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = { onEvent(VerifyCodeEvent.VerifyClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary,
                    disabledContainerColor = BluePrimary.copy(alpha = 0.6f)
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = SurfaceWhite,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Verify Code",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            uiState.message?.let { message ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = message,
                    color = SuccessGreen,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun VerifyCodeResendRow(
    uiState: VerifyCodeUiState,
    onEvent: (VerifyCodeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Didn't get the code?",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
        TextButton(
            onClick = { onEvent(VerifyCodeEvent.ResendClicked) },
            enabled = !uiState.isLoading && uiState.resendCooldownSeconds == 0
        ) {
            Text(
                text = if (uiState.resendCooldownSeconds > 0) {
                    "Resend (${uiState.resendCooldownSeconds}s)"
                } else {
                    "Resend"
                },
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun VerifyCodeHeader(onBack: () -> Unit) {
    Surface(
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "Verify Code",
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 21.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
