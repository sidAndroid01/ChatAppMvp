package com.example.chatappmvp.utils.aisimulator

import android.util.Log
import com.example.chatappmvp.data.model.MessageSender
import com.example.chatappmvp.data.repository.ChatRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class AIAgentSimulator @Inject constructor(
    private val repository: ChatRepository
) {
    private val textResponses = listOf(
        "I'm looking into that for you.",
        "Let me check the details.",
        "Got it! I'll help you with that.",
        "That's a great question. Here's what I found...",
        "I've processed your request.",
    )

    private val placeholderImages = listOf(
        "https://picsum.photos/400/300",
        "https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=400",
        "https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=100",
        "https://images.unsplash.com/photo-1464037866556-6812c9d1c72e?w=400",
        "https://images.unsplash.com/photo-1464037866556-6812c9d1c72e?w=100"
    )

    suspend fun processMessageAndMaybeReply(
        chatId: String,
        isUserMessage: Boolean
    ) {
        Log.d("AIAgentSimulator", "processMessageAndMaybeReply: coming to this")
        if (!isUserMessage) return
        Log.d("AIAgentSimulator", "processMessageAndMaybeReply: coming to this going beyond !isUserMessage")

        val currentUserMessageCount = repository.getUserMessageCount(chatId)
        Log.d("AIAgentSimulator", "processMessageAndMaybeReply: coming to this currentUserMessageCount $currentUserMessageCount")

        val replyThreshold = Random.nextInt(4, 8)

        Log.d("AIAgentSimulator", "processMessageAndMaybeReply: coming to this reply threshold $replyThreshold")
        Log.d("AIAgentSimulator", "processMessageAndMaybeReply: coming to this should reply ${currentUserMessageCount % replyThreshold == 0}")
        if (currentUserMessageCount % replyThreshold == 0) {
            Log.d("AIAgentSimulator", "processMessageAndMaybeReply: coming inside should reply")
            val thinkingDelay = Random.nextLong(1000, 2000)
            delay(thinkingDelay)

            val replyType = if (Random.nextDouble() <= 0.7) "text"
            else "file"

            Log.d("AIAgentSimulator", "processMessageAndMaybeReply: coming inside replyType $replyType")

            when(replyType) {
                "file" -> {
                    val imageUrl = placeholderImages.random()
                    repository.sendFileMessage(
                        chatId = chatId,
                        filePath = imageUrl,
                        fileSize = Random.nextLong(100000, 500000),
                        thumbnailPath = imageUrl,
                        caption = "",
                        sender = MessageSender.AGENT
                    )
                }
                "text" -> {
                    val response = textResponses.random()
                    repository.sendTextMessage(
                        chatId = chatId,
                        text = response,
                        sender = MessageSender.AGENT
                    )
                }
            }
        }
    }
}