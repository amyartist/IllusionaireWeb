package com.illusionaireweb

import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.Text

/**
 * Manages the display of the game screen.
 */
fun showGameScreen() {
    val body = document.body
    if (body != null) {
        // Ensure the body and html take up the full screen height and have no margin.
        // Cast documentElement to HTMLElement to access the style property.
        (document.documentElement as? HTMLElement)?.style?.height = "100%"
        body.style.height = "100%"
        body.style.margin = "0"
        body.style.display = "flex" // Use flexbox for easy centering
        body.style.alignItems = "center" // Vertical centering
        body.style.justifyContent = "center" // Horizontal centering

        // 1. Create a new div element to act as the game container.
        val gameContainer = document.createElement("div") as HTMLDivElement

        // 2. Apply styles to the div.
        with(gameContainer.style) {
            // Set a maximum width and height. The aspect ratio is 1:1.1.
            maxWidth = "1024px"
            maxHeight = "1126.4px" // 1024px * 1.1

            // Use viewport units to ensure the container fits on screen.
            // It will be 100% of the viewport width OR 90% of the viewport height,
            // whichever is smaller, maintaining the aspect ratio.
            width = "min(100vw, 90vh * (1 / 1.1))"

            // Maintain a 1:1.1 aspect ratio using setProperty.
            setProperty("aspect-ratio", "1 / 1.1")

            // Add a thin, yellow border.
            border = "2px solid yellow"

            // The flex properties on the body now handle centering,
            // so margin is no longer needed for that.
        }

        // 3. Create the text node.
        val textNode: Text = document.createTextNode("Hello, world!")

        // 4. Add the text inside the new div.
        gameContainer.appendChild(textNode)

        // 5. Append the styled div to the body of the page.
        body.appendChild(gameContainer)

    } else {
        println("Error: document.body is not available.")
    }
}
