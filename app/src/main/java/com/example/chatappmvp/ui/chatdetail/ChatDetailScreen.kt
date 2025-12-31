package com.example.chatappmvp.ui.chatdetail

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatappmvp.R
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
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
    val isEditingTitle by viewModel.isEditingTitle.collectAsState()
    val editedTitle by viewModel.editedTitle.collectAsState()
    val messageText by viewModel.messageText
    var showImageOptions by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val messages = (messagesState as? ChatUiState.Success)?.data ?: emptyList()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.sendImageMessage(context, it) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            viewModel.handleCameraPhoto(context)
        } else {
            Log.d("###", "ChatDetailScreen: Camera capture cancelled or failed")
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    ChatDetailScreenView(
        messagesState,
        chat,
        listState,
        isEditingTitle,
        editedTitle,
        onBackClick,
        messageText,
        showImageOptions,
        onAttachClick = {
            showImageOptions = true
        },
        onGalleryClick = {
            Log.d("###", "ChatDetailScreen: gallery clicked")
            showImageOptions = false
            galleryLauncher.launch("image/*")
        },
        onCameraClick = {
            Log.d("###", "ChatDetailScreen: camera clicked")
            showImageOptions = false
            // Create a URI for the camera to save the photo to
            val photoUri = viewModel.createCameraImageUri(context)
            // Launch camera with the URI
            cameraLauncher.launch(photoUri)
        },
        onDismiss = {
            Log.d("###", "ChatDetailScreen: dismissed")
            showImageOptions = false
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreenView(
    messagesState: ChatUiState<List<Message>>,
    chatState: ChatUiState<Chat>,
    listState: LazyListState,
    isEditingTitle: Boolean,
    editedTitle: String,
    onBackClick:()->Unit,
    messageText: String,
    showImageOptions: Boolean,
    onAttachClick: () -> Unit,
    onGalleryClick: ()->Unit,
    onCameraClick: () -> Unit,
    onDismiss: ()->Unit,
    viewModel: ChatDetailViewModel = hiltViewModel<ChatDetailViewModel>()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isEditingTitle) {
                        OutlinedTextField(
                            value = editedTitle,
                            onValueChange = { viewModel.updateEditedTitle(it) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    } else {
                        val currChatTitle = if(chatState is ChatUiState.Success) chatState.data.title
                        else "Chat"
                        Text(
                            text = currChatTitle,
                            modifier = Modifier.clickable { viewModel.startEditingTitle() }
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (isEditingTitle) {
                        IconButton(onClick = { viewModel.saveTitle() }) {
                            Icon(Icons.Default.Edit, "Save")
                        }
                        IconButton(onClick = { viewModel.cancelEditingTitle() }) {
                            Icon(Icons.Default.Close, "Cancel")
                        }
                    } else {
                        IconButton(onClick = { viewModel.startEditingTitle() }) {
                            Icon(Icons.Default.Edit, "Edit Title")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            MessageInputBar(
                messageText = messageText,
                onMessageChange = { viewModel.messageText.value = it },
                onSendClick = { viewModel.sendMessage() },
                onAttachClick = onAttachClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
    }

    if (showImageOptions) {
        ImagePickerDialog(
            onGalleryClick = {
                onGalleryClick()
            },
            onCameraClick = {
                onCameraClick()
            },
            onDismiss = {
                onDismiss()
            }
        )
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
                            val imageRequest = ImageRequest.Builder(LocalContext.current)
                                .data(file.path)
                                .crossfade(true)
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .error(R.drawable.ic_error_img)
                                .build()
                            val painter = rememberAsyncImagePainter(model = imageRequest)
                            Image(
                                painter = painter,
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

@Composable
private fun MessageInputBar(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onAttachClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding(),
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = onMessageChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                placeholder = { Text("Type a message...") },
                maxLines = 4,
                shape = RoundedCornerShape(24.dp)
            )

            IconButton(onClick = onAttachClick) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Attach image"
                )
            }

            IconButton(
                onClick = onSendClick,
                enabled = messageText.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send message",
                    tint = if (messageText.isNotBlank()) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    }
                )
            }
        }
    }
}

@Composable
private fun ImagePickerDialog(
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Image") },
        text = {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp))
            {
                TextButton(
                    onClick = onGalleryClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Choose from Gallery",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                TextButton(
                    onClick = onCameraClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Take Photo",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
