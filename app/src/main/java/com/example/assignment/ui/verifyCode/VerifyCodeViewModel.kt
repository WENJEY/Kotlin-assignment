package com.example.assignment.ui.verifyCode

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.database.remote.supabase.SupabaseClientProvider
import com.example.assignment.navigation.PasswordResetMode
import com.example.assignment.ui.forgotPassword.VerificationCodeCooldown
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VerifyCodeViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val auth = SupabaseClientProvider.client.auth
    private val email: String = savedStateHandle.get<String>("email")
        ?.let(Uri::decode)
        .orEmpty()
    private val mode: String = savedStateHandle.get<String>("mode")
        ?: PasswordResetMode.Forgot
    private var cooldownJob: Job? = null

    private val _uiState = MutableStateFlow(
        VerifyCodeUiState(email = email, mode = mode)
    )
    val uiState: StateFlow<VerifyCodeUiState> = _uiState.asStateFlow()

    init {
        observeCooldown()
    }

    fun onEvent(event: VerifyCodeEvent) {
        when (event) {
            is VerifyCodeEvent.CodeChanged -> {
                val digitsOnly = event.value.filter { it.isDigit() }.take(8)
                _uiState.update {
                    it.copy(code = digitsOnly, error = null, message = null)
                }
            }
            VerifyCodeEvent.VerifyClicked -> verifyCode()
            VerifyCodeEvent.ResendClicked -> resendCode()
            VerifyCodeEvent.BackClicked -> _uiState.update { it.copy(navigateBack = true) }
            VerifyCodeEvent.NavigationHandled -> _uiState.update {
                it.copy(navigateBack = false, navigateToResetPassword = false)
            }
        }
    }

    private fun verifyCode() {
        val code = _uiState.value.code.trim()
        if (code.length < 6) {
            _uiState.update {
                it.copy(error = "Please enter the 6-digit verification code.", message = null)
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null, message = null) }

        viewModelScope.launch {
            try {
                auth.verifyEmailOtp(
                    type = OtpType.Email.RECOVERY,
                    email = email,
                    token = code
                )
                _uiState.update {
                    it.copy(isLoading = false, navigateToResetPassword = true)
                }
            } catch (e: Exception) {
                Log.e("VerifyCode", "Verify code failed", e)
                _uiState.update {
                    it.copy(isLoading = false, error = mapOtpError(e, sending = false))
                }
            }
        }
    }

    private fun resendCode() {
        if (email.isBlank()) {
            _uiState.update { it.copy(error = "Missing email. Please go back and try again.") }
            return
        }

        val remaining = _uiState.value.resendCooldownSeconds
        if (remaining > 0) {
            _uiState.update {
                it.copy(error = "Please wait $remaining seconds before sending another code.")
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null, message = null) }

        viewModelScope.launch {
            try {
                auth.resetPasswordForEmail(
                    email = email,
                    redirectUrl = null
                )
                VerificationCodeCooldown.markSent()
                syncCooldown()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        message = "Verification code sent."
                    )
                }
            } catch (e: Exception) {
                Log.e("VerifyCode", "Resend code failed", e)
                _uiState.update {
                    it.copy(isLoading = false, error = mapOtpError(e, sending = true))
                }
            }
        }
    }

    private fun mapOtpError(exception: Exception, sending: Boolean): String {
        val message = listOfNotNull(exception.message, exception.cause?.message)
            .joinToString(" ")
        return when {
            message.contains("rate limit", ignoreCase = true) ->
                "Too many attempts. Please try again later."
            message.contains("expired", ignoreCase = true) ->
                "This code has expired. Please request a new one."
            message.contains("redirect", ignoreCase = true) ->
                "Redirect URL is not allowed. Keep Site URL as http://localhost:3000 in Authentication > URL Configuration."
            message.contains("invalid", ignoreCase = true) ||
                (message.contains("otp", ignoreCase = true) &&
                    message.contains("token", ignoreCase = true)) ->
                "Invalid verification code. Please try again."
            sending && message.isNotBlank() -> message
            sending -> "Unable to send verification code. Please try again."
            message.isNotBlank() -> message
            else -> "Unable to verify the code. Please try again."
        }
    }

    private fun observeCooldown() {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            while (true) {
                syncCooldown()
                delay(1000)
            }
        }
    }

    private fun syncCooldown() {
        _uiState.update {
            it.copy(resendCooldownSeconds = VerificationCodeCooldown.remainingSeconds())
        }
    }
}
