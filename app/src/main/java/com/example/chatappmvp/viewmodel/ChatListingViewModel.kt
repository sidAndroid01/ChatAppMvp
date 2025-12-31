package com.example.chatappmvp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatappmvp.data.model.Chat
import com.example.chatappmvp.data.repository.ChatRepository
import com.example.chatappmvp.utils.ChatUiState
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

    private val _uiState: MutableStateFlow<ChatUiState<List<Chat>>> =
        MutableStateFlow(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState<List<Chat>>> = _uiState.asStateFlow()

    private fun observeChats() {
        viewModelScope.launch {
            repository.getAllChats()
                .catch { exception ->
                    _uiState.value = ChatUiState.Error(
                        exception.message ?: "Unknown error occurred"
                    )
                }
                .collect { chatList ->
                    if(chatList.isEmpty()) {
                        _uiState.value= ChatUiState.Empty
                    }
                    else {
                        _uiState.value= ChatUiState.Success(chatList)
                    }
                }
        }
    }

}