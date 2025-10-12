package com.illusionaireweb

import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.Text

/**
 * Injects the game screen into a specified container element on the page.
 * @param containerId The ID of the div element where the game should be embedded.
 */
fun showGameScreen(containerId: String) {
    // 1. Find the host container element by its ID.
    val hostContainer = document.getElementById(containerId) as? HTMLElement

    if (hostContainer != null) {
        hostContainer.style.display = "flex"
        hostContainer.style.alignItems = "center"
        hostContainer.style.justifyContent = "center"
        hostContainer.style.width = "100%"
        hostContainer.style.height = "100%"

        // Main Game Container
        val gameContainer = document.createElement("div") as HTMLDivElement
        with(gameContainer.style) {
            maxWidth = "1024px"
            maxHeight = "1126.4px" // 1024px * 1.1
            width = "min(100%, 100vw, 90vh * (1 / 1.1))"
            setProperty("aspect-ratio", "1 / 1.1")
            border = "2px solid yellow"
            backgroundColor = "black"
        }

        val textNode: Text = document.createTextNode("Hello, world!")
        gameContainer.appendChild(textNode)




        hostContainer.innerHTML = ""
        hostContainer.appendChild(gameContainer)
    } else {
        console.error("IllusionaireWeb Error: Container element with ID '$containerId' was not found.")
    }
}
