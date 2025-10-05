package com.amyartist.illusionaireweb

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amyartist.illusionaireweb.data.generateAndSaveRoomImages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseViewModel : ViewModel() {

//    private val _uiState = MutableStateFlow<GeminiUiState>(GeminiUiState.Idle)
//    val uiState: StateFlow<GeminiUiState> = _uiState.asStateFlow()
//
fun generateRooms(context: Context) { // Accept Context here
    viewModelScope.launch(Dispatchers.IO) {
        try {
            Log.d("FirebaseViewModel", "generateRooms called. Starting image generation...")
            generateAndSaveRoomImages(context.applicationContext) // Pass applicationContext
            Log.d("FirebaseViewModel", "Image generation process finished.")
        } catch (e: Exception) {
            Log.e("FirebaseViewModel", "Error in generateRooms: ${e.message}", e)
        }
    }
}
//    suspend fun sendImageAndPromptToAI(capturedImage: Bitmap, fixedPrompt: String) {
//        try {
//            val config = generationConfig {
////                temperature = 0.9f // Example: A bit more creative
////                topK = 1 // Example: Only consider the most likely next token (greedy decoding)
////                topP = 0.95f // Example: Consider tokens that make up 95% of the probability mass
////                candidateCount = 1
////                maxOutputTokens = 2048 // Example
////                stopSequences =
////                    listOf(".", "?", "!") // Example: Stop if a sentence terminator is generated
//                responseModalities = listOf(ResponseModality.TEXT, ResponseModality.IMAGE)
//            }
//
//            val model = Firebase.ai(backend = GenerativeBackend.googleAI())
//                .generativeModel(
//                    "gemini-2.0-flash-preview-image-generation",
//                    config
//                )
//
//            val inputContent = content {
//                image(capturedImage)
//                text(fixedPrompt)
//            }
//
//            val response = model.generateContent(inputContent)
//            println("AI Response: ${response.text}")
//
//            val aiResponseBitmap: Bitmap? = response.candidates.firstOrNull()
//                ?.content?.parts?.firstOrNull()
//                ?.asImageOrNull()
//
//            if (aiResponseBitmap != null) {
//                _uiState.value = GeminiUiState.Success(aiResponseBitmap) // This is now safe
//            } else {
//                val errorTextFromResponse = response.text // Check if there's any text explanation
//                _uiState.value = GeminiUiState.Error(
//                    errorTextFromResponse ?: "AI did not return a valid image."
//                )
//                println("AI response did not contain a valid image. Response text: ${response.text}")
//            }
//
//        } catch (e: Exception) {
//            println("Oh no! Error interacting with Firebase AI: ${e.message}")
//            e.printStackTrace()
//        }
//    }
}

//sealed interface GeminiUiState {
//    object Idle : GeminiUiState
//    object Loading : GeminiUiState
//    data class Success(val outputBitmap: Bitmap) : GeminiUiState
//    data class Error(val errorMessage: String) : GeminiUiState
//}
