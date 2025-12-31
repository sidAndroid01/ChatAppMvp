package com.example.chatappmvp.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatappmvp.data.model.Chat
import com.example.chatappmvp.data.model.Message
import com.example.chatappmvp.data.repository.ChatRepository
import com.example.chatappmvp.utils.ChatUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ChatRepository,
) : ViewModel() {

    private val chatId: String = savedStateHandle.get<String>("chatId")
        ?: throw IllegalStateException("chatId is required")

    private val _uiState = MutableStateFlow<ChatUiState<List<Message>>>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState<List<Message>>> = _uiState.asStateFlow()

    private val _currChat = MutableStateFlow<ChatUiState<Chat>>(ChatUiState.Loading)
    val currChat: StateFlow<ChatUiState<Chat>> = _currChat.asStateFlow()



    val messageText = mutableStateOf("")

    init {
        observeMessages()
        observeChat()
    }

    private fun observeMessages() {
        Log.d("###", "observeMessages: inside this with chatId as $chatId")
        viewModelScope.launch(Dispatchers.IO) {
            repository.getMessagesForChat(chatId)
                .catch { exception ->
                    _uiState.value = ChatUiState.Error(
                        exception.message ?: "Unknown error occurred"
                    )
                }
                .collect { messages ->
                    _uiState.value = ChatUiState.Success(messages)
                }
        }
    }

    private fun observeChat() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getChatById(chatId)
                .catch { exception ->
                    _currChat.value = ChatUiState.Error(
                        exception.message ?: "Unknown error occurred"
                    )
                }
                .collect { chat ->
                    if(chat==null) {
                        _currChat.value = ChatUiState.Error(
                            "Chat is null"
                        )
                    }
                    else {
                        _currChat.value = ChatUiState.Success(chat)
                    }
                }
        }
    }

}