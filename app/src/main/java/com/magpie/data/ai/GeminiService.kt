package com.magpie.data.ai

import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.magpie.data.db.AIContentLog
import com.magpie.data.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiService(context: Context, private val apiKey: String) {
    private val database = AppDatabase.getDatabase(context)
    private val safetyValidator = SafetyValidator()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey,
        safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE)
        )
    )

    data class GeneratedHunt(
        val items: List<HuntItemData>,
        val isSafe: Boolean,
        val flaggedContent: String? = null
    )

    data class HuntItemData(
        val name: String,
        val funFact: String
    )

    suspend fun generateHuntItems(
        location: String,
        itemCount: Int = 9
    ): GeneratedHunt = withContext(Dispatchers.IO) {
        try {
            val prompt = buildSafetyPrompt(location, itemCount)
            val response = generativeModel.generateContent(prompt)
            val responseText = response.text ?: ""

            // Parse response into items
            val items = parseHuntItems(responseText)

            // Validate content
            val validationResult = safetyValidator.validateItems(
                items.map { "${it.name} - ${it.funFact}" }
            )

            // Log the interaction
            val log = AIContentLog(
                location = location,
                prompt = prompt,
                response = responseText,
                itemsGenerated = items.size,
                safetyCheckPassed = validationResult.isSafe,
                flaggedContent = if (!validationResult.isSafe) validationResult.reason else null
            )
            database.aiContentLogDao().insertLog(log)

            GeneratedHunt(
                items = items,
                isSafe = validationResult.isSafe,
                flaggedContent = validationResult.reason
            )
        } catch (e: Exception) {
            // Log error and return empty hunt
            GeneratedHunt(
                items = emptyList(),
                isSafe = false,
                flaggedContent = "Error: ${e.message}"
            )
        }
    }

    private fun buildSafetyPrompt(location: String, itemCount: Int): String {
        return """
            You are creating a family-friendly scavenger hunt for children aged 5-12.
            
            CRITICAL SAFETY REQUIREMENTS:
            - ALL content MUST be age-appropriate and educational
            - NO violence, weapons, dangerous activities, or adult themes
            - NO references to drugs, alcohol, or harmful substances
            - NO scary or disturbing content
            - Focus on nature, landmarks, architecture, and cultural items
            - Use simple, child-friendly language
            
            Generate exactly $itemCount scavenger hunt items for: $location
            
            Format your response as a JSON array with this structure:
            [
              {"name": "Item Name", "funFact": "An interesting, educational fact about this item"},
              ...
            ]
            
            Return ONLY the JSON array, no other text.
        """.trimIndent()
    }

    private fun parseHuntItems(responseText: String): List<HuntItemData> {
        return try {
            // Remove markdown code blocks if present
            val cleanedText = responseText
                .replace("```json", "")
                .replace("```", "")
                .trim()

            // Simple JSON parsing (in production, use a proper JSON library)
            val itemRegex = "\"name\"\\s*:\\s*\"([^\"]+)\",\\s*\"funFact\"\\s*:\\s*\"([^\"]+)\"".toRegex()
            val matches = itemRegex.findAll(cleanedText)

            matches.map { match ->
                HuntItemData(
                    name = match.groupValues[1],
                    funFact = match.groupValues[2]
                )
            }.toList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
