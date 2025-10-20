// main.kt
package com.illusionaireweb

import kotlinx.browser.document

/**
 * Main entry point. This function looks for a container on the host page
 * and injects the game into it.
 */
fun main() {
    document.addEventListener("DOMContentLoaded", {
        // The ID of the div on the host website where the game will be embedded.
        val gameContainerId = "illusionaire-web-container"
        console.log("Kotlin main function has started.")

        showGameScreen(gameContainerId)
    })
}
