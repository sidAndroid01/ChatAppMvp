package com.example.chatappmvp.ui.chatdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.chatappmvp.data.model.Chat
import com.example.chatappmvp.data.model.Message
import com.example.chatappmvp.data.model.MessageSender
import com.example.chatappmvp.data.model.MessageType
import com.example.chatappmvp.utils.ChatUiState
import com.example.chatappmvp.utils.EmptyState
import com.example.chatappmvp.utils.ErrorState
import com.example.chatappmvp.utils.FormatterUtil
import com.example.chatappmvp.utils.LoadingState
import com.example.chatappmvp.viewmodel.ChatDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    viewModel: ChatDetailViewModel= hiltViewModel<ChatDetailViewModel>(),
    onBackClick: () -> Unit
) {

    val messagesState by viewModel.uiState.collectAsState()
    val chat by viewModel.currChat.collectAsState()
    val listState = rememberLazyListState()

    val messages = (messagesState as? ChatUiState.Success)?.data ?: emptyList()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    ChatDetailScreenView(messagesState, chat, listState)
}


@Composable
fun ChatDetailScreenView(
    messagesState: ChatUiState<List<Message>>,
    chatState: ChatUiState<Chat>,
    listState: LazyListState,
) {
    when (messagesState) {
        is ChatUiState.Loading -> {
            LoadingState("messages")
        }
        is ChatUiState.Success -> {
            val messages = messagesState.data
            if (messages.isEmpty()) {
                EmptyState("messages", "Start the conversation")
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = messages,
                        key = { message -> message.id }
                    ) { message ->
                        MessageItem(message) {}
                    }
                }
            }
        }
        is ChatUiState.Error -> {
            ErrorState("Error loading the messages")
        }
        is ChatUiState.Empty -> {
            EmptyState("messages", "Start the conversation")
        }
    }
}

@Composable
private fun MessageItem(
    message: Message,
    onImageClick: (String) -> Unit
) {
    val isUser = message.sender == MessageSender.USER
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bubbleColor = if (isUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        Card(
            modifier = Modifier.widthIn(max = 300.dp),
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                when (message.type) {
                    MessageType.TEXT -> {
                        Text(
                            text = message.message,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    MessageType.FILE -> {
                        message.file?.let { file ->
                            Image(
                                painter = rememberAsyncImagePainter(file.path),
                                contentDescription = "Shared image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onImageClick(file.path) },
                                contentScale = ContentScale.Crop
                            )
                            Text(
                                text = FormatterUtil.formatFileSize(file.fileSize),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            if (message.message.isNotBlank()) {
                                Text(
                                    text = message.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = FormatterUtil.formatMessageTime(message.timestamp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
        )
    }
}
