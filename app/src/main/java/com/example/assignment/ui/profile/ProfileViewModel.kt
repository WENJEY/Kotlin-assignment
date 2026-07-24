package com.example.assignment.ui.profile

import androidx.lifecycle.ViewModel
import com.example.assignment.ui.navigation.ProfileRoutes
import com.example.assignment.ui.navigation.ScreenRoutes
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {

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
                FirebaseAuth.getInstance().signOut()
                navigateTo(ScreenRoutes.Login.route)
            }
            ProfileEvent.NavigationHandled -> clearNavigation()
            is ProfileEvent.BottomTabSelected -> selectBottomTab(event.tab)
        }
    }

    private fun loadProfile() {
        _uiState.update {
            it.copy(
                username = "Jesus",
                email = "jesus@gmail.com",
                profileImageUrl = null,
                isLoading = false,
                errorMessage = null
            )
        }
    }

    private fun selectBottomTab(tab: ProfileBottomTab) {
        val route = when (tab) {
            ProfileBottomTab.Home -> ScreenRoutes.Home.route
            ProfileBottomTab.Scanner -> ScreenRoutes.Scanner.route
            ProfileBottomTab.ChatBox -> ScreenRoutes.ChatBox.route
            ProfileBottomTab.Profile -> null
        }

        _uiState.update {
            it.copy(
                selectedBottomTab = tab,
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
