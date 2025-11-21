package com.magpie.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
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

// Extension to convert HuntItem list to JSON string
fun List<HuntItem>.toJsonString(): String {
    return this.joinToString(",") { item ->
        """{"id":${item.id},"name":"${item.name.replace("\"", "\\\"")}","imageUrl":"${item.imageUrl.replace("\"", "\\\"")}","funFact":"${item.funFact.replace("\"", "\\\"")}","isFound":${item.isFound}}"""
    }.let { "[$it]" }
}

// Extension to parse JSON string back to HuntItem list
fun String.toHuntItemList(): List<HuntItem> {
    return try {
        val itemRegex = "\"id\":(\\d+),\"name\":\"([^\"]*)\",\"imageUrl\":\"([^\"]*)\",\"funFact\":\"([^\"]*)\",\"isFound\":(true|false)".toRegex()
        val matches = itemRegex.findAll(this)
        matches.map { match ->
            HuntItem(
                id = match.groupValues[1].toInt(),
                name = match.groupValues[2],
                imageUrl = match.groupValues[3],
                funFact = match.groupValues[4],
                isFound = match.groupValues[5].toBoolean()
            )
        }.toList()
    } catch (e: Exception) {
        emptyList()
    }
}
