package com.example.chatappmvp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.chatappmvp.data.model.Chat
import com.example.chatappmvp.data.model.Message
import com.example.chatappmvp.data.offline.ChatDatabase
import kotlinx.coroutines.flow.Flow
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

    fun getChatById(chatId: String): Flow<Chat?> {
        return dataBase.chatDao().getChatById(chatId)
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

    suspend fun insertSeedData(chats: List<Chat>, messages: List<Message>) {
        dataBase.chatDao().insertChats(chats)
        dataBase.messageDao().insertMessages(messages)
    }
}