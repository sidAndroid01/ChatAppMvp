package com.example.chatappmvp.utils

import com.example.chatappmvp.data.model.Chat

sealed class ChatListUiState {
    object Loading : ChatListUiState()
    data class Success(val chats: List<Chat>) : ChatListUiState()
    object Empty : ChatListUiState()
    data class Error(val message: String) : ChatListUiState()
}