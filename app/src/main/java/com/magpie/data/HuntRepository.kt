package com.magpie.data

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.gson.Gson
import com.magpie.data.db.AppDatabase
import com.magpie.data.db.GeneratedHunt
import com.magpie.data.db.toHuntItemList
import com.magpie.data.db.toJsonString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

data class Hunt(
    val id: String,
    val name: String,
    val location: String,
    val items: List<HuntItem>
)

data class HuntItem(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val funFact: String,
    val isFound: Boolean = false
)

class HuntRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val gson = Gson()

    fun getHuntByDifficulty(difficulty: com.magpie.ui.screens.Difficulty): Hunt {
        val resourceName = when (difficulty) {
            com.magpie.ui.screens.Difficulty.TODDLER -> "hunt_toddler"
            com.magpie.ui.screens.Difficulty.EXPLORER -> "hunt_explorer"
            com.magpie.ui.screens.Difficulty.EXPERT -> "hunt_expert"
        }

        val inputStream = context.resources.openRawResource(
            context.resources.getIdentifier(resourceName, "raw", context.packageName)
        )
        val json = inputStream.bufferedReader().use { it.readText() }
        return gson.fromJson(json, Hunt::class.java)
    }

    suspend fun saveGeneratedHunt(
        location: String,
        difficulty: String,
        items: List<HuntItem>
    ): Long = withContext(Dispatchers.IO) {
        val hunt = GeneratedHunt(
            location = location,
            difficulty = difficulty,
            itemCount = items.size,
            items = items.toJsonString()
        )
        database.generatedHuntDao().insertHunt(hunt)
    }

    suspend fun updateHuntProgress(
        huntId: Int,
        items: List<HuntItem>
    ) = withContext(Dispatchers.IO) {
        val hunt = database.generatedHuntDao().getHuntById(huntId)
        if (hunt != null) {
            val isComplete = items.all { it.isFound }
            val updatedHunt = hunt.copy(
                items = items.toJsonString(),
                isComplete = isComplete,
                lastPlayedAt = LocalDateTime.now(),
                completedAt = if (isComplete) LocalDateTime.now() else hunt.completedAt
            )
            database.generatedHuntDao().updateHunt(updatedHunt)
        }
    }

    suspend fun getLatestIncompleteHunt(): GeneratedHunt? = withContext(Dispatchers.IO) {
        database.generatedHuntDao().getLatestIncompleteHunt()
    }

    suspend fun getHuntById(id: Int): GeneratedHunt? = withContext(Dispatchers.IO) {
        database.generatedHuntDao().getHuntById(id)
    }

    suspend fun getAllHunts(): List<GeneratedHunt> = withContext(Dispatchers.IO) {
        database.generatedHuntDao().getAllHunts()
    }

    suspend fun deleteHunt(id: Int) = withContext(Dispatchers.IO) {
        database.generatedHuntDao().deleteHunt(id)
    }
}
