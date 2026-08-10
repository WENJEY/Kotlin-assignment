package com.example.assignment.ui.forgotPassword

import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.ui.text.style.TextAlign
import com.example.assignment.ui.theme.BluePrimary
import com.example.assignment.ui.theme.BorderGray
import com.example.assignment.ui.theme.ErrorRed
import com.example.assignment.ui.theme.SuccessGreen
import com.example.assignment.ui.theme.TextDark
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

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = horizontalPadding,
                        top = 28.dp,
                        end = horizontalPadding
                    ),
                horizontalArrangement = Arrangement.Start
            ) {

                IconButton(
                    onClick = {
                        onEvent(
                            ForgotPasswordEvent.BackToLoginClicked
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
        },

        bottomBar = {
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
                    color = Color.White,
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
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
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
                    .background(
                        Color.White.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(logoSize * 0.72f)
                        .clip(CircleShape)
                        .background(
                            Color.White.copy(alpha = 0.10f)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Forgot password",
                        tint = Color.White,
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
                color = Color.White
            )


            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Don't worry! Enter your email address\n" +
                        "and we'll send you a link to reset your password.",
                fontSize = 16.sp,
                lineHeight = 23.sp,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.95f)
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
                color = Color.White,
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
                    // Email Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(
                                    BluePrimary.copy(
                                        alpha = 0.10f
                                    )
                                ),
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


                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "Enter your email address",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "We'll send a password reset link to your email.",
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = TextGray
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(22.dp))

                    // Email Field
                    OutlinedTextField(
                        value = uiState.email,

                        onValueChange = {
                            onEvent(
                                ForgotPasswordEvent.EmailChanged(it)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !uiState.isLoading,
                        placeholder = {
                            Text(
                                text = "Email address",
                                color = TextGray
                            )
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
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = BorderGray,
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark,
                            cursorColor = BluePrimary
                        ),
                        isError = uiState.error != null
                    )
                    // Error
                    uiState.error?.let { error ->

                        Spacer(
                            modifier = Modifier.height(7.dp)
                        )

                        Text(
                            text = error,
                            color = ErrorRed,
                            fontSize = 13.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }


                    Spacer(modifier = Modifier.height(18.dp))
                    // Send reset link
                    Button(
                        onClick = {
                            onEvent(
                                ForgotPasswordEvent.SendResetLinkClicked
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BluePrimary,
                            disabledContainerColor =
                                BluePrimary.copy(alpha = 0.6f)
                        )
                    ) {
                        if (uiState.isLoading) {

                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "Send Reset Link",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    // Success message
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

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}