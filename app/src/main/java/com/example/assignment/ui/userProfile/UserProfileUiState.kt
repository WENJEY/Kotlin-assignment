package com.example.assignment.ui.userProfile

data class UserProfileUiState(
    val username: String = "",
    val email: String = "",
    val age: String = "",
    val phoneNumber: String = "",
    val gender: String = "",
    val profileImageUrl: String? = null,
    val usernameError: String? = null,
    val ageError: String? = null,
    val phoneError: String? = null,
    val genderError: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val avatarPreviewBytes: ByteArray? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val navigateBack: Boolean = false
)
