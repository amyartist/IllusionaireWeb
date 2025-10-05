package com.amyartist.illusionaireweb.data

import android.content.Context
import android.util.Log
import com.amyartist.illusionaireweb.utils.saveBitmapToGalleryWithSpecificName
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ImagenAspectRatio
import com.google.firebase.ai.type.ImagenGenerationConfig
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.collections.iterator

sealed interface GeminiUiState {
    object Idle : GeminiUiState
    object Loading : GeminiUiState
    data class Success(val successMessge: String) : GeminiUiState
    data class Error(val errorMessage: String) : GeminiUiState
}

private val _uiState = MutableStateFlow<GeminiUiState>(GeminiUiState.Idle)
val currentRoom: MutableStateFlow<Room?> = MutableStateFlow(gameRooms["starting_room"])

@OptIn(PublicPreviewAPI::class)
suspend fun generateAndSaveRoomImages(context: Context) {
    val config = generationConfig {
    }

    val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .imagenModel(
            modelName = "imagen-3.0-generate-002",
            generationConfig = ImagenGenerationConfig(
                numberOfImages = 1,
                aspectRatio = ImagenAspectRatio.SQUARE_1x1
            )
        )

    var successCount = 0
    var failureCount = 0

    for ((roomId, room) in gameRooms) {
        Log.d("GeminiAI", "Generating image for room: $roomId ('${room.name}') with prompt: '${room.prompt}'")
        try {
            val imageResponse = model.generateImages(prompt = room.prompt)
            val aiResponseBitmap = imageResponse.images.first()

            Log.d("GeminiAI", "Image generated successfully for room: $roomId")

            val success =
                saveBitmapToGalleryWithSpecificName(context, aiResponseBitmap.asBitmap(), roomId)
            if (success) {
                Log.i("GeminiAI", "Image for room '$roomId' saved to gallery.")
                successCount++
            } else {
                Log.e("GeminiAI", "Failed to save image to gallery for room: $roomId")
                failureCount++
            }

            _uiState.value = GeminiUiState.Success("Image for ${room.name} generated.")
        } catch (e: Exception) {
            Log.e("GeminiAI", "Error generating image for room: $roomId. Message: ${e.message}", e)
            e.printStackTrace()
            failureCount++
            _uiState.value = GeminiUiState.Error("Error for ${room.name}: ${e.message}")
        }
        // Add a small delay between API calls if you're hitting rate limits,
        delay(500)
    }

    Log.d("GeminiAI", "Batch image generation finished. Success: $successCount, Failures: $failureCount")

    if (failureCount == 0 && successCount > 0) {
        _uiState.value = GeminiUiState.Success("All room images generated and saved!")
    } else {
        _uiState.value = GeminiUiState.Error("Batch finished with $failureCount failures.")
    }
}
