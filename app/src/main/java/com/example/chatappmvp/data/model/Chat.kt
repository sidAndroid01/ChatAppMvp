package com.example.chatappmvp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class Chat(
    @PrimaryKey
    val id: String,
    val title: String,
    val lastMessage: String,
    val lastMessageTimestamp: Long,
    val createdAt: Long,
    val updatedAt: Long
)