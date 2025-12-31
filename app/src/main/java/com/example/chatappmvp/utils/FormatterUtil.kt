package com.example.chatappmvp.utils

import android.text.format.DateUtils.isToday
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.log10
import kotlin.math.pow

object FormatterUtil {
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

    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"

        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()

        val size = bytes / 1024.0.pow(digitGroups.toDouble())

        return if (digitGroups == 0) {
            "${bytes.toInt()} ${units[digitGroups]}"
        } else {
            "%.1f ${units[digitGroups]}".format(size)
        }
    }

    fun formatMessageTime(timestamp: Long): String {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val timeStr = timeFormat.format(Date(timestamp))

        return when {
            isToday(timestamp) -> timeStr
            isYesterday(timestamp) -> "Yesterday, $timeStr"
            isThisWeek(timestamp) -> {
                val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                "${dayFormat.format(Date(timestamp))}, $timeStr"
            }
            isThisYear(timestamp) -> {
                val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
                "${dateFormat.format(Date(timestamp))}, $timeStr"
            }
            else -> {
                val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                "${dateFormat.format(Date(timestamp))}, $timeStr"
            }
        }
    }

    private fun isThisWeek(timestamp: Long): Boolean {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        val days = TimeUnit.MILLISECONDS.toDays(diff)
        return days < 7
    }
}