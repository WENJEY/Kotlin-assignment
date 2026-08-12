package com.example.assignment.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.database.Repository
import com.example.assignment.database.SupabaseRepository
import com.example.assignment.navigation.ProfileRoutes
import com.example.assignment.navigation.ScreenRoutes
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
            ProfileEvent.ChangePasswordClicked -> navigateTo(ProfileRoutes.ChangePassword.route)
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
            is ProfileEvent.AvatarCropped -> {
                _uiState.update { it.copy(profileImageUrl = event.imageUri) }
            }
            is ProfileEvent.TabSelected -> selectTab(event.tab)
        }
    }

    private fun loadProfile() {
        val user = repository.currentUser()
        _uiState.update {
            it.copy(
                username = user?.username.orEmpty(),
                email = user?.email.orEmpty(),
                profileImageUrl = null,
                isLoading = false,
                errorMessage = if (user == null) "Unable to load your profile" else null
            )
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
