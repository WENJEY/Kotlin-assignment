package com.example.assignment.ui.profile

sealed class ProfileEvent {
    data object LoadProfile : ProfileEvent()
    data object UserProfileClicked : ProfileEvent()
    data object ChangePasswordClicked : ProfileEvent()
    data object FeedbackClicked : ProfileEvent()
    data object LogoutClicked : ProfileEvent()
    data object NavigationHandled : ProfileEvent()
    data object LogoutConfirmed : ProfileEvent()
    data object LogoutCanceled : ProfileEvent()
    data object ErrorShown : ProfileEvent()
    data class AvatarCropped(val imageUri: String) : ProfileEvent()
    data class TabSelected(val tab: ProfileTab) : ProfileEvent()
}

