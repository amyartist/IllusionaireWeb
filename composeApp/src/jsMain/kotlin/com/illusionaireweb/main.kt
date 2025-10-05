package com.illusionaireweb

import kotlinx.browser.document
import org.w3c.dom.Text

/**
 * The main entry point for the web application.
 * This function will be executed when the JavaScript file is loaded in the browser.
 */
fun main() {
    // We will now interact with the document's body to add text to the page.

    // 1. Wait for the DOM to be fully loaded before trying to manipulate it.
    document.addEventListener("DOMContentLoaded", {
        // 2. Get the 'body' element of the HTML page.
        val body = document.body

        // 3. Check if the body exists to avoid errors.
        if (body != null) {
            // 4. Create a new text node with the content "Hello, world!".
            val textNode: Text = document.createTextNode("Hello, world!")

            // 5. Append the new text node to the body of the page.
            body.appendChild(textNode)
        } else {
            // Fallback in case the body isn't ready, prints to console.
            println("Error: document.body is not available.")
        }
    })
}
