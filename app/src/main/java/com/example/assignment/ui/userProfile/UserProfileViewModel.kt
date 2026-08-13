package com.example.assignment.ui.userProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.database.Repository
import com.example.assignment.database.SupabaseRepository
import com.example.assignment.ui.utils.RegisterValidator
import com.example.assignment.ui.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserProfileViewModel(
    private val repository: Repository = SupabaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserProfileUiState(isLoading = true))
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        onEvent(UserProfileEvent.LoadProfile)
    }

    fun onEvent(event: UserProfileEvent) {
        when (event) {
            UserProfileEvent.LoadProfile -> loadProfile()
            is UserProfileEvent.UsernameChanged -> _uiState.update {
                it.copy(username = event.value, usernameError = null, errorMessage = null)
            }
            is UserProfileEvent.AgeChanged -> {
                val digitsOnly = event.value.filter { it.isDigit() }.take(3)
                _uiState.update {
                    it.copy(age = digitsOnly, ageError = null, errorMessage = null)
                }
            }
            is UserProfileEvent.PhoneNumberChanged -> {
                val hasSpaceBetween = event.value.trim().any { it.isWhitespace() }
                val filtered = event.value.filter { it.isDigit() || it == '-' }
                _uiState.update {
                    it.copy(
                        phoneNumber = filtered,
                        phoneError = if (hasSpaceBetween) "No spacing between the number" else null,
                        errorMessage = null
                    )
                }
            }
            is UserProfileEvent.GenderSelected -> _uiState.update {
                it.copy(gender = event.value, genderError = null, errorMessage = null)
            }
            UserProfileEvent.SaveClicked -> saveProfile()
            UserProfileEvent.BackClicked -> _uiState.update { it.copy(navigateBack = true) }
            UserProfileEvent.NavigationHandled -> _uiState.update { it.copy(navigateBack = false) }
            UserProfileEvent.MessageShown -> _uiState.update {
                it.copy(errorMessage = null, successMessage = null)
            }
        }
    }

    fun uploadProfileImage(imageBytes: ByteArray) = viewModelScope.launch {
        val user = repository.currentUser()
        if (user == null) {
            _uiState.update { it.copy(errorMessage = "Please sign in again to update your picture") }
            return@launch
        }
        _uiState.update {
            it.copy(avatarPreviewBytes = imageBytes, errorMessage = null)
        }
        when (val upload = repository.uploadProfileImage(user.id, imageBytes)) {
            is Result.Error -> {
                _uiState.update { it.copy(errorMessage = upload.message) }
            }
            is Result.Success -> {
                when (val save = repository.updateProfileImage(upload.data)) {
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                profileImageUrl = upload.data,
                                avatarPreviewBytes = null,
                                errorMessage = null
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(errorMessage = save.message) }
                    }
                }
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.getUserProfile()) {
                is Result.Success -> {
                    val user = result.data
                    _uiState.update {
                        it.copy(
                            username = user.username,
                            email = user.email,
                            age = if (user.age > 0) user.age.toString() else "",
                            phoneNumber = user.phoneNumber,
                            gender = user.gender,
                            profileImageUrl = user.profileImageUrl,
                            isLoading = false
                        )
                    }
                }
                is Result.Error -> {
                    val fallback = repository.currentUser()
                    _uiState.update {
                        it.copy(
                            username = fallback?.username.orEmpty(),
                            email = fallback?.email.orEmpty(),
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    private fun saveProfile() {
        val state = _uiState.value
        val usernameError = RegisterValidator.validateUsername(state.username.trim())
        val ageError = validateAge(state.age)
        val phoneError = validatePhone(state.phoneNumber)
        // Gender is optional — no required check.

        if (usernameError != null || ageError != null || phoneError != null) {
            _uiState.update {
                it.copy(
                    usernameError = usernameError,
                    ageError = ageError,
                    phoneError = phoneError,
                    genderError = null
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            when (
                val result = repository.updateUserProfile(
                    username = state.username.trim(),
                    age = state.age.toIntOrNull(),
                    phoneNumber = state.phoneNumber.trim(),
                    gender = state.gender
                )
            ) {
                is Result.Success -> _uiState.update {
                    it.copy(isSaving = false, successMessage = "Profile saved")
                }
                is Result.Error -> _uiState.update {
                    it.copy(isSaving = false, errorMessage = result.message)
                }
            }
        }
    }

    private fun validateAge(age: String): String? {
        if (age.isBlank()) return null
        val value = age.toIntOrNull() ?: return "Enter a valid age"
        return if (value !in 1..120) "Age must be between 1 and 120" else null
    }

    private fun validatePhone(phone: String): String? {
        val trimmed = phone.trim()
        if (trimmed.isBlank()) return null
        if (trimmed.any { it.isWhitespace() }) {
            return "No spacing between the number"
        }
        if (trimmed.any { !it.isDigit() && it != '-' }) {
            return "Phone number can only contain digits and symbol \"-\""
        }
        val digits = trimmed.filter { it.isDigit() }
        return when {
            digits.length < 8 -> "Phone number is too short"
            digits.length > 15 -> "Phone number is too long"
            else -> null
        }
    }
}
