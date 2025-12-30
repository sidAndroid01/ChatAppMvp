package com.example.chatappmvp.data.offline

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.example.chatappmvp.data.model.Chat
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats ORDER BY lastMessageTimestamp DESC")
    fun getAllChatsFlow(): Flow<List<Chat>>

    @Query("SELECT * FROM chats ORDER BY lastMessageTimestamp DESC")
    fun getAllChatsPaging(): PagingSource<Int, Chat>
}
