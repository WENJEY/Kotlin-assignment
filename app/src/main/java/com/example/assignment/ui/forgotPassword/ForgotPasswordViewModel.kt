package com.example.assignment.ui.forgotPassword

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.database.remote.supabase.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ForgotPasswordViewModel : ViewModel() {

    private val auth = SupabaseClientProvider.client.auth
    private val postgrest = SupabaseClientProvider.client.postgrest
    private var cooldownJob: Job? = null

    private val _uiState =
        MutableStateFlow(ForgotPasswordUiState())

    val uiState: StateFlow<ForgotPasswordUiState> =
        _uiState.asStateFlow()

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS
            .matcher(email)
            .matches()
    }

    fun onEvent(event: ForgotPasswordEvent) {

        when (event) {
            is ForgotPasswordEvent.EmailChanged -> {
                _uiState.update {
                    it.copy(
                        email = event.value,
                        error = null,
                        message = null
                    )
                }
            }

            ForgotPasswordEvent.SendVerificationCodeClicked -> {
                sendVerificationCode()
            }

            ForgotPasswordEvent.BackToLoginClicked -> {
                _uiState.update {
                    it.copy(navigateBack = true)
                }
            }

            ForgotPasswordEvent.NavigationHandled -> {
                _uiState.update {
                    it.copy(
                        navigateBack = false,
                        navigateToVerifyCode = false
                    )
                }
            }

            ForgotPasswordEvent.ScreenResumed -> syncCooldown()
        }
    }

    init {
        observeCooldown()
    }

    private fun sendVerificationCode() {

        val remaining = _uiState.value.resendCooldownSeconds
        if (remaining > 0) {
            _uiState.update {
                it.copy(error = "Please wait $remaining seconds before sending another code.")
            }
            return
        }

        val email =
            _uiState.value.email.trim()

        if (email.isEmpty()) {
            _uiState.update {
                it.copy(
                    error = "Please enter your email.",
                    message = null
                )
            }
            return
        }

        if (!isValidEmail(email)) {
            _uiState.update {
                it.copy(
                    error = "Please enter a valid email address.",
                    message = null
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                error = null,
                message = null
            )
        }

        viewModelScope.launch {
            try {
                val emailExists = postgrest
                    .rpc(
                        function = "email_exists",
                        parameters = buildJsonObject {
                            put("check_email", email)
                        }
                    )
                    .decodeAs<Boolean>()

                if (!emailExists) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Email does not exist.",
                            message = null
                        )
                    }
                    return@launch
                }

                auth.resetPasswordForEmail(
                    email = email,
                    redirectUrl = null
                )

                VerificationCodeCooldown.markSent()
                syncCooldown()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        navigateToVerifyCode = true
                    )
                }
            } catch (e: Exception) {
                Log.e("ForgotPassword", "Send verification code failed", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = mapSendError(e),
                        message = null
                    )
                }
            }
        }
    }

    private fun mapSendError(exception: Exception): String {
        val message = listOfNotNull(exception.message, exception.cause?.message)
            .joinToString(" ")
        return when {
            message.contains("rate limit", ignoreCase = true) ||
                (message.contains("after", ignoreCase = true) &&
                    message.contains("seconds", ignoreCase = true)) ->
                "Too many attempts. Please try again later."
            message.contains("redirect", ignoreCase = true) ->
                "Redirect URL is not allowed. Keep Site URL as http://localhost:3000 in Authentication > URL Configuration."
            message.contains("signups not allowed", ignoreCase = true) ->
                "This email is not registered."
            message.contains("unexpected_failure", ignoreCase = true) ||
                message.contains("error sending recovery email", ignoreCase = true) ->
                "Supabase failed to send the recovery email. Check Email Templates > Reset Password and SMTP settings."
            else -> "Unable to send verification code. Please try again."
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
