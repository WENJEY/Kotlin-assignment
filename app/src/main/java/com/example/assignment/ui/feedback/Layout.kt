package com.example.assignment.ui.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.navigation.ScreenRoutes
import com.example.assignment.ui.theme.ButtonBlue
import com.example.assignment.ui.theme.ErrorRed
import com.example.assignment.ui.theme.FeedbackBorder
import com.example.assignment.ui.theme.FeedbackIconBackground
import com.example.assignment.ui.theme.FeedbackIconTint
import com.example.assignment.ui.theme.FeedbackMuted
import com.example.assignment.ui.theme.HeaderBlue
import com.example.assignment.ui.theme.RatingBad
import com.example.assignment.ui.theme.RatingExcellent
import com.example.assignment.ui.theme.RatingGood
import com.example.assignment.ui.theme.RatingLabel
import com.example.assignment.ui.theme.RatingNeutral
import com.example.assignment.ui.theme.RatingVeryBad
import com.example.assignment.ui.theme.SurfaceWhite

private data class RatingOption(
    val value: Int,
    val face: String,
    val label: String,
    val color: Color
)

private val ratings = listOf(
    RatingOption(1, "☹", "Very Bad", RatingVeryBad),
    RatingOption(2, "☹", "Bad", RatingBad),
    RatingOption(3, "—", "Neutral", RatingNeutral),
    RatingOption(4, "☺", "Good", RatingGood),
    RatingOption(5, "●", "Excellent", RatingExcellent)
)

@Composable
fun FeedbackLayout(
    uiState: FeedbackUiState,
    windowSize: WindowWidthSizeClass,
    snackbarHostState: SnackbarHostState,
    onEvent: (FeedbackEvent) -> Unit,
    onNavigate: (String) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { FeedbackHeader(onBack = { onEvent(FeedbackEvent.BackClicked) }) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 680.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = if (windowSize == WindowWidthSizeClass.Compact) 20.dp else 40.dp,
                        vertical = 24.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                WelcomeCard()
                RatingSection(uiState = uiState, onEvent = onEvent)
                CategorySection(uiState = uiState, onEvent = onEvent)
                MessageSection(uiState = uiState, onEvent = onEvent)
                ContactSection(uiState = uiState, onEvent = onEvent)
                SubmitButton(uiState = uiState, onEvent = onEvent)
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun FeedbackHeader(onBack: () -> Unit) {
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
                text = "Feedback",
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 21.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .background(FeedbackIconBackground, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    tint = FeedbackIconTint,
                    modifier = Modifier.size(64.dp)
                )
            }
            Spacer(Modifier.width(18.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "We’d love to hear from you!",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Your feedback helps us improve Malaysia Labour Law Assistant and serve you better.",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
            }
        }
    }
}

@Composable
private fun RatingSection(
    uiState: FeedbackUiState,
    onEvent: (FeedbackEvent) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionTitle("How was your experience?")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ratings.forEach { option ->
                val selected = uiState.rating == option.value
                Column(
                    modifier = Modifier
                        .width(61.dp)
                        .clickable {
                            onEvent(FeedbackEvent.RatingSelected(option.value))
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(if (selected) 50.dp else 46.dp),
                        shape = CircleShape,
                        color = option.color,
                        shadowElevation = if (selected) 7.dp else 0.dp,
                        border = if (selected) {
                            androidx.compose.foundation.BorderStroke(3.dp, HeaderBlue)
                        } else {
                            null
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = option.face,
                                color = RatingLabel,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = option.label,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        FieldError(uiState.ratingError)
    }
}

@Composable
private fun CategorySection(
    uiState: FeedbackUiState,
    onEvent: (FeedbackEvent) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle("What is your feedback about?")
        Box {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clickable { onEvent(FeedbackEvent.CategoryClicked) },
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (uiState.categoryError != null) ErrorRed else FeedbackBorder
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.GridView, null, tint = HeaderBlue)
                    Spacer(Modifier.width(14.dp))
                    Text(
                        text = uiState.category.ifBlank { "Select a category" },
                        modifier = Modifier.weight(1f),
                        color = if (uiState.category.isBlank()) FeedbackMuted else MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    )
                    Icon(Icons.Default.ExpandMore, null, tint = FeedbackMuted)
                }
            }
            DropdownMenu(
                expanded = uiState.isCategoryMenuOpen,
                onDismissRequest = { onEvent(FeedbackEvent.CategoryDismissed) },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .widthIn(min = 280.dp)
            ) {
                FeedbackUiState.Categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = { onEvent(FeedbackEvent.CategorySelected(category)) }
                    )
                }
            }
        }
        FieldError(uiState.categoryError)
    }
}

@Composable
private fun MessageSection(
    uiState: FeedbackUiState,
    onEvent: (FeedbackEvent) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle("Your feedback")
        OutlinedTextField(
            value = uiState.message,
            onValueChange = { onEvent(FeedbackEvent.MessageChanged(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            placeholder = { Text("Tell us more...", color = FeedbackMuted) },
            prefix = {
                Icon(Icons.Default.RateReview, null, tint = FeedbackMuted,modifier = Modifier.size(24.dp))
            },
            supportingText = {
                Text(
                    text = "${uiState.message.length}/500",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            },
            isError = uiState.messageError != null,
            minLines = 4,
            maxLines = 6,
            shape = RoundedCornerShape(12.dp),
            colors = feedbackFieldColors()
        )
        FieldError(uiState.messageError)
    }
}

@Composable
private fun ContactSection(
    uiState: FeedbackUiState,
    onEvent: (FeedbackEvent) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row {
            SectionTitle("Contact")
            Text(" (Optional)", color = FeedbackMuted, fontSize = 16.sp)
        }
        OutlinedTextField(
            value = uiState.contactEmail,
            onValueChange = { onEvent(FeedbackEvent.ContactEmailChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter your email", color = FeedbackMuted) },
            leadingIcon = { Icon(Icons.Default.Email, null, tint = FeedbackMuted) },
            singleLine = true,
            isError = uiState.emailError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(12.dp),
            colors = feedbackFieldColors()
        )
        FieldError(uiState.emailError)
    }
}

@Composable
private fun SubmitButton(
    uiState: FeedbackUiState,
    onEvent: (FeedbackEvent) -> Unit
) {
    Button(
        onClick = { onEvent(FeedbackEvent.SubmitClicked) },
        enabled = !uiState.isSubmitting,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue)
    ) {
        if (uiState.isSubmitting) {
            CircularProgressIndicator(
                modifier = Modifier.size(23.dp),
                color = SurfaceWhite,
                strokeWidth = 2.dp
            )
        } else {
            Icon(Icons.AutoMirrored.Filled.Send, null)
            Spacer(Modifier.width(12.dp))
            Text("Send Feedback", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun FieldError(message: String?) {
    if (message != null) {
        Text(text = message, color = ErrorRed, fontSize = 12.sp)
    }
}

@Composable
private fun feedbackFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    errorContainerColor = MaterialTheme.colorScheme.surface,
    focusedBorderColor = HeaderBlue,
    unfocusedBorderColor = FeedbackBorder,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
)

