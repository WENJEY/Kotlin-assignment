package com.example.assignment.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.database.Repository
import com.example.assignment.database.SupabaseRepository
import com.example.assignment.navigation.PasswordResetMode
import com.example.assignment.navigation.ProfileRoutes
import com.example.assignment.navigation.ScreenRoutes
import com.example.assignment.ui.forgotPassword.VerificationCodeCooldown
import com.example.assignment.ui.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: Repository = SupabaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        onEvent(ProfileEvent.LoadProfile)
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.LoadProfile -> loadProfile()
            ProfileEvent.UserProfileClicked -> navigateTo(ProfileRoutes.UserProfile.route)
            ProfileEvent.ChangePasswordClicked -> sendChangePasswordCode()
            ProfileEvent.FeedbackClicked -> navigateTo(ProfileRoutes.Feedback.route)
            ProfileEvent.LogoutClicked -> {
                _uiState.update { it.copy(showLogoutDialog = true) }
            }

            ProfileEvent.LogoutConfirmed -> {
                _uiState.update { it.copy(showLogoutDialog = false) }
                viewModelScope.launch {
                    repository.logout()
                    navigateTo(ScreenRoutes.Login.route)
                }
            }

            // Dismiss dialog
            ProfileEvent.LogoutCanceled -> {
                _uiState.update { it.copy(showLogoutDialog = false) }
            }

            ProfileEvent.NavigationHandled -> clearNavigation()
            ProfileEvent.ErrorShown -> _uiState.update { it.copy(errorMessage = null) }
            is ProfileEvent.AvatarCropped -> Unit
            is ProfileEvent.TabSelected -> selectTab(event.tab)
        }
    }

    private fun loadProfile() {
        val user = repository.currentUser()
        if (user == null) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Unable to load your profile") }
            return
        }
        viewModelScope.launch {
            val imageUrl = when (val result = repository.getProfileImageUrl()) {
                is com.example.assignment.ui.utils.Result.Success -> result.data
                is com.example.assignment.ui.utils.Result.Error -> null
            }
            _uiState.update {
                it.copy(
                    username = user.username,
                    email = user.email,
                    profileImageUrl = imageUrl,
                    avatarPreviewBytes = null,
                    isLoading = false
                )
            }
        }
    }

    fun uploadProfileImage(imageBytes: ByteArray) = viewModelScope.launch {
        val user = repository.currentUser()
        if (user == null) {
            _uiState.update { it.copy(errorMessage = "Please sign in again to update your picture") }
            return@launch
        }
        // Show the cropped image immediately from memory (avoids flaky file:// / cache races).
        _uiState.update {
            it.copy(avatarPreviewBytes = imageBytes, errorMessage = null)
        }
        when (val upload = repository.uploadProfileImage(user.id, imageBytes)) {
            is com.example.assignment.ui.utils.Result.Error -> {
                _uiState.update { it.copy(errorMessage = upload.message) }
            }
            is com.example.assignment.ui.utils.Result.Success -> {
                when (val save = repository.updateProfileImage(upload.data)) {
                    is com.example.assignment.ui.utils.Result.Success -> {
                        _uiState.update {
                            it.copy(
                                // Prefer the versioned remote URL so every device shows the same image.
                                profileImageUrl = upload.data,
                                avatarPreviewBytes = null,
                                errorMessage = null
                            )
                        }
                    }
                    is com.example.assignment.ui.utils.Result.Error -> {
                        _uiState.update { it.copy(errorMessage = save.message) }
                    }
                }
            }
        }
    }

    private fun sendChangePasswordCode() {
        if (_uiState.value.isSendingChangePassword) return

        val email = repository.currentUser()?.email?.trim()
            .orEmpty()
            .ifBlank { _uiState.value.email.trim() }

        if (email.isEmpty()) {
            _uiState.update {
                it.copy(errorMessage = "Unable to find your registered email.")
            }
            return
        }

        val remaining = VerificationCodeCooldown.remainingSeconds()
        if (remaining > 0) {
            navigateTo(
                ScreenRoutes.VerifyCode.createRoute(email, PasswordResetMode.Change)
            )
            return
        }

        _uiState.update {
            it.copy(isSendingChangePassword = true, errorMessage = null)
        }

        viewModelScope.launch {
            when (val result = repository.resetPassword(email)) {
                is Result.Success -> {
                    VerificationCodeCooldown.markSent()
                    _uiState.update {
                        it.copy(
                            isSendingChangePassword = false,
                            navigateTo = ScreenRoutes.VerifyCode.createRoute(
                                email,
                                PasswordResetMode.Change
                            )
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isSendingChangePassword = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    private fun selectTab(tab: ProfileTab) {

        if (tab == ProfileTab.Logout) {
            _uiState.update { it.copy(showLogoutDialog = true) }
            return
        }

        val route = when (tab) {
            ProfileTab.Home -> ScreenRoutes.Home.route
            ProfileTab.Scanner -> ScreenRoutes.Scanner.route
            ProfileTab.ChatBox -> ScreenRoutes.ChatBox.route
            ProfileTab.Profile -> null
            else -> null
        }

        _uiState.update {
            it.copy(
                selectedTab = tab,
                navigateTo = route
            )
        }
    }

    private fun navigateTo(route: String) {
        _uiState.update { it.copy(navigateTo = route) }
    }

    private fun clearNavigation() {
        _uiState.update { it.copy(navigateTo = null) }
    }
}
