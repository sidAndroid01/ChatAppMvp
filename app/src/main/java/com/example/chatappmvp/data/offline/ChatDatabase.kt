package com.example.chatappmvp.data.offline

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.chatappmvp.data.model.Chat
import com.example.chatappmvp.data.model.Message

@Database(entities = [Chat::class, Message::class], version = 1, exportSchema = false)
abstract class ChatDatabase: RoomDatabase() {

    abstract fun chatDao(): ChatDao

    abstract fun messageDao(): MessageDao
}