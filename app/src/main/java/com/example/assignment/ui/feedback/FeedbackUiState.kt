package com.example.assignment.ui.feedback

data class FeedbackUiState(
    val rating: Int? = null,
    val category: String = "",
    val message: String = "",
    val contactEmail: String = "",
    val isCategoryMenuOpen: Boolean = false,
    val isSubmitting: Boolean = false,
    val ratingError: String? = null,
    val categoryError: String? = null,
    val messageError: String? = null,
    val emailError: String? = null,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val navigateBack: Boolean = false
) {
    companion object {
        val Categories = listOf(
            "App Experience",
            "Legal Information",
            "Chat Assistant",
            "Document Scanner",
            "Other"
        )
    }
}
