package com.example.assignment.ui.feedback

sealed interface FeedbackEvent {
    data class RatingSelected(val rating: Int) : FeedbackEvent
    data class CategorySelected(val category: String) : FeedbackEvent
    data class MessageChanged(val value: String) : FeedbackEvent
    data class ContactEmailChanged(val value: String) : FeedbackEvent
    data object CategoryClicked : FeedbackEvent
    data object CategoryDismissed : FeedbackEvent
    data object SubmitClicked : FeedbackEvent
    data object BackClicked : FeedbackEvent
    data object NavigationHandled : FeedbackEvent
    data object MessageShown : FeedbackEvent
}
