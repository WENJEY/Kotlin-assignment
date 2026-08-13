package com.example.assignment.ui.login

import com.example.assignment.R
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.LocalAutofillHighlightColor
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.ui.theme.BodyText
import com.example.assignment.ui.theme.BrandBlue
import com.example.assignment.ui.theme.CardShape
import com.example.assignment.ui.theme.FieldShape
import com.example.assignment.ui.theme.FieldTint
import com.example.assignment.ui.theme.LinkBlue
import com.example.assignment.ui.theme.MutedText
import com.example.assignment.ui.theme.SurfaceWhite
import com.example.assignment.ui.utils.ScreenHeight

@Composable
internal fun CompactLayout(
    uiState: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
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
        maxHeight <= 600.dp -> ScreenHeight.Small
        maxHeight <= 800.dp -> ScreenHeight.Medium
        maxHeight <= 900.dp -> ScreenHeight.Large
        else -> ScreenHeight.ExtraLarge
    }

    val isShortScreen = maxHeight <= 800.dp
    val isVeryShortScreen = maxHeight <= 500.dp
    val keyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val scrollState = rememberScrollState()

    val contentModifier = if (keyboardVisible || isShortScreen || centerContent) {
        Modifier.verticalScroll(scrollState)
    } else {
        Modifier
    }

    // NEW: If centerContent is true, always center,Otherwise use adaptive logic
    val verticalArrangement = when {
        centerContent -> Arrangement.Center
        isVeryShortScreen -> Arrangement.Top
        isShortScreen -> Arrangement.Top
        else -> Arrangement.Center
    }

    val boxAlignment = when {
        centerContent -> Alignment.Center
        isShortScreen -> Alignment.TopCenter
        else -> Alignment.Center
    }

    val adaptiveLogoSize = when {
        isVeryShortScreen -> logoSize * 0.6f
        isShortScreen -> logoSize * 0.75f
        else -> logoSize
    }

    val adaptiveTitleSize = when {
        isVeryShortScreen -> titleSize * 0.7f
        isShortScreen -> titleSize * 0.85f
        else -> titleSize
    }

    val headerSpacer = when {
        isVeryShortScreen -> 8.dp
        isShortScreen -> 12.dp
        centerContent -> 24.dp
        else -> 20.dp
    }

    val topPadding = when {
        isVeryShortScreen -> 8.dp
        isShortScreen -> 16.dp
        centerContent -> 32.dp
        else -> 22.dp
    }

    val contentMaxWidth = if (isLandscape && maxWidth > 600.dp) 560.dp else Dp.Unspecified

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .then(contentModifier),
        contentAlignment = boxAlignment
    ) {
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

            LoginForm(
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

@Composable
internal fun MediumLayout(
    uiState: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    logoSize: Dp,
    titleSize: TextUnit,
    formMaxWidth: Dp,
    bottomPadding: Dp,
    maxHeight: Dp = Dp.Unspecified,
    maxWidth: Dp = Dp.Unspecified,
    centerContent: Boolean = false,
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
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

        LoginForm(
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

@Composable
internal fun ExpandedLayout(
    uiState: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    logoSize: Dp,
    titleSize: TextUnit,
    formMaxWidth: Dp,
    bottomPadding: Dp,
    maxHeight: Dp = Dp.Unspecified,
    maxWidth: Dp = Dp.Unspecified,
    centerContent: Boolean = false,
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

    // NEW: Center vertically when centerContent is true
    val verticalAlignment = if (centerContent) Alignment.CenterVertically else Alignment.CenterVertically

    Row(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = if (isShortScreen) 16.dp else 34.dp,
                bottom = bottomPadding + 24.dp
            ),
        verticalAlignment = verticalAlignment,
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
            LoginForm(
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



// UI COMPONENTS
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
                modifier = Modifier
                    .size(logoSize * 1.1f)
            )
        }
    }

    if (stackContent) {
        Spacer(modifier = Modifier.width(22.dp))
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
            text = "Welcome back",
            fontSize = titleSize,
            fontFamily = FontFamily.Serif,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            lineHeight = titleSize * 1.05f
        )
        Text(
            text = "Login to continue your work",
            fontSize = 15.sp,
            color = Color(0xFFD5E5DD),
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun LoginForm(
    uiState: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    screenHeight: ScreenHeight,
    modifier: Modifier = Modifier
) {
    // Dimension calculations based on screen height
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
    val forgotSize = when (screenHeight) {
        ScreenHeight.Small -> 11.sp
        ScreenHeight.Medium -> 12.sp
        ScreenHeight.Large -> 13.sp
        ScreenHeight.ExtraLarge -> 14.sp
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
                text = "Login",
                color = Color(0xFF0B6DB3),
                fontSize = titleSize,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(tinySpacer))
            Text(
                text = "Access your Government HR Consultation workspace",
                color = MutedText,
                fontSize = bodySize,
                lineHeight = subtitleLineHeight
            )
            Spacer(modifier = Modifier.height(largeSpacer))

            // Email field
            CustomTextField(
                label = "Email / Username",
                value = uiState.identifier,
                onValueChange = { onEvent(LoginEvent.IdentifierChanged(it)) },
                placeholder = "Email / Username",
                keyboardType = KeyboardType.Text,
                error = null,
                bodySize = bodySize,
                buttonHeight = buttonHeight
            )
            Spacer(modifier = Modifier.height(mediumSpacer))

            // Password field
            CustomPasswordField(
                label = "Password",
                value = uiState.password,
                onValueChange = { onEvent(LoginEvent.PasswordChanged(it)) },
                placeholder = "Password",
                error = null,
                bodySize = bodySize,
                buttonHeight = buttonHeight
            )
            Spacer(modifier = Modifier.height(smallSpacer))

            // Forgot password
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                val forgotInteractionSource = remember { MutableInteractionSource() }
                val isForgotPressed by forgotInteractionSource.collectIsPressedAsState()
                TextButton(
                    onClick = { onEvent(LoginEvent.ForgotPasswordClicked) },
                    interactionSource = forgotInteractionSource,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = LinkBlue
                    )
                ) {
                    Text(
                        text = "Forgot password?",
                        color = if (isForgotPressed) BrandBlue else LinkBlue,
                        fontSize = forgotSize,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(tinySpacer))

            // Global authentication error
            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = bodySize,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(tinySpacer))
            }

            // Login button
            CustomLoginButton(
                text = "Login",
                isLoading = uiState.isLoading,
                buttonHeight = buttonHeight,
                bodySize = buttonTextSize,
                onClick = {
                    onEvent(LoginEvent.LoginClicked)
                }
            )

            Spacer(modifier = Modifier.height(tinySpacer))

            // Sign up
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = "Don't have an account? ", color = MutedText, fontSize = bodySize)
                Text(
                    text = "Sign Up",
                    color = LinkBlue,
                    fontSize = bodySize,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onEvent(LoginEvent.SignUpClicked) }
                )
            }
        }
    }
}

@Composable
internal fun CustomTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    error: String? = null,
    bodySize: TextUnit,
    buttonHeight: Dp,
) {
    CompositionLocalProvider(
        LocalAutofillHighlightColor provides Color.Transparent
    ) {
        Column {
            Text(
                text = label,
                color = BodyText,
                fontSize = bodySize,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                placeholder = {
                    Text(text = placeholder, color = Color.Gray, fontSize = bodySize)
                },
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                shape = FieldShape,
                isError = error != null,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = FieldTint,
                    unfocusedContainerColor = FieldTint,
                    disabledContainerColor = FieldTint,
                    errorContainerColor = FieldTint,
                    focusedTextColor = BodyText,
                    unfocusedTextColor = BodyText,
                    disabledTextColor = BodyText,
                    errorTextColor = BodyText,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    cursorColor = BrandBlue,
                    errorCursorColor = BrandBlue
                ),
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(min = buttonHeight)
            )
            error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = bodySize * 0.85f,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }
        }
    }
}

@Composable
internal fun CustomPasswordField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String? = null,
    bodySize: TextUnit,
    buttonHeight: Dp,
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    CompositionLocalProvider(
        LocalAutofillHighlightColor provides Color.Transparent
    ) {
        Column {
            Text(
                text = label,
                color = BodyText,
                fontSize = bodySize,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                placeholder = {
                    Text(text = placeholder, color = Color.Gray, fontSize = bodySize)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation(mask = '*')
                },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) {
                                Icons.Filled.Visibility
                            } else {
                                Icons.Filled.VisibilityOff
                            },
                            contentDescription = if (isPasswordVisible) {
                                "Hide password"
                            } else {
                                "Show password"
                            },
                            tint = BrandBlue
                        )
                    }
                },
                shape = FieldShape,
                isError = error != null,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = FieldTint,
                    unfocusedContainerColor = FieldTint,
                    disabledContainerColor = FieldTint,
                    errorContainerColor = FieldTint,
                    focusedTextColor = BodyText,
                    unfocusedTextColor = BodyText,
                    disabledTextColor = BodyText,
                    errorTextColor = BodyText,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    cursorColor = BrandBlue,
                    errorCursorColor = BrandBlue
                ),
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(min = buttonHeight)
            )
            error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = bodySize * 0.85f,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }
        }
    }
}

@Composable
internal fun CustomLoginButton(
    text: String,
    isLoading: Boolean,
    buttonHeight: Dp,
    bodySize: TextUnit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        shape = ButtonDefaults.shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandBlue,
            disabledContainerColor = BrandBlue.copy(alpha = 0.65f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(buttonHeight)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                color = Color.White,
                fontSize = bodySize,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
