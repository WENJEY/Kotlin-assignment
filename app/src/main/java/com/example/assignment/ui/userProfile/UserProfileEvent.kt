package com.example.assignment.ui.userProfile

sealed interface UserProfileEvent {
    data object LoadProfile : UserProfileEvent
    data class UsernameChanged(val value: String) : UserProfileEvent
    data class AgeChanged(val value: String) : UserProfileEvent
    data class PhoneNumberChanged(val value: String) : UserProfileEvent
    data class GenderSelected(val value: String) : UserProfileEvent
    data object SaveClicked : UserProfileEvent
    data object BackClicked : UserProfileEvent
    data object NavigationHandled : UserProfileEvent
    data object MessageShown : UserProfileEvent
}
