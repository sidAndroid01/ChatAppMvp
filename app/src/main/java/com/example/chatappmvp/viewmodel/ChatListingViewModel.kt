package com.example.chatappmvp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatappmvp.data.repository.ChatRepository
import com.example.chatappmvp.utils.ChatListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListingViewModel @Inject constructor(private val repository: ChatRepository
) : ViewModel() {

    init {
        observeChats()
    }

    private val _uiState = MutableStateFlow<ChatListUiState>(ChatListUiState.Loading)
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    private fun observeChats() {
        viewModelScope.launch {
            repository.getAllChats()
                .catch { exception ->
                    _uiState.value = ChatListUiState.Error(
                        exception.message ?: "Unknown error occurred"
                    )
                }
                .collect { chatList ->
                    _uiState.value = when {
                        chatList.isEmpty() -> ChatListUiState.Empty
                        else -> ChatListUiState.Success(chatList)
                    }
                }
        }
    }

}