package com.example.assignment.ui.forgetPassword

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun ForgotPasswordScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onBackClick: () -> Unit,
    isLoading: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF63B8FF),
                        Color(0xFF1E88E5)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        ForgotPasswordCard(
            email = email,
            onEmailChange = onEmailChange,
            onSendClick = onSendClick,
            onBackClick = onBackClick,
            isLoading = isLoading
        )
    }
}
@Composable
private fun ForgotPasswordCard(
    email: String,
    onEmailChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onBackClick: () -> Unit,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(0.88f)
            .widthIn(max = 420.dp),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp,
        color = Color.White
    ) {

        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Forgot Password",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D47A1)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Enter your email to reset your password",
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            Spacer(Modifier.height(28.dp))

            CustomTextField(
                label = "",
                value = email,
                onValueChange = onEmailChange,
                placeholder = "Enter your email address",
                keyboardType = KeyboardType.Email,
                bodySize = 15.sp,
                buttonHeight = 56.dp
            )

            Spacer(Modifier.height(20.dp))

            CustomLoginButton(
                text = "Send Reset Link",
                isLoading = isLoading,
                buttonHeight = 54.dp,
                bodySize = 16.sp,
                onClick = onSendClick
            )

            Spacer(Modifier.height(18.dp))

            Text(
                text = "Back to Login",
                color = Color(0xFF0D47A1),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onBackClick)
            )
        }
    }
}