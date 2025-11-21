package com.magpie.data.ai

import android.content.Context
import com.magpie.data.HuntItem
import com.magpie.data.HuntRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Orchestrates AI-powered hunt generation.
 * Handles the flow from Gemini API through validation to storage.
 */
class HuntGenerator(context: Context, apiKey: String) {
    private val geminiService = GeminiService(context, apiKey)
    private val huntRepository = HuntRepository(context)

    suspend fun generateHuntForLocation(
        location: String,
        itemCount: Int = 9
    ): GenerationResult = withContext(Dispatchers.IO) {
        try {
            // Call Gemini to generate items
            val generatedHunt = geminiService.generateHuntItems(location, itemCount)

            // Check if content passed safety validation
            if (!generatedHunt.isSafe) {
                return@withContext GenerationResult(
                    success = false,
                    items = emptyList(),
                    error = "Content failed safety validation: ${generatedHunt.flaggedContent}",
                    requiresReview = true
                )
            }

            // Convert to HuntItem format
            val huntItems = generatedHunt.items.mapIndexed { index, itemData ->
                HuntItem(
                    id = index + 1,
                    name = itemData.name,
                    imageUrl = "", // TODO: Image generation/search in future stage
                    funFact = itemData.funFact,
                    isFound = false
                )
            }

            GenerationResult(
                success = true,
                items = huntItems,
                error = null,
                requiresReview = false
            )
        } catch (e: Exception) {
            GenerationResult(
                success = false,
                items = emptyList(),
                error = "Generation failed: ${e.message}",
                requiresReview = false
            )
        }
    }

    data class GenerationResult(
        val success: Boolean,
        val items: List<HuntItem>,
        val error: String? = null,
        val requiresReview: Boolean = false
    )
}
