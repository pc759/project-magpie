package com.magpie.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "ai_content_logs")
data class AIContentLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val location: String,
    val prompt: String,
    val response: String,
    val itemsGenerated: Int,
    val safetyCheckPassed: Boolean,
    val flaggedContent: String? = null,
    val userReported: Boolean = false,
    val reportReason: String? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }
}
