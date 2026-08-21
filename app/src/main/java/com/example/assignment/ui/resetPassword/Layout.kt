package com.example.assignment.ui.resetPassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
internal fun CompactLayout(
    uiState: ResetPasswordUiState,
    onEvent: (ResetPasswordEvent) -> Unit,
    logoSize: Dp,
    formMaxWidth: Dp,
    horizontalPadding: Dp,
    bottomPadding: Dp,
    maxHeight: Dp,
    centerContent: Boolean = false,
    isLandscape: Boolean = false
) {
    val scrollState = rememberScrollState()
    val isShortScreen = maxHeight < 700.dp
    val isVeryShortScreen = maxHeight < 500.dp
    val isKeyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val showBottomLogin = !uiState.isChangePassword && !isLandscape && !isKeyboardVisible

    val adaptiveLogoSize = when {
        isLandscape -> logoSize * 0.72f
        isVeryShortScreen -> logoSize * 0.55f
        isShortScreen -> logoSize * 0.70f
        else -> logoSize
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            ResetPasswordHeader(
                title = if (uiState.isChangePassword) {
                    "Change password"
                } else {
                    "Create new password"
                },
                onBack = { onEvent(ResetPasswordEvent.BackToLoginClicked) },
                backContentDescription = if (uiState.isChangePassword) {
                    "Back to profile"
                } else {
                    "Back to login"
                }
            )
        },
        bottomBar = {
            if (showBottomLogin) {
                ResetPasswordLoginRow(
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
                    top = if (isLandscape || isVeryShortScreen) 8.dp else 16.dp,
                    bottom = if (isLandscape) bottomPadding + 16.dp else 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (centerContent) Arrangement.Center else Arrangement.Top
        ) {
            ResetPasswordHero(
                logoSize = adaptiveLogoSize
            )
            Spacer(modifier = Modifier.height(if (isLandscape) 16.dp else 24.dp))
            ResetPasswordFormCard(
                uiState = uiState,
                onEvent = onEvent,
                modifier = Modifier.widthIn(
                    max = if (formMaxWidth == Dp.Unspecified) Dp.Infinity else formMaxWidth
                )
            )
            if (isLandscape && !uiState.isChangePassword) {
                Spacer(modifier = Modifier.height(8.dp))
                ResetPasswordLoginRow(
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
internal fun MediumLayout(
    uiState: ResetPasswordUiState,
    onEvent: (ResetPasswordEvent) -> Unit,
    logoSize: Dp,
    formMaxWidth: Dp,
    horizontalPadding: Dp,
    bottomPadding: Dp,
    centerContent: Boolean
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            ResetPasswordHeader(
                title = if (uiState.isChangePassword) {
                    "Change password"
                } else {
                    "Create new password"
                },
                onBack = { onEvent(ResetPasswordEvent.BackToLoginClicked) },
                backContentDescription = if (uiState.isChangePassword) {
                    "Back to profile"
                } else {
                    "Back to login"
                }
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
                ResetPasswordHero(
                    logoSize = logoSize
                )
                Spacer(modifier = Modifier.height(32.dp))
                ResetPasswordFormCard(
                    uiState = uiState,
                    onEvent = onEvent,
                    modifier = Modifier.widthIn(
                        max = if (formMaxWidth == Dp.Unspecified) Dp.Infinity else formMaxWidth
                    )
                )
                if (!uiState.isChangePassword) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ResetPasswordLoginRow(
                        onEvent = onEvent,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// ==================== EXPANDED (Large tablet / desktop) ====================

@Composable
internal fun ExpandedLayout(
    uiState: ResetPasswordUiState,
    onEvent: (ResetPasswordEvent) -> Unit,
    logoSize: Dp,
    formMaxWidth: Dp,
    horizontalPadding: Dp,
    bottomPadding: Dp,
    centerContent: Boolean
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            ResetPasswordHeader(
                title = if (uiState.isChangePassword) {
                    "Change password"
                } else {
                    "Create new password"
                },
                onBack = { onEvent(ResetPasswordEvent.BackToLoginClicked) },
                backContentDescription = if (uiState.isChangePassword) {
                    "Back to profile"
                } else {
                    "Back to login"
                }
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
                ResetPasswordHero(
                    logoSize = logoSize
                )
            }

            Column(
                modifier = Modifier.weight(0.62f),
                verticalArrangement = Arrangement.Center
            ) {
                ResetPasswordFormCard(
                    uiState = uiState,
                    onEvent = onEvent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(
                            max = if (formMaxWidth == Dp.Unspecified) Dp.Infinity else formMaxWidth
                        )
                )
                if (!uiState.isChangePassword) {
                    Spacer(modifier = Modifier.height(12.dp))
                    ResetPasswordLoginRow(
                        onEvent = onEvent,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun ResetPasswordHero(
    logoSize: Dp
) {
    Box(
        modifier = Modifier
            .size(logoSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Reset password",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(logoSize * 0.45f)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "New password must be different from last password",
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ResetPasswordFormCard(
    uiState: ResetPasswordUiState,
    onEvent: (ResetPasswordEvent) -> Unit,
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
            Text(
                text = "New Password",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            CompositionLocalProvider(
                LocalAutofillHighlightColor provides Color.Transparent
            ) {
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { onEvent(ResetPasswordEvent.PasswordChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "Enter new password",
                            color = TextGray
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = BluePrimary
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                onEvent(ResetPasswordEvent.TogglePasswordVisibility)
                            }
                        ) {
                            Icon(
                                imageVector = if (uiState.passwordVisible) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = if (uiState.passwordVisible) {
                                    "Hide password"
                                } else {
                                    "Show password"
                                },
                                tint = BluePrimary
                            )
                        }
                    },
                    visualTransformation = if (uiState.passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = BorderGray,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = BluePrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Confirm Password",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            CompositionLocalProvider(
                LocalAutofillHighlightColor provides Color.Transparent
            ) {
                OutlinedTextField(
                    value = uiState.confirmPassword,
                    onValueChange = {
                        onEvent(ResetPasswordEvent.ConfirmPasswordChanged(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "Confirm new password",
                            color = TextGray
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = BluePrimary
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                onEvent(ResetPasswordEvent.ToggleConfirmPasswordVisibility)
                            }
                        ) {
                            Icon(
                                imageVector = if (uiState.confirmPasswordVisible) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = if (uiState.confirmPasswordVisible) {
                                    "Hide password"
                                } else {
                                    "Show password"
                                },
                                tint = BluePrimary
                            )
                        }
                    },
                    visualTransformation = if (uiState.confirmPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = BorderGray,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = BluePrimary
                    )
                )
            }

            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = ErrorRed,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Password requirements",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            PasswordRequirement(text = "At least 6 characters")

            Spacer(modifier = Modifier.height(7.dp))

            PasswordRequirement(text = "Passwords must match")

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = { onEvent(ResetPasswordEvent.UpdatePasswordClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary,
                    contentColor = SurfaceWhite,
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
                    Text(
                        text = "Save password",
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
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ResetPasswordLoginRow(
    onEvent: (ResetPasswordEvent) -> Unit,
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
        Spacer(modifier = Modifier.width(4.dp))
        TextButton(
            onClick = { onEvent(ResetPasswordEvent.BackToLoginClicked) },
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = "Back to login",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PasswordRequirement(
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(BluePrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = SurfaceWhite,
                modifier = Modifier.size(14.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = text,
            color = TextGray,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ResetPasswordHeader(
    title: String,
    onBack: () -> Unit,
    backContentDescription: String
) {
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
                    contentDescription = backContentDescription,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = title,
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 21.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
