package com.magpie.data

import android.content.Context
import com.magpie.ui.screens.Difficulty
import org.json.JSONArray
import org.json.JSONObject

data class Hunt(
    val name: String,
    val location: String,
    val difficulty: Difficulty,
    val items: List<HuntItem>
)

class HuntRepository(private val context: Context) {
    fun getHuntByDifficulty(difficulty: Difficulty): Hunt {
        val resourceId = when (difficulty) {
            Difficulty.TODDLER -> com.magpie.R.raw.hunt_toddler
            Difficulty.EXPLORER -> com.magpie.R.raw.hunt_explorer
            Difficulty.EXPERT -> com.magpie.R.raw.hunt_expert
        }

        val inputStream = context.resources.openRawResource(resourceId)
        val json = inputStream.bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(json)

        return parseHunt(jsonObject)
    }

    private fun parseHunt(jsonObject: JSONObject): Hunt {
        val name = jsonObject.getString("name")
        val location = jsonObject.getString("location")
        val difficulty = Difficulty.valueOf(jsonObject.getString("difficulty"))
        val itemsArray = jsonObject.getJSONArray("items")

        val items = mutableListOf<HuntItem>()
        for (i in 0 until itemsArray.length()) {
            val itemJson = itemsArray.getJSONObject(i)
            items.add(
                HuntItem(
                    id = itemJson.getInt("id"),
                    name = itemJson.getString("name"),
                    imageUrl = itemJson.getString("imageUrl"),
                    funFact = itemJson.getString("funFact"),
                    isFound = false
                )
            )
        }

        return Hunt(
            name = name,
            location = location,
            difficulty = difficulty,
            items = items
        )
    }
}
