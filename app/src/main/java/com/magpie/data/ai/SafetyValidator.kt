package com.magpie.data.ai

/**
 * Dual-layer safety validation for AI-generated content.
 * Layer 1: Gemini's built-in safety filters (handled by API)
 * Layer 2: Custom keyword-based validation
 */
class SafetyValidator {
    private val blockedKeywords = setOf(
        // Violence
        "kill", "murder", "stab", "shoot", "gun", "weapon", "attack", "fight", "blood",
        "death", "die", "dead", "hurt", "injury", "injured", "violence", "violent",
        
        // Adult/Sexual content
        "sex", "sexual", "nude", "naked", "adult", "porn", "xxx", "inappropriate",
        "abuse", "assault", "rape",
        
        // Dangerous activities
        "drug", "drugs", "cocaine", "heroin", "meth", "alcohol", "drunk", "intoxicated",
        "suicide", "self-harm", "cutting", "overdose",
        
        // Hate speech / discrimination
        "hate", "racist", "racism", "sexist", "sexism", "homophobic", "transphobic",
        
        // Profanity (basic filter)
        "damn", "hell", "crap"
    )

    data class ValidationResult(
        val isSafe: Boolean,
        val flaggedTerms: List<String> = emptyList(),
        val reason: String? = null
    )

    fun validateContent(content: String): ValidationResult {
        val lowerContent = content.lowercase()
        val foundFlaggedTerms = mutableListOf<String>()

        for (keyword in blockedKeywords) {
            if (lowerContent.contains(keyword)) {
                foundFlaggedTerms.add(keyword)
            }
        }

        return if (foundFlaggedTerms.isEmpty()) {
            ValidationResult(isSafe = true)
        } else {
            ValidationResult(
                isSafe = false,
                flaggedTerms = foundFlaggedTerms,
                reason = "Content contains flagged terms: ${foundFlaggedTerms.joinToString(", ")}"
            )
        }
    }

    fun validateItems(items: List<String>): ValidationResult {
        val allContent = items.joinToString(" ")
        return validateContent(allContent)
    }
}
