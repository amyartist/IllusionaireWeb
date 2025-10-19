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
                // If the audio is already loaded enough, resolve immediately.
                if (audio.readyState >= 4) { // HAVE_ENOUGH_DATA
                    resolve(Unit)
                    return@Promise
                }

                audio.oncanplaythrough = {
                    resolve(Unit)
                }

                audio.onerror = { _, _, _, _, _ ->
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
}