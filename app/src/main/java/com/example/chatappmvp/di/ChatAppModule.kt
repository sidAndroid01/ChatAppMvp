package com.example.chatappmvp.di

import android.content.Context
import androidx.room.Room
import com.example.chatappmvp.data.offline.ChatDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ChatAppModule {

    @Provides
    @Singleton
    fun provideDatabaseName(): String = "chatDatabase"

    @Provides
    @Singleton
    fun provideChatDatabase(
        @ApplicationContext context: Context,
        databaseName: String
    ): ChatDatabase {
        return Room.databaseBuilder(
            context,
            ChatDatabase::class.java,
            databaseName
        ).build()
    }
}