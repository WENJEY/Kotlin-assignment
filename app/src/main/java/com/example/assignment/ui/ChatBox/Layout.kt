package com.example.assignment.ui.ChatBox

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.assignment.database.remote.ChatBox.LegalChatAnswer
import com.example.assignment.ui.profile.ProfileTab
import com.example.assignment.ui.theme.BrandBlue
import com.example.assignment.ui.theme.LogoutRed
import com.example.assignment.ui.theme.NavSelected
import com.example.assignment.ui.theme.NavUnselected
import com.example.assignment.ui.theme.SurfaceWhite
import com.example.assignment.ui.theme.appNavigationBarColor
import com.example.assignment.ui.utils.bottomProfileItems
import com.example.assignment.ui.utils.sideProfileItems

@Composable
fun AiChatBoxLayout(
    uiState: AiChatBoxUiState,
    windowSize: WindowWidthSizeClass,
    snackbarHostState: SnackbarHostState,
    onEvent: (AiChatBoxEvent) -> Unit
) {
    when (windowSize) {
        WindowWidthSizeClass.Compact -> AiChatBoxCompactLayout(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onEvent = onEvent
        )
        WindowWidthSizeClass.Medium -> AiChatBoxRailLayout(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onEvent = onEvent,
            showLabels = false
        )
        else -> AiChatBoxRailLayout(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onEvent = onEvent,
            showLabels = true
        )
    }
}

@Composable
private fun AiChatBoxCompactLayout(
    uiState: AiChatBoxUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (AiChatBoxEvent) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            ChatBottomNavigation(
                selectedTab = uiState.selectedTab,
                onTabSelected = { onEvent(AiChatBoxEvent.TabSelected(it)) }
            )
        }
    ) { innerPadding ->
        ChatContent(
            uiState = uiState,
            onEvent = onEvent,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        )
    }
}

@Composable
private fun AiChatBoxRailLayout(
    uiState: AiChatBoxUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (AiChatBoxEvent) -> Unit,
    showLabels: Boolean
) {
    Row(modifier = Modifier.fillMaxSize()) {
        ChatNavigationRail(
            selectedTab = uiState.selectedTab,
            onTabSelected = { onEvent(AiChatBoxEvent.TabSelected(it)) },
            showLabels = showLabels
        )
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding(),
                contentAlignment = Alignment.TopCenter
            ) {
                ChatContent(
                    uiState = uiState,
                    onEvent = onEvent,
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun ChatContent(
    uiState: AiChatBoxUiState,
    onEvent: (AiChatBoxEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size, uiState.isSending) {
        val lastIndex = uiState.messages.lastIndex + if (uiState.isSending) 1 else 0
        if (lastIndex >= 0) {
            listState.animateScrollToItem(lastIndex)
        }
    }

    Column(modifier = modifier) {
        ChatHeader(
            title = uiState.conversationTitle,
            onHistoryClick = { onEvent(AiChatBoxEvent.HistoryClicked) },
            onNewChatClick = { onEvent(AiChatBoxEvent.NewChatClicked) }
        )

        if (uiState.isHistoryOpen) {
            Dialog(onDismissRequest = { onEvent(AiChatBoxEvent.HistoryDismissed) }) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    ConversationHistorySheet(
                        uiState = uiState,
                        onEvent = onEvent
                    )
                }
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (uiState.isLoadingHistory) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.messages.isEmpty() && !uiState.isSending) {
                item {
                    Text(
                        text = "Ask a Malaysian employment-law question.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp)
                    )
                }
            }

            items(uiState.messages, key = { it.id }) { message ->
                ChatBubble(message = message)
            }

            if (uiState.isSending) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Thinking…",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        ChatInputBar(
            value = uiState.inputText,
            enabled = !uiState.isSending,
            onValueChange = { onEvent(AiChatBoxEvent.InputChanged(it)) },
            onSend = { onEvent(AiChatBoxEvent.SendClicked) }
        )
    }
}

@Composable
private fun ChatHeader(
    title: String,
    onHistoryClick: () -> Unit,
    onNewChatClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onHistoryClick) {
            Icon(
                imageVector = Icons.Filled.History,
                contentDescription = "Chat history",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = onNewChatClick) {
            Icon(
                imageVector = Icons.Filled.AddComment,
                contentDescription = "New chat",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun ConversationHistorySheet(
    uiState: AiChatBoxUiState,
    onEvent: (AiChatBoxEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 480.dp)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 28.dp)
    ) {
        Text(
            text = "Chats",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEvent(AiChatBoxEvent.NewChatClicked) },
            shape = RoundedCornerShape(14.dp),
            color = BrandBlue
        ) {
            Text(
                text = "New chat",
                color = SurfaceWhite,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (uiState.conversations.isEmpty()) {
            Text(
                text = "No saved chats yet.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            uiState.conversations.forEach { conversation ->
                val selected = conversation.id == uiState.currentConversationId
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable { onEvent(AiChatBoxEvent.ConversationSelected(conversation.id)) },
                    shape = RoundedCornerShape(14.dp),
                    color = if (selected) {
                        BrandBlue.copy(alpha = 0.12f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ) {
                    Text(
                        text = conversation.title,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.isFromUser
    val structured = message.structured
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            modifier = if (isUser) {
                Modifier.widthIn(max = 300.dp)
            } else {
                Modifier.fillMaxWidth(0.94f)
            },
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 18.dp
            ),
            color = if (isUser) BrandBlue else MaterialTheme.colorScheme.surface,
            tonalElevation = if (isUser) 0.dp else 1.dp
        ) {
            if (!isUser && structured != null && structured.hasStructure) {
                StructuredAiAnswer(
                    answer = structured,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                )
            } else {
                Text(
                    text = message.text,
                    color = if (isUser) SurfaceWhite else MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun StructuredAiAnswer(
    answer: LegalChatAnswer,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (answer.answer.isNotBlank()) {
            AnswerSection(title = "Answer", body = answer.answer)
        }
        if (answer.statute.isNotBlank()) {
            AnswerSection(title = "Legal basis", body = answer.statute)
        }
        if (answer.explanation.isNotBlank()) {
            AnswerSection(title = "Explanation", body = answer.explanation)
        }
        if (answer.nextSteps.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "What you can do",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = BrandBlue
                )
                answer.nextSteps.forEach { step ->
                    Text(
                        text = "• $step",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }
            }
        }
        if (answer.followUp.isNotBlank()) {
            AnswerSection(title = "Next question", body = answer.followUp)
        }
    }
}

@Composable
private fun AnswerSection(
    title: String,
    body: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = BrandBlue
        )
        Text(
            text = body,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 15.sp,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    val canSend = enabled && value.trim().isNotEmpty()

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                enabled = enabled,
                placeholder = { Text("Ask about employment law…") },
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { if (canSend) onSend() }),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onSend,
                enabled = canSend,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (canSend) BrandBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = SurfaceWhite
                )
            }
        }
    }
}

@Composable
private fun ChatBottomNavigation(
    selectedTab: ProfileTab,
    onTabSelected: (ProfileTab) -> Unit
) {
    NavigationBar(containerColor = appNavigationBarColor()) {
        bottomProfileItems.forEach { item ->
            NavigationBarItem(
                selected = selectedTab == item.tab,
                onClick = { onTabSelected(item.tab) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.description
                    )
                },
                label = { Text(text = item.iconText) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NavSelected,
                    selectedTextColor = NavSelected,
                    indicatorColor = NavSelected.copy(alpha = 0.12f),
                    unselectedIconColor = NavUnselected,
                    unselectedTextColor = NavUnselected
                )
            )
        }
    }
}

@Composable
private fun ChatNavigationRail(
    selectedTab: ProfileTab,
    onTabSelected: (ProfileTab) -> Unit,
    showLabels: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(if (showLabels) 280.dp else 100.dp)
            .background(appNavigationBarColor())
            .padding(vertical = 32.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        sideProfileItems.forEach { item ->
            val selected = selectedTab == item.tab
            val bgColor = if (selected) NavSelected else Color.Transparent
            val contentColor = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
            val isLogout = item.icon == Icons.AutoMirrored.Filled.Logout

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(bgColor)
                    .clickable { onTabSelected(item.tab) }
                    .padding(horizontal = if (showLabels) 20.dp else 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (showLabels) Arrangement.Start else Arrangement.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.description,
                    tint = if (isLogout) LogoutRed else contentColor,
                    modifier = Modifier.size(30.dp)
                )
                if (showLabels) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = item.iconText,
                        color = if (isLogout) LogoutRed else contentColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
