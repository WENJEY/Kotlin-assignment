package com.example.assignment.ui.forgotPassword

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.LocalAutofillHighlightColor
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import com.example.assignment.ui.theme.BluePrimary
import com.example.assignment.ui.theme.BorderGray
import com.example.assignment.ui.theme.ErrorRed
import com.example.assignment.ui.theme.SuccessGreen
import com.example.assignment.ui.theme.SurfaceWhite
import com.example.assignment.ui.theme.TextGray

@Composable
fun CompactLayout(
    uiState: ForgotPasswordUiState,
    onEvent: (ForgotPasswordEvent) -> Unit,
    logoSize: Dp,
    titleSize: TextUnit,
    formMaxWidth: Dp,
    horizontalPadding: Dp,
    bottomPadding: Dp,
    centerContent: Boolean
) {

    val scrollState = rememberScrollState()
    val isKeyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0


    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            ForgotPasswordHeader(
                onBack = { onEvent(ForgotPasswordEvent.BackToLoginClicked) }
            )
        },

        bottomBar = {
            if (!isKeyboardVisible) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        bottom = bottomPadding + 8.dp
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Remember your password?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )

                TextButton(
                    onClick = {
                        onEvent(
                            ForgotPasswordEvent.BackToLoginClicked
                        )
                    }
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
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    top = 12.dp,
                    bottom = 24.dp
                ),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (centerContent) {
                Arrangement.Center
            } else {
                Arrangement.Top
            }
        ) {
            // Lock Icon
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
                        modifier = Modifier.size(
                            logoSize * 0.43f
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            //Title
            Text(
                text = "Forgot Password?",
                fontSize = titleSize,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
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


            Spacer(modifier = Modifier.height(28.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
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
                ) {
                    EmailForm(uiState = uiState, onEvent = onEvent)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
                .height(64.dp)
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
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 21.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
