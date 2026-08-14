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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.LocalAutofillHighlightColor
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.example.assignment.ui.theme.BluePrimary
import com.example.assignment.ui.theme.BorderGray
import com.example.assignment.ui.theme.ErrorRed
import com.example.assignment.ui.theme.SuccessGreen
import com.example.assignment.ui.theme.SurfaceWhite
import com.example.assignment.ui.theme.TextGray

@Composable
internal fun CompactLayout(
    uiState: ResetPasswordUiState,
    onEvent: (ResetPasswordEvent) -> Unit,
    logoSize: Dp,
    titleSize: TextUnit,
    formMaxWidth: Dp,
    horizontalPadding: Dp,
    maxHeight: Dp,
    centerContent: Boolean = false
){
    val scrollState = rememberScrollState()

    val isShortScreen = maxHeight < 700.dp
    val isVeryShortScreen = maxHeight < 500.dp
    val keyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    val adaptiveLogoSize = when {
        isVeryShortScreen -> logoSize * 0.55f
        isShortScreen -> logoSize * 0.70f
        else -> logoSize
    }

    val adaptiveTitleSize = when {
        isVeryShortScreen -> titleSize * 0.75f
        isShortScreen -> titleSize * 0.85f
        else -> titleSize
    }

    val topPadding = when {
        isVeryShortScreen -> 4.dp
        isShortScreen -> 8.dp
        else -> 16.dp
    }

    val headerSpacer = when {
        isVeryShortScreen -> 6.dp
        isShortScreen -> 10.dp
        else -> 16.dp
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
        }

    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (keyboardVisible) {
                            Modifier.verticalScroll(scrollState)
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.TopCenter
            ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        top = topPadding,
                        bottom = 20.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Lock Icon
                Box(
                    modifier = Modifier
                        .size(adaptiveLogoSize)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Reset password",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(adaptiveLogoSize * 0.45f)
                    )
                }

                Spacer(modifier = Modifier.height(headerSpacer))

                Text(
                    text = "New password must be different from last password",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Form Card
                Surface(
                    modifier = Modifier.fillMaxWidth()
                        .widthIn(
                        max = if (formMaxWidth == Dp.Unspecified) {
                            Dp.Infinity
                        } else {
                            formMaxWidth
                        }
                    ),
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 22.dp,
                                vertical = 24.dp
                            )
                    ) { // New Password
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
                                            imageVector =
                                                if (uiState.passwordVisible) {
                                                    Icons.Default.VisibilityOff
                                                } else {
                                                    Icons.Default.Visibility
                                                },
                                            contentDescription =
                                                if (uiState.passwordVisible) {
                                                    "Hide password"
                                                } else {
                                                    "Show password"
                                                },
                                            tint = BluePrimary
                                        )
                                    }
                                },
                                visualTransformation =
                                    if (uiState.passwordVisible) {
                                        VisualTransformation.None
                                    } else {
                                        PasswordVisualTransformation()
                                    },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password
                                ),
                                shape = RoundedCornerShape(14.dp),
                                colors =
                                    OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BluePrimary,
                                        unfocusedBorderColor = BorderGray,
                                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        cursorColor = BluePrimary
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Confirm Password
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
                                    onEvent(
                                        ResetPasswordEvent.ConfirmPasswordChanged(
                                            it
                                        )
                                    )
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
                                        onClick = { onEvent(ResetPasswordEvent.ToggleConfirmPasswordVisibility) }
                                    ) {
                                        Icon(
                                            imageVector =
                                                if (
                                                    uiState.confirmPasswordVisible
                                                ) {
                                                    Icons.Default.VisibilityOff
                                                } else {
                                                    Icons.Default.Visibility
                                                },
                                            contentDescription =
                                                if (
                                                    uiState.confirmPasswordVisible
                                                ) {
                                                    "Hide password"
                                                } else {
                                                    "Show password"
                                                },
                                            tint = BluePrimary
                                        )
                                    }
                                },
                                visualTransformation =
                                    if (
                                        uiState.confirmPasswordVisible
                                    ) {
                                        VisualTransformation.None
                                    } else {
                                        PasswordVisualTransformation()
                                    },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType =
                                        KeyboardType.Password
                                ),
                                shape = RoundedCornerShape(14.dp),
                                colors =
                                    OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BluePrimary,
                                        unfocusedBorderColor = BorderGray,
                                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        cursorColor = BluePrimary
                                    )
                            )
                            // Error
                            uiState.error?.let { error ->

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = error,
                                    color = ErrorRed,
                                    fontSize = 13.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        // Password requirement
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

                        // Update Password button
                        Button(
                            onClick = {
                                onEvent(
                                    ResetPasswordEvent.UpdatePasswordClicked
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = !uiState.isLoading,
                            shape = RoundedCornerShape(50),
                            colors =
                                ButtonDefaults.buttonColors(
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

                        // Success Message
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
            }
            if (!uiState.isChangePassword) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Remember your password?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                TextButton(
                    onClick = {
                        onEvent(
                            ResetPasswordEvent.BackToLoginClicked
                        )
                    },
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