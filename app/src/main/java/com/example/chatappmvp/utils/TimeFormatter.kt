package com.example.chatappmvp.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object TimeFormatter {
    fun formatChatListTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        if (diff < 0) return "Just now"

        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            isYesterday(timestamp) -> "Yesterday"
            days < 7 -> SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(timestamp))
            isThisYear(timestamp) -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
            else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
        }
    }

    private fun isYesterday(timestamp: Long): Boolean {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = calendar.get(Calendar.DAY_OF_YEAR)
        val yesterdayYear = calendar.get(Calendar.YEAR)

        calendar.timeInMillis = timestamp
        val messageDay = calendar.get(Calendar.DAY_OF_YEAR)
        val messageYear = calendar.get(Calendar.YEAR)

        return yesterday == messageDay && yesterdayYear == messageYear
    }

    private fun isThisYear(timestamp: Long): Boolean {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)

        calendar.timeInMillis = timestamp
        val messageYear = calendar.get(Calendar.YEAR)

        return currentYear == messageYear
    }
}