package com.illusionaireweb

import org.w3c.dom.Audio

/**
 * A singleton object to manage loading and playing sound effects.
 * It caches Audio objects to prevent re-loading and allows for rapid re-triggering of sounds.
 */
object SoundManager {
    private val soundCache = mutableMapOf<String, Audio>()

    /**
     * Plays a sound effect.
     *
     * @param soundName The base name of the sound file (e.g., "select") without the extension.
     *                  The function assumes a ".wav" extension.
     */
    fun play(soundName: String) {
        try {
            val sound = soundCache.getOrPut(soundName) {
                // If the sound is not in the cache, create a new Audio object and add it.
                console.log("Loading sound: $soundName")
                Audio("sounds/$soundName.wav")
            }

            // By setting currentTime to 0, we can replay the sound even if it's already playing.
            sound.currentTime = 0.0
            sound.play()
        } catch (e: dynamic) {
            console.error("Error playing sound '$soundName':", e)
        }
    }
}