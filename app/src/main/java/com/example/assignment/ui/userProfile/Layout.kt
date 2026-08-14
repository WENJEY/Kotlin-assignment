package com.example.assignment.ui.userProfile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.LocalAutofillHighlightColor
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.ui.profile.CropAvatarDialog
import com.example.assignment.ui.profile.ProfileAvatar
import com.example.assignment.ui.theme.BrandBlue
import com.example.assignment.ui.theme.ErrorRed
import com.example.assignment.ui.theme.MutedText
import com.example.assignment.ui.theme.SurfaceWhite

private val GenderOptions = listOf("Male", "Female", "Other")

@Composable
fun UserProfileCompactLayout(
    uiState: UserProfileUiState,
    onAvatarSelected: (ByteArray) -> Unit,
    onEvent: (UserProfileEvent) -> Unit,
    snackBarHostState: SnackbarHostState,
    horizontalPadding: Dp,
    avatarSize: Dp,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        // Disable Scaffold's default bottom inset slot so it doesn't cover the form.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {},
        topBar = {
            UserProfileHeader(onBack = { onEvent(UserProfileEvent.BackClicked) })
        }
    ) { innerPadding ->
        val context = LocalContext.current
        var imageToCrop by remember { mutableStateOf<Uri?>(null) }
        val imagePicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            imageToCrop = uri
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .navigationBarsPadding()
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = horizontalPadding)
                            .padding(bottom = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        ProfileAvatar(
                            profileImageUrl = uiState.profileImageUrl,
                            previewBytes = uiState.avatarPreviewBytes,
                            size = avatarSize,
                            onEditClick = { imagePicker.launch("image/*") }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 8.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                            ProfileTextField(
                                label = "Username",
                                value = uiState.username,
                                onValueChange = { onEvent(UserProfileEvent.UsernameChanged(it)) },
                                keyboardType = KeyboardType.Text,
                                error = uiState.usernameError
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            ProfileTextField(
                                label = "Email",
                                value = uiState.email,
                                onValueChange = {},
                                keyboardType = KeyboardType.Email,
                                enabled = false
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            ProfileTextField(
                                label = "Age",
                                value = uiState.age,
                                onValueChange = { onEvent(UserProfileEvent.AgeChanged(it)) },
                                keyboardType = KeyboardType.Number,
                                error = uiState.ageError
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            ProfileTextField(
                                label = "Phone Number",
                                value = uiState.phoneNumber,
                                onValueChange = { onEvent(UserProfileEvent.PhoneNumberChanged(it)) },
                                keyboardType = KeyboardType.Phone,
                                error = uiState.phoneError
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Gender",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                GenderOptions.forEach { option ->
                                    GenderChip(
                                        label = option,
                                        selected = uiState.gender.equals(option, ignoreCase = true),
                                        onClick = { onEvent(UserProfileEvent.GenderSelected(option)) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            uiState.genderError?.let { error ->
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = error, color = ErrorRed, fontSize = 13.sp)
                            }

                            Spacer(modifier = Modifier.height(22.dp))

                            Button(
                                onClick = { onEvent(UserProfileEvent.SaveClicked) },
                                enabled = !uiState.isSaving,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BrandBlue,
                                    contentColor = SurfaceWhite,
                                    disabledContainerColor = BrandBlue.copy(alpha = 0.6f)
                                )
                            ) {
                                if (uiState.isSaving) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        color = SurfaceWhite,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = "Save Changes",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            }
                        }
                    }
                }
            }

            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        imageToCrop?.let { uri ->
            CropAvatarDialog(
                imageUri = uri,
                onDismiss = { imageToCrop = null },
                onCropConfirmed = { croppedUri ->
                    imageToCrop = null
                    context.contentResolver.openInputStream(croppedUri)?.use { stream ->
                        onAvatarSelected(stream.readBytes())
                    }
                }
            )
        }
    }
}

@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    error: String? = null,
    enabled: Boolean = true
) {
    CompositionLocalProvider(
        LocalAutofillHighlightColor provides Color.Transparent
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                shape = RoundedCornerShape(14.dp),
                isError = error != null,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    errorContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledTextColor = MutedText,
                    errorTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    cursorColor = BrandBlue
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
            )
            error?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = it, color = ErrorRed, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun GenderChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = if (selected) BrandBlue else MaterialTheme.colorScheme.surfaceVariant
    val content = if (selected) SurfaceWhite else MaterialTheme.colorScheme.onSurface
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .border(
                width = 1.dp,
                color = if (selected) BrandBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = content,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Composable
private fun UserProfileHeader(onBack: () -> Unit) {
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
                text = "User Profile",
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 21.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
