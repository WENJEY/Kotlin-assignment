package com.example.assignment.ui.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

private const val CropOutputSize = 512

@Composable
fun CropAvatarDialog(
    imageUri: Uri,
    onDismiss: () -> Unit,
    onCropConfirmed: (Uri) -> Unit
) {
    val context = LocalContext.current
    val bitmap by produceState<Bitmap?>(initialValue = null, imageUri) {
        value = withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(imageUri)?.use(BitmapFactory::decodeStream)
        }
    }
    var zoom by remember(imageUri) { mutableFloatStateOf(1f) }
    var offset by remember(imageUri) { mutableStateOf(Offset.Zero) }
    var isSaving by remember { mutableStateOf(false) }
    var cropSizePx by remember { mutableFloatStateOf(280f) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text("Crop profile photo") },
        text = {
            if (bitmap == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AvatarCropPreview(
                        bitmap = bitmap!!,
                        zoom = zoom,
                        offset = offset,
                        onCropSizeChanged = { cropSizePx = it },
                        onTransform = { zoomChange, panChange ->
                            val newZoom = (zoom * zoomChange).coerceIn(1f, 4f)
                            zoom = newZoom
                            offset = constrainOffset(
                                bitmap = bitmap!!,
                                cropSize = cropSizePx,
                                zoom = newZoom,
                                requestedOffset = offset + panChange
                            )
                        }
                    )
                    Text(
                        text = "Drag to reposition and pinch to zoom",
                        modifier = Modifier.padding(top = 16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = bitmap != null && !isSaving,
                onClick = {
                    val source = bitmap ?: return@TextButton
                    isSaving = true
                    scope.launch {
                        val outputUri = withContext(Dispatchers.Default) {
                            cropBitmap(source, cropSizePx, zoom, offset)
                                .let { writeCroppedAvatar(context, it) }
                        }
                        if (outputUri != null) onCropConfirmed(outputUri) else isSaving = false
                    }
                }
            ) {
                Text(if (isSaving) "Saving…" else "Use photo")
            }
        },
        dismissButton = {
            TextButton(enabled = !isSaving, onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun AvatarCropPreview(
    bitmap: Bitmap,
    zoom: Float,
    offset: Offset,
    onCropSizeChanged: (Float) -> Unit,
    onTransform: (zoomChange: Float, panChange: Offset) -> Unit
) {
    Box(
        modifier = Modifier
            .size(280.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            .onSizeChanged { onCropSizeChanged(it.width.toFloat()) }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, gestureZoom, _ ->
                    onTransform(gestureZoom, pan)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Selected profile photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    scaleX = zoom
                    scaleY = zoom
                    translationX = offset.x
                    translationY = offset.y
                }
        )
    }
}

private fun constrainOffset(
    bitmap: Bitmap,
    cropSize: Float,
    zoom: Float,
    requestedOffset: Offset
): Offset {
    val scale = max(cropSize / bitmap.width, cropSize / bitmap.height) * zoom
    val maxX = max(0f, (bitmap.width * scale - cropSize) / 2f)
    val maxY = max(0f, (bitmap.height * scale - cropSize) / 2f)
    return Offset(
        x = requestedOffset.x.coerceIn(-maxX, maxX),
        y = requestedOffset.y.coerceIn(-maxY, maxY)
    )
}

private fun cropBitmap(bitmap: Bitmap, previewSize: Float, zoom: Float, offset: Offset): Bitmap {
    val baseScale = max(previewSize / bitmap.width, previewSize / bitmap.height)
    val scale = baseScale * zoom * CropOutputSize / previewSize
    val translateX = CropOutputSize / 2f - bitmap.width * scale / 2f + offset.x * CropOutputSize / previewSize
    val translateY = CropOutputSize / 2f - bitmap.height * scale / 2f + offset.y * CropOutputSize / previewSize
    val matrix = Matrix().apply {
        setValues(floatArrayOf(scale, 0f, translateX, 0f, scale, translateY, 0f, 0f, 1f))
    }
    return Bitmap.createBitmap(CropOutputSize, CropOutputSize, Bitmap.Config.ARGB_8888).also { output ->
        Canvas(output).drawBitmap(bitmap, matrix, null)
    }
}

private fun writeCroppedAvatar(context: Context, bitmap: Bitmap): Uri? = try {
    val directory = File(context.cacheDir, "avatars").apply { mkdirs() }
    val file = File(directory, "avatar-${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { stream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
    }
    Uri.fromFile(file)
} catch (_: Exception) {
    null
}