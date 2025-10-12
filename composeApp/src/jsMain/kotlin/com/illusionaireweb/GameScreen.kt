package com.illusionaireweb

import kotlinx.browser.document
import org.w3c.dom.Text

/**
 * Manages the display of the game screen.
 */
fun showGameScreen() {
    val body = document.body
    if (body != null) {
        val textNode: Text = document.createTextNode("Hello, world!")

        body.appendChild(textNode)
    } else {
        println("Error: document.body is not available.")
    }
}
