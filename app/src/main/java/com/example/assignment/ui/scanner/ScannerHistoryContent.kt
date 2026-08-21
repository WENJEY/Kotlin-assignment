package com.example.assignment.ui.scanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScannerHistoryContent(
    uiState: ScannerUiState,
    onEvent: (ScannerEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onEvent(ScannerEvent.BackClicked) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Scan history",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (uiState.documents.isEmpty()) {
                        "No files yet"
                    } else {
                        "${uiState.documents.size} saved document${if (uiState.documents.size == 1) "" else "s"}"
                    },
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (uiState.documents.isEmpty()) {
            EmptyDocumentsState(
                title = "No history yet",
                subtitle = "Scan a physical document or import a file from your phone to see it here."
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                uiState.documents.forEach { document ->
                    RecentDocumentCard(
                        document = document,
                        onClick = { onEvent(ScannerEvent.DocumentClicked(document.id)) },
                        onShare = { onEvent(ScannerEvent.ShareClicked(document.id)) },
                        onDelete = { onEvent(ScannerEvent.DeleteClicked(document.id)) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun EmptyDocumentsState(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.History,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
