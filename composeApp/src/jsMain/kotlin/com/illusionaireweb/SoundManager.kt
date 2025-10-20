package com.illusionaireweb

import kotlinx.coroutines.await
import org.w3c.dom.Audio
import kotlin.js.Promise

/**
 * A singleton object to manage loading, caching, and playing sound effects.
 * Includes a preloading mechanism to ensure instant playback on first use.
 */
object SoundManager {
    private val soundCache = mutableMapOf<String, Audio>()
    private var currentMusic: Audio? = null

    /**
     * Preloads a list of sound files so they are ready for instant playback.
     * This should be called once when the game starts.
     *
     * @param soundNames A list of sound file names (e.g., "select") without extensions.
     */
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

    /**
     * Plays a sound effect from the cache. Assumes the sound has been preloaded.
     *
     * @param soundName The base name of the sound file (e.g., "select").
     */
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
        // If this music is already playing, do nothing.
        if (currentMusic?.src?.contains("$soundName.wav") == true) {
            return
        }

        // Stop any music that is currently playing before starting the new one.
        stopLoop()

        val music = soundCache[soundName]
        if (music == null) {
            console.error("Music '$soundName' not preloaded. Cannot play.")
            return
        }

        try {
            music.loop = true
            // --- THIS IS THE CHANGE ---
            // The play() function returns a Promise. We handle its rejection.
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
    /**
     * Stops the currently playing background music.
     */
    fun stopLoop() {
        currentMusic?.let {
            it.pause()
            it.currentTime = 0.0
        }
        currentMusic = null
    }
}