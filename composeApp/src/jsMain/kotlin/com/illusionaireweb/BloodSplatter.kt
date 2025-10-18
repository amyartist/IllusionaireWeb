package com.illusionaireweb

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLImageElement

/**
 * Creates the blood splatter image element and styles it.
 * The element is hidden by default.
 *
 * @return The HTMLImageElement for the blood splatter.
 */
fun createBloodSplatterElement(): HTMLImageElement {
    val splatterImage = document.createElement("img") as HTMLImageElement
    splatterImage.src = "images/blood_splatter.png"
    splatterImage.id = "blood-splatter-effect" // Give it an ID for clarity

    with(splatterImage.style) {
        position = "absolute"
        // Center it within its parent container (the monster display)
        top = "50%"
        left = "50%"
        transform = "translate(-50%, -50%)"

        width = "300px"
        height = "auto"
        opacity = "0.8"
        zIndex = "20"

        // This prevents the image from interfering with mouse clicks on buttons underneath it
        setProperty("pointer-events", "none")

        // Start hidden
        display = "none"
    }

    return splatterImage
}

/**
 * Triggers the blood splatter effect.
 * It makes the image visible, then uses a timer to hide it again.
 *
 * @param splatterImage The element created by `createBloodSplatterElement`.
 */
fun showBloodSplatterEffect(splatterImage: HTMLImageElement) {
    splatterImage.style.display = "block"
    window.setTimeout({
        splatterImage.style.display = "none"
    }, 1200)
}