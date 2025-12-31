package com.example.chatappmvp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.chatappmvp.data.model.Chat
import com.example.chatappmvp.data.model.FileAttachment
import com.example.chatappmvp.data.model.Message
import com.example.chatappmvp.data.model.MessageSender
import com.example.chatappmvp.data.model.MessageType
import com.example.chatappmvp.data.model.Thumbnail
import com.example.chatappmvp.data.offline.ChatDatabase
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    val dataBase: ChatDatabase
) {

    fun getAllChats(): Flow<List<Chat>> {
        return dataBase.chatDao().getAllChatsFlow()
    }

    fun getMessagesForChat(chatId: String): Flow<List<Message>> {
        return dataBase.messageDao().getMessagesForChatFlow(chatId)
    }

    fun getMessagesForChatPaged(chatId: String): Flow<PagingData<Message>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 5,
                enablePlaceholders = false,
                initialLoadSize = 15
            ),
            pagingSourceFactory = { dataBase.messageDao().getMessagesForChatPaging(chatId) }
        ).flow
    }

    fun getChatById(chatId: String): Flow<Chat?> {
        return dataBase.chatDao().getChatFlowById(chatId)
    }

    fun getAllChatsPaging(): Flow<PagingData<Chat>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = { dataBase.chatDao().getAllChatsPaging() }
        ).flow
    }

    suspend fun hasChats(): Boolean {
        return dataBase.chatDao().getChatCount() > 0
    }

    suspend fun getUserMessageCount(chatId: String): Int {
        return dataBase.messageDao().getUserMessageCount(chatId)
    }

    suspend fun insertSeedData(chats: List<Chat>, messages: List<Message>) {
        dataBase.chatDao().insertChats(chats)
        dataBase.messageDao().insertMessages(messages)
    }

    suspend fun sendTextMessage(
        chatId: String,
        text: String,
        sender: MessageSender
    ): String {
        val messageId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()

        val message = Message(
            id = messageId,
            chatId = chatId,
            message = text,
            type = MessageType.TEXT,
            file = null,
            sender = sender,
            timestamp = timestamp
        )

        dataBase.messageDao().insertMessage(message)
        updateChatAfterMessage(chatId, text, timestamp, sender)

        return messageId
    }

    private suspend fun updateChatAfterMessage(
        chatId: String,
        messageText: String,
        timestamp: Long,
        sender: MessageSender
    ) {
        val chat = dataBase.chatDao().getChatById(chatId) ?: return

        val newTitle = if (chat.title == "New Chat" && sender == MessageSender.USER) {
            messageText.take(50).trim().ifEmpty { "New Chat" }
        } else {
            chat.title
        }

        val previewText = messageText.take(100)

        dataBase.chatDao().updateChat(
            chat.copy(
                title = newTitle,
                lastMessage = previewText,
                lastMessageTimestamp = timestamp,
                updatedAt = timestamp
            )
        )
    }

    suspend fun updateChatTitle(chatId: String, newTitle: String) {
        val chat = dataBase.chatDao().getChatById(chatId) ?: return
        dataBase.chatDao().updateChat(chat.copy(title = newTitle, updatedAt = System.currentTimeMillis()))
    }

    suspend fun sendFileMessage(
        chatId: String,
        filePath: String,
        fileSize: Long,
        thumbnailPath: String? = null,
        caption: String = "",
        sender: MessageSender
    ): String {
        val messageId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()

        val message = Message(
            id = messageId,
            chatId = chatId,
            message = caption,
            type = MessageType.FILE,
            file = FileAttachment(
                path = filePath,
                fileSize = fileSize,
                thumbnail = thumbnailPath?.let { Thumbnail(it) }
            ),
            sender = sender,
            timestamp = timestamp
        )

        dataBase.messageDao().insertMessage(message)

        val previewText = caption.ifBlank { "Photo" }
        updateChatAfterMessage(chatId, previewText, timestamp, sender)

        return messageId
    }

    suspend fun createChat(): String {
        val chatId = UUID.randomUUID().toString()
        val currentTime = System.currentTimeMillis()

        val newChat = Chat(
            id = chatId,
            title = "New Chat",
            lastMessage = "",
            lastMessageTimestamp = currentTime,
            createdAt = currentTime,
            updatedAt = currentTime
        )

        dataBase.chatDao().insertChat(newChat)
        return chatId
    }
}