package com.illusionaireweb

import kotlinx.coroutines.await
import org.w3c.dom.Audio
import kotlin.js.Promise

object SoundManager {
    private val soundCache = mutableMapOf<String, Audio>()
    private var currentMusic: Audio? = null

    suspend fun preloadSounds(soundNames: List<String>) {
        console.log("Preloading sounds: $soundNames")
        val promises = soundNames.map { soundName ->
            val audio = soundCache.getOrPut(soundName) {
                Audio("sounds/$soundName.wav")
            }

            Promise { resolve, reject ->
                if (audio.readyState >= 4) {
                    console.log("Sound '$soundName' was already cached and loaded.")
                    resolve(Unit)
                    return@Promise
                }

                audio.oncanplaythrough = {
                    console.log("Successfully preloaded sound: '$soundName.wav'")
                    resolve(Unit)
                }

                audio.onerror = { _, _, _, _, _ ->
                    val errorMessage = "Failed to load sound: '$soundName.wav'. Check if the file exists in 'resources/sounds' and the name is correct."
                    console.error(errorMessage)
                    reject(Error("Failed to load sound: $soundName"))
                }
            }
        }

        Promise.all(promises.toTypedArray()).await()
        console.log("All sounds preloaded successfully!")
    }

    fun play(soundName: String) {
        val sound = soundCache[soundName]
        if (sound == null) {
            console.error("Sound '$soundName' not preloaded. Cannot play.")
            return
        }

        try {
            sound.currentTime = 0.0
            sound.play()
        } catch (e: dynamic) {
            console.error("Error playing sound '$soundName':", e)
        }
    }

    fun playLoop(soundName: String) {
        if (currentMusic?.src?.contains("$soundName.wav") == true) {
            return
        }

        stopLoop()

        val music = soundCache[soundName]
        if (music == null) {
            console.error("Music '$soundName' not preloaded. Cannot play.")
            return
        }

        try {
            music.loop = true
            music.play().catch { error ->
                console.warn(
                    "Could not play background music due to browser policy. " +
                            "Waiting for user interaction. Error: ", error
                )
            }
            currentMusic = music // Keep track of the current music
        } catch (e: dynamic) {
            console.error("Error playing looping music '$soundName':", e)
        }
    }

    fun stopLoop() {
        currentMusic?.let {
            it.pause()
            it.currentTime = 0.0
        }
        currentMusic = null
    }
}