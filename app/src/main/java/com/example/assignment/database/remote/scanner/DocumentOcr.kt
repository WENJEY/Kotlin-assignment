package com.example.assignment.database.remote.scanner

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class DocumentOcr {
    private val client = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun fromUri(context: Context, uri: Uri): String {
        return runCatching {
            recognize(InputImage.fromFilePath(context, uri))
        }.getOrDefault("")
    }

    suspend fun fromBitmap(bitmap: Bitmap): String {
        return runCatching {
            recognize(InputImage.fromBitmap(bitmap, 0))
        }.getOrDefault("")
    }

    private suspend fun recognize(image: InputImage): String =
        suspendCancellableCoroutine { continuation ->
            client.process(image)
                .addOnSuccessListener { result: Text ->
                    if (continuation.isActive) {
                        continuation.resume(result.text)
                    }
                }
                .addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume("")
                    }
                }
        }

    fun close() {
        client.close()
    }
}