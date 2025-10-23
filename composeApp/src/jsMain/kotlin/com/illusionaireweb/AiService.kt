package com.illusionaireweb

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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

    private val backendUrl = "https://cartoonminiboss.com"

    suspend fun getRiddle(theme: String): String? {
        console.log("Requesting riddle with theme '$theme' from backend service.")
        return try {
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