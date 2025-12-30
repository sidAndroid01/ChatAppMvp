package com.example.chatappmvp.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = Chat::class,
            parentColumns = ["id"],
            childColumns = ["chatId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["chatId"])]
)
data class Message(
    @PrimaryKey
    val id: String,
    val chatId: String,
    val message: String,
    val type: MessageType,
    @Embedded(prefix = "file_")
    val file: FileAttachment? = null,
    val sender: MessageSender,
    val timestamp: Long
)