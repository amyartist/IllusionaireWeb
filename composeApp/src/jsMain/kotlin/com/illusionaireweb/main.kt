// main.kt
package com.illusionaireweb

import kotlinx.browser.document

fun main() {
    document.addEventListener("DOMContentLoaded", {
        val gameContainerId = "illusionaire-web-container"
        console.log("Kotlin main function has started.")

        showGameScreen(gameContainerId)
    })
}
