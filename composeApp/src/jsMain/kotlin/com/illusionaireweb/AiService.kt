package com.illusionaireweb

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Data classes to match the Gemini REST API JSON structure
@Serializable
data class GeminiRequest(val contents: List<Content>)
@Serializable
data class Content(val parts: List<Part>)
@Serializable
data class Part(val text: String)

@Serializable
data class GeminiResponse(val candidates: List<Candidate>)
@Serializable
data class Candidate(val content: Content)


class AiService {

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // The API returns more fields than we need
            })
        }
    }

    private val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$GEMINI_API_KEY"

    private suspend fun generateContent(prompt: String): String? {
        return try {
            val requestBody = GeminiRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt))))
            )

            val response: GeminiResponse = httpClient.post(endpoint) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

            response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
        } catch (e: Exception) {
            console.error("Error calling Gemini REST API: ${e.message}")
            null
        }
    }

    suspend fun getRiddle(theme: String): String? {
        // Create a new, dynamic prompt using the theme.
        // We also add instructions for brevity and tone.
        val prompt = """
            Ask me a challenging but fair riddle.
            The riddle's theme should be related to a "$theme".
            The riddle should be no more than three sentences long.
            Do not reveal the answer.
        """.trimIndent()

        console.log("New riddle prompt sent to AI: $prompt") // Good for debugging
        return generateContent(prompt)
    }

    suspend fun checkRiddleAnswer(riddle: String, userAnswer: String): Boolean {
        // This new prompt gives the AI the necessary context to make a correct judgment.
        val prompt = """
            The riddle was: "$riddle"
            My answer is: "$userAnswer"
            Is my answer correct for the riddle provided? Please answer only with the single word "yes" or "no".
        """.trimIndent()

        val responseText = generateContent(prompt)?.trim()?.lowercase() ?: "no"
        console.log("Gemini's answer validation: '$responseText'")
        // Using .contains("yes") is a bit safer than a direct equals check
        return responseText.contains("yes")
    }}