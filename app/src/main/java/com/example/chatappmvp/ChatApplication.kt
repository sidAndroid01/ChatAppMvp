package com.example.chatappmvp

import android.app.Application
import android.util.Log
import com.example.chatappmvp.data.repository.ChatRepository
import com.example.chatappmvp.utils.SeedDataProvider
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ChatApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Inject
    lateinit var repository: ChatRepository

    override fun onCreate() {
        super.onCreate()

        initializeSeedData()
    }

    private fun initializeSeedData() {
        applicationScope.launch {
            try {
                val hasChats = repository.hasChats()

                if (!hasChats) {
                    val seedChats = SeedDataProvider.getSeedChats()
                    val seedMessages = SeedDataProvider.getAllSeedMessages()

                    repository.insertSeedData(seedChats, seedMessages)
                    Log.d("###", "ChatAppMvp: Seed data initialized successfully")
                }
            } catch (e: Exception) {
                Log.d("###", "ChatAppMvp: Error in populating the seed data")
                e.printStackTrace()
            }
        }
    }
}