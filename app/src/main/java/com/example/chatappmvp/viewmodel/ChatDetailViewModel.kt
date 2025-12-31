package com.example.chatappmvp.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.chatappmvp.data.model.Chat
import com.example.chatappmvp.data.model.Message
import com.example.chatappmvp.data.model.MessageSender
import com.example.chatappmvp.data.repository.ChatRepository
import com.example.chatappmvp.utils.ChatUiState
import com.example.chatappmvp.utils.aisimulator.AIAgentSimulator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ChatRepository,
    private val aiAgentSimulator: AIAgentSimulator
) : ViewModel() {

    private val chatId: String = savedStateHandle.get<String>("chatId")
        ?: throw IllegalStateException("chatId is required")

    private val _uiState = MutableStateFlow<ChatUiState<List<Message>>>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState<List<Message>>> = _uiState.asStateFlow()

    private val _currChat = MutableStateFlow<ChatUiState<Chat>>(ChatUiState.Loading)
    val currChat: StateFlow<ChatUiState<Chat>> = _currChat.asStateFlow()

    private val _isEditingTitle = MutableStateFlow(false)
    val isEditingTitle: StateFlow<Boolean> = _isEditingTitle.asStateFlow()

    private val _editedTitle = MutableStateFlow("")
    val editedTitle: StateFlow<String> = _editedTitle.asStateFlow()

    private val _cameraImageUri = MutableStateFlow<Uri?>(null)
    val cameraImageUri: StateFlow<Uri?> = _cameraImageUri.asStateFlow()

    private val _fullscreenImage = MutableStateFlow<String?>(null)
    val fullscreenImage: StateFlow<String?> = _fullscreenImage.asStateFlow()

    val messages: Flow<PagingData<Message>> = repository.getMessagesForChatPaged(chatId)
        .cachedIn(viewModelScope)

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

    fun sendMessage() {
        Log.d("###", "sendMessage: sending mssge $messageText")
        val text = messageText.value.trim()
        if (text.isEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.sendTextMessage(
                    chatId = chatId,
                    text = text,
                    sender = MessageSender.USER
                )
                messageText.value = ""
                aiAgentSimulator.processMessageAndMaybeReply(
                    chatId = chatId,
                    isUserMessage = true
                )
            } catch (e: Exception) {
                _uiState.value = ChatUiState.Error("Failed to send message: ${e.message}")
            }
        }
    }

    fun startEditingTitle() {
        val currentChatState = _currChat.value

        if (currentChatState is ChatUiState.Success) {
            _editedTitle.value = currentChatState.data.title
            _isEditingTitle.value = true
        }
    }

    fun saveTitle() {
        viewModelScope.launch {
            try {
                val newTitle = _editedTitle.value.trim()
                if (newTitle.isNotEmpty()) {
                    repository.updateChatTitle(chatId, newTitle)
                }
                _isEditingTitle.value = false
            } catch (e: Exception) {
                _uiState.value = ChatUiState.Error("Failed to update title: ${e.message}")
            }
        }
    }

    fun cancelEditingTitle() {
        _isEditingTitle.value = false
    }

    fun updateEditedTitle(newTitle: String) {
        _editedTitle.value = newTitle
    }

    fun sendImageMessage(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            try {
                val file = copyImageToInternalStorage(context, imageUri)
                val fileSize = file.length()

                val filePath = file.absolutePath

                repository.sendFileMessage(
                    chatId = chatId,
                    filePath = filePath,
                    fileSize = fileSize,
                    thumbnailPath = filePath,
                    caption = "",
                    sender = MessageSender.USER
                )

                aiAgentSimulator.processMessageAndMaybeReply(
                    chatId = chatId,
                    isUserMessage = true
                )
            } catch (e: Exception) {
                _uiState.value = ChatUiState.Error("Failed to send image: ${e.message}")
            }
        }
    }

    private fun copyImageToInternalStorage(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Failed to open image")

        val timestamp = System.currentTimeMillis()
        val file = File(context.filesDir, "image_$timestamp.jpg")

        FileOutputStream(file).use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        inputStream.close()
        return file
    }

    fun createCameraImageUri(context: Context): Uri {
        val timestamp = System.currentTimeMillis()
        val fileName = "camera_${timestamp}.jpg"

        val picturesDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "")
        if (!picturesDir.exists()) {
            picturesDir.mkdirs()
        }

        val imageFile = File(picturesDir, fileName)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )

        _cameraImageUri.value = uri

        return uri
    }

    fun handleCameraPhoto(context: Context) {
        viewModelScope.launch {
            try {
                _cameraImageUri.value?.let { uri ->
                    sendImageMessage(context, uri)

                    _cameraImageUri.value = null
                }
            } catch (e: Exception) {
                _uiState.value = ChatUiState.Error(
                    "Failed to process camera photo: ${e.message}"
                )
            }
        }
    }

    fun showImageFullscreen(imagePath: String) {
        _fullscreenImage.value = imagePath
    }

    fun closeImageFullscreen() {
        _fullscreenImage.value = null
    }
}