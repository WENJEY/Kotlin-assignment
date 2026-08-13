package com.example.assignment.ui.forgotPassword

object VerificationCodeCooldown {
    const val DurationSeconds = 60

    @Volatile
    private var lastSentAtMs: Long = 0L

    fun markSent() {
        lastSentAtMs = System.currentTimeMillis()
    }

    fun remainingSeconds(now: Long = System.currentTimeMillis()): Int {
        if (lastSentAtMs == 0L) return 0
        val elapsedSeconds = (now - lastSentAtMs) / 1000L
        return (DurationSeconds - elapsedSeconds).toInt().coerceIn(0, DurationSeconds)
    }
}
