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
        // --- Host Container Styling ---
        // Make the host a flex container to center the game canvas.
        hostContainer.style.display = "flex"
        hostContainer.style.alignItems = "center"
        hostContainer.style.justifyContent = "center"
        // Ensure it can fill the space it's given.
        hostContainer.style.width = "100%"
        hostContainer.style.height = "100%"

        // --- Game Container Creation ---
        // 2. Create the main game div.
        val gameContainer = document.createElement("div") as HTMLDivElement

        // 3. Apply styles to the game div.
        with(gameContainer.style) {
            // Set a maximum size.
            maxWidth = "1024px"
            maxHeight = "1126.4px" // 1024px * 1.1

            // Use viewport units AND percentage to ensure it fits inside the host container.
            // '100%' refers to the parent (hostContainer), 'vw/vh' to the viewport.
            // This ensures it scales within the host but never overflows the screen.
            width = "min(100%, 100vw, 90vh * (1 / 1.1))"

            // Maintain a 1:1.1 aspect ratio.
            setProperty("aspect-ratio", "1 / 1.1")

            // Add border.
            border = "2px solid yellow"
        }

        // 4. Create the "Hello, world!" text.
        val textNode: Text = document.createTextNode("Hello, world!")
        gameContainer.appendChild(textNode)

        // 5. Clear the host container and append the new game container.
        hostContainer.innerHTML = "" // Clear any placeholder content
        hostContainer.appendChild(gameContainer)

    } else {
        // If the container ID is not found, log an error.
        console.error("IllusionaireWeb Error: Container element with ID '$containerId' was not found.")
    }
}
