package com.example.chatappmvp.utils

import com.example.chatappmvp.data.model.Chat
import com.example.chatappmvp.data.model.FileAttachment
import com.example.chatappmvp.data.model.Message
import com.example.chatappmvp.data.model.MessageSender
import com.example.chatappmvp.data.model.MessageType
import com.example.chatappmvp.data.model.Thumbnail

object SeedDataProvider {
    fun getSeedChats(): List<Chat> {
        return listOf(
            Chat(
                id = "chat-001",
                title = "Mumbai Flight Booking",
                lastMessage = "The second option looks perfect! How do I proceed?",
                lastMessageTimestamp = 1703520480000L,
                createdAt = 1703520000000L,
                updatedAt = 1703520480000L
            ),
            Chat(
                id = "chat-002",
                title = "Hotel Reservation Help",
                lastMessage = "I've found 5 hotels in that area. Here's a comparison.",
                lastMessageTimestamp = 1703450000000L,
                createdAt = 1703440000000L,
                updatedAt = 1703450000000L
            ),
            Chat(
                id = "chat-003",
                title = "Restaurant Recommendations",
                lastMessage = "Thanks! I'll check them out.",
                lastMessageTimestamp = 1703380000000L,
                createdAt = 1703370000000L,
                updatedAt = 1703380000000L
            )
        )
    }

    fun getSeedMessagesForChat001(): List<Message> {
        return listOf(
            Message(
                id = "msg-001",
                chatId = "chat-001",
                message = "Hi! I need help booking a flight to Mumbai.",
                type = MessageType.TEXT,
                file = null,
                sender = MessageSender.USER,
                timestamp = 1703520000000L
            ),
            Message(
                id = "msg-002",
                chatId = "chat-001",
                message = "Hello! I'd be happy to help you book a flight to Mumbai. When are you planning to travel?",
                type = MessageType.TEXT,
                file = null,
                sender = MessageSender.AGENT,
                timestamp = 1703520030000L
            ),
            Message(
                id = "msg-003",
                chatId = "chat-001",
                message = "Next Friday, December 29th.",
                type = MessageType.TEXT,
                file = null,
                sender = MessageSender.USER,
                timestamp = 1703520090000L
            ),
            Message(
                id = "msg-004",
                chatId = "chat-001",
                message = "Great! And when would you like to return?",
                type = MessageType.TEXT,
                file = null,
                sender = MessageSender.AGENT,
                timestamp = 1703520120000L
            ),
            Message(
                id = "msg-005",
                chatId = "chat-001",
                message = "January 5th. Also, I prefer morning flights.",
                type = MessageType.TEXT,
                file = null,
                sender = MessageSender.USER,
                timestamp = 1703520180000L
            ),
            Message(
                id = "msg-006",
                chatId = "chat-001",
                message = "Perfect! Let me search for morning flights from your location to Mumbai. Could you also share your departure city?",
                type = MessageType.TEXT,
                file = null,
                sender = MessageSender.AGENT,
                timestamp = 1703520210000L
            ),
            Message(
                id = "msg-007",
                chatId = "chat-001",
                message = "Bangalore. Here's a screenshot of my preferred airline.",
                type = MessageType.FILE,
                file = FileAttachment(
                    path = "https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=400",
                    fileSize = 245680L,
                    thumbnail = Thumbnail("https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=100")
                ),
                sender = MessageSender.USER,
                timestamp = 1703520300000L
            ),
            Message(
                id = "msg-008",
                chatId = "chat-001",
                message = "Thanks for sharing! I can see you prefer IndiGo. Let me find the best options for you.",
                type = MessageType.TEXT,
                file = null,
                sender = MessageSender.AGENT,
                timestamp = 1703520330000L
            ),
            Message(
                id = "msg-009",
                chatId = "chat-001",
                message = "Flight options comparison",
                type = MessageType.FILE,
                file = FileAttachment(
                    path = "https://images.unsplash.com/photo-1464037866556-6812c9d1c72e?w=400",
                    fileSize = 189420L,
                    thumbnail = Thumbnail("https://images.unsplash.com/photo-1464037866556-6812c9d1c72e?w=100")
                ),
                sender = MessageSender.AGENT,
                timestamp = 1703520420000L
            ),
            Message(
                id = "msg-010",
                chatId = "chat-001",
                message = "The second option looks perfect! How do I proceed?",
                type = MessageType.TEXT,
                file = null,
                sender = MessageSender.USER,
                timestamp = 1703520480000L
            )
        )
    }

    fun getSeedMessagesForChat002(): List<Message> {
        return listOf(
            Message(
                id = "msg-011",
                chatId = "chat-002",
                message = "I need a hotel near Mumbai airport for 3 nights.",
                type = MessageType.TEXT,
                file = null,
                sender = MessageSender.USER,
                timestamp = 1703440000000L
            ),
            Message(
                id = "msg-012",
                chatId = "chat-002",
                message = "Sure! What's your budget range and preferred star rating?",
                type = MessageType.TEXT,
                file = null,
                sender = MessageSender.AGENT,
                timestamp = 1703440030000L
            ),
            Message(
                id = "msg-013",
                chatId = "chat-002",
                message = "Around ₹5000 per night, 4-star would be great.",
                type = MessageType.TEXT,
                file = null,
                sender = MessageSender.USER,
                timestamp = 1703440090000L
            ),
            Message(
                id = "msg-014",
                chatId = "chat-002",
                message = "I've found 5 hotels in that area. Here's a comparison.",
                type = MessageType.TEXT,
                file = null,
                sender = MessageSender.AGENT,
                timestamp = 1703450000000L
            )
        )
    }

    fun getSeedMessagesForChat003(): List<Message> {
        return listOf(
            Message(
                id = "msg-015",
                chatId = "chat-003",
                message = "Can you recommend some good restaurants in Bangalore?",
                type = MessageType.TEXT,
                file = null,
                sender = MessageSender.USER,
                timestamp = 1703370000000L
            ),
            Message(
                id = "msg-016",
                chatId = "chat-003",
                message = "Of course! What type of cuisine are you interested in?",
                type = MessageType.TEXT,
                file = null,
                sender = MessageSender.AGENT,
                timestamp = 1703370030000L
            ),
            Message(
                id = "msg-017",
                chatId = "chat-003",
                message = "I love Indian and Italian food!",
                type = MessageType.TEXT,
                file = null,
                sender = MessageSender.USER,
                timestamp = 1703370090000L
            ),
            Message(
                id = "msg-018",
                chatId = "chat-003",
                message = "Great choices! Here are my top recommendations: Karavalli for authentic coastal Indian cuisine, and Toscano for excellent Italian dishes. Both have great ambiance and are highly rated!",
                type = MessageType.TEXT,
                file = null,
                sender = MessageSender.AGENT,
                timestamp = 1703370150000L
            ),
            Message(
                id = "msg-019",
                chatId = "chat-003",
                message = "Thanks! I'll check them out.",
                type = MessageType.TEXT,
                file = null,
                sender = MessageSender.USER,
                timestamp = 1703380000000L
            )
        )
    }

    fun getAllSeedMessages(): List<Message> {
        return getSeedMessagesForChat001() +
                getSeedMessagesForChat002() +
                getSeedMessagesForChat003()
    }
}