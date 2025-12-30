package com.example.chatappmvp.data.model

import androidx.room.Embedded

data class FileAttachment(
    val path: String,
    val fileSize: Long,
    @Embedded(prefix = "thumbnail_")
    val thumbnail: Thumbnail? = null
)

data class Thumbnail(
    val path: String
)