package com.example.assignment.ui.scanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.assignment.ui.theme.ErrorRed
import com.example.assignment.ui.theme.SuccessGreen
import com.example.assignment.ui.theme.TextGray
import java.io.File

@Composable
fun ScannerDetailContent(
    document: ScannedDocument,
    onEvent: (ScannerEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val previewPath = document.thumbnailPath ?: document.filePath.takeIf {
        document.type == DocumentType.IMAGE
    }

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
                    text = document.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2
                )
                Text(
                    text = document.metadata,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { onEvent(ScannerEvent.ShareClicked(document.id)) }) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Share"
                )
            }
            IconButton(onClick = { onEvent(ScannerEvent.DeleteClicked(document.id)) }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete"
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            if (previewPath != null && File(previewPath).exists()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(File(previewPath))
                        .crossfade(true)
                        .build(),
                    contentDescription = document.name,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (document.type == DocumentType.PDF) "PDF document" else "Image document",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailChip(
                label = if (document.source == DocumentSource.SCAN) "Scanned" else "Imported",
                modifier = Modifier.weight(1f)
            )
            DetailChip(
                label = if (document.type == DocumentType.PDF) "PDF" else "Image",
                modifier = Modifier.weight(1f)
            )
            DetailChip(
                label = "${document.pageCount} page${if (document.pageCount == 1) "" else "s"}",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        ValidationResultCard(
            document = document,
            onRecheck = { onEvent(ScannerEvent.RecheckClicked) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        LabourLawResultCard(document = document)
        Spacer(modifier = Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Extracted text",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = { onEvent(ScannerEvent.CopyTextClicked) }) {
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text("Copy")
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            SelectionContainer {
                Text(
                    text = document.extractedText.ifBlank {
                        "No readable text was found in this document."
                    },
                    modifier = Modifier.padding(16.dp),
                    color = if (document.extractedText.isBlank()) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun ValidationResultCard(
    document: ScannedDocument,
    onRecheck: () -> Unit
) {
    val statusColor = validationStatusColor(document.validationStatus)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AI validity check",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = document.validationStatus.label(),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = statusColor
                    )
                }
                TextButton(onClick = onRecheck) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Recheck")
                }
            }
            if (document.documentKind.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = document.documentKind.replaceFirstChar { it.uppercaseChar() },
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (document.validationSummary.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = document.validationSummary,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (document.validationIssues.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Issues",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                document.validationIssues.forEach { issue ->
                    Text(
                        text = "• $issue",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

private fun validationStatusColor(status: DocumentValidationStatus): Color = when (status) {
    DocumentValidationStatus.VALID -> SuccessGreen
    DocumentValidationStatus.INVALID,
    DocumentValidationStatus.UNREADABLE -> ErrorRed
    else -> TextGray
}

@Composable
private fun LabourLawResultCard(document: ScannedDocument) {
    val statusColor = when (document.legalStatus) {
        DocumentLegalStatus.COMPLIANT -> SuccessGreen
        DocumentLegalStatus.NON_COMPLIANT -> ErrorRed
        else -> TextGray
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Malaysian labour law",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = document.legalStatus.label(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = statusColor
            )
            if (document.legalStatute.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = document.legalStatute,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (document.legalSummary.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = document.legalSummary,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            BulletBlock(title = "Violations", items = document.legalViolations)
            BulletBlock(title = "Missing requirements", items = document.legalMissing)
            BulletBlock(title = "What you can do", items = document.legalNextSteps)
        }
    }
}

@Composable
private fun BulletBlock(
    title: String,
    items: List<String>
) {
    if (items.isEmpty()) return
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = title,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(4.dp))
    items.forEach { item ->
        Text(
            text = "• $item",
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DetailChip(
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
