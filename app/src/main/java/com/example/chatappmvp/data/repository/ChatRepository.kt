package com.example.chatappmvp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.chatappmvp.data.model.Chat
import com.example.chatappmvp.data.offline.ChatDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepository @Inject constructor(
    val dataBase: ChatDatabase
) {

    fun getAllChats(): Flow<List<Chat>> {
        return dataBase.chatDao().getAllChatsFlow()
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
}