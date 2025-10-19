package com.illusionaireweb

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Data classes to match the backend's API
// Note: RiddleRequest is no longer needed for the frontend.
@Serializable
data class RiddleResponse(val riddle: String)

@Serializable
data class CheckAnswerRequest(val riddle: String, val userAnswer: String)

@Serializable
data class CheckAnswerResponse(val correct: Boolean)


class AiService {

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    // The backend service is running locally
    //private val backendUrl = "http://localhost:8080"
    private val backendUrl = "http://34.173.109.129:8080"

    suspend fun getRiddle(theme: String): String? {
        console.log("Requesting riddle with theme '$theme' from backend service.")
        return try {
            // **CHANGED**: Make a GET request to the /riddle endpoint,
            // passing the theme as a URL query parameter.
            val response: RiddleResponse = httpClient.get("$backendUrl/riddle") {
                url {
                    parameters.append("theme", theme)
                }
            }.body()

            response.riddle
        } catch (e: Exception) {
            console.error("Error calling backend service for a riddle: ${e.message}")
            "Sorry, I couldn't think of a riddle right now. Please try again."
        }
    }

    suspend fun checkRiddleAnswer(riddle: String, userAnswer: String): Boolean {
        console.log("Sending answer to backend for verification.")
        return try {
            // This remains a POST request, as we are sending data to be evaluated.
            val response: CheckAnswerResponse = httpClient.post("$backendUrl/check-answer") {
                contentType(ContentType.Application.Json)
                setBody(CheckAnswerRequest(riddle = riddle, userAnswer = userAnswer))
            }.body()

            response.correct
        } catch (e: Exception) {
            console.error("Error calling backend service to check an answer: ${e.message}")
            false
        }
    }
}