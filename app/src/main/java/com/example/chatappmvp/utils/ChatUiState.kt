package com.example.chatappmvp.utils

sealed interface ChatUiState<out T> {
    object Loading : ChatUiState<Nothing>
    data class Success<T>(val data: T) : ChatUiState<T>
    object Empty : ChatUiState<Nothing>
    data class Error(val message: String) : ChatUiState<Nothing>
}