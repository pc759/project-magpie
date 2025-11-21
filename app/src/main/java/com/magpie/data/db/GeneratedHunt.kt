package com.magpie.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.magpie.data.HuntItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "generated_hunts")
data class GeneratedHunt(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val location: String,
    val difficulty: String,
    val itemCount: Int,
    val items: String, // JSON serialized list of HuntItems
    val isComplete: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastPlayedAt: LocalDateTime? = null,
    val completedAt: LocalDateTime? = null
)

data class HuntItemData(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val funFact: String,
    val isFound: Boolean = false
)

// Extension to convert HuntItem list to JSON string using Gson
fun List<HuntItem>.toJsonString(): String {
    return Gson().toJson(this)
}

// Extension to parse JSON string back to HuntItem list using Gson
fun String.toHuntItemList(): List<HuntItem> {
    return try {
        val type = object : TypeToken<List<HuntItem>>() {}.type
        Gson().fromJson(this, type) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
}
