package com.example.assignment.ui.forgotPassword
/**package com.example.assignment.ui.forgotPassword

import androidx.lifecycle.ViewModel
import com.example.assignment.ui.login.LoginUiState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ForgotPasswordViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEvent(event: ForgotPasswordEvent) {

        when(event){

            is ForgotPasswordEvent.EmailChanged -> {

                _uiState.update {
                    it.copy(
                        email = event.value,
                        error = null,
                        message = null
                    )
                }

            }

            ForgotPasswordEvent.SendResetLinkClicked -> {

                sendResetEmail()

            }

            ForgotPasswordEvent.BackToLoginClicked -> {

                _uiState.update {
                    it.copy(navigateBack = true)
                }

            }

            ForgotPasswordEvent.NavigationHandled -> {

                _uiState.update {
                    it.copy(navigateBack = false)
                }

            }
        }
    }

    private fun sendResetEmail(){

        val email = _uiState.value.email.trim()

        if(email.isEmpty()){

            _uiState.update {
                it.copy(error = "Please enter your email.")
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

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {

                _uiState.update {

                    it.copy(
                        isLoading = false,
                        message = "Password reset email sent."
                    )

                }

            }
            .addOnFailureListener { exception ->

                _uiState.update {

                    it.copy(
                        isLoading = false,
                        error = exception.localizedMessage
                            ?: "Unable to send reset email."
                    )

                }

            }

    }
}
**/