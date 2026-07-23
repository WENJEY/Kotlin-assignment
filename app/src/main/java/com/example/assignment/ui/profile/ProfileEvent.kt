package com.example.assignment.ui.profile

sealed interface ProfileEvent {
    data object LoadProfile : ProfileEvent
    data object UserProfileClicked : ProfileEvent
    data object ChangePasswordClicked : ProfileEvent
    data object FeedbackClicked : ProfileEvent
    data object LogoutClicked : ProfileEvent
    data object NavigationHandled : ProfileEvent
    data class BottomTabSelected(val tab: ProfileBottomTab) : ProfileEvent
}

