package com.illusionaireweb

import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import kotlin.math.max

/**
 * Creates the visual health bar element.
 *
 * @return A Div element representing the health bar container.
 */
fun createHealthBarElement(): HTMLDivElement {
    // 1. Create the outer container (the gold border)
    val healthBarContainer = document.createElement("div") as HTMLDivElement
    with(healthBarContainer.style) {
        position = "absolute" // Position it relative to the game container
        top = "10px"
        left = "10px"
        width = "200px"
        height = "25px"
        border = "2px solid gold"
        backgroundColor = "#333" // Dark background for the empty part of the bar
        borderRadius = "5px"
        boxSizing = "border-box" // Makes border and padding part of the element's total width and height
    }

    // 2. Create the inner bar (the green fill)
    val healthFill = document.createElement("div") as HTMLDivElement
    healthFill.id = "health-fill" // Give it an ID for easy access later
    with(healthFill.style) {
        height = "100%"
        backgroundColor = "green"
        borderRadius = "2px"
        width = "100%" // Start at 100% health
        // Add a smooth transition when the width changes
        transition = "width 0.5s ease-in-out"
    }

    // 3. Add the green fill inside the container and return it
    healthBarContainer.appendChild(healthFill)
    return healthBarContainer
}

fun updateHealthBar(healthBarContainer: HTMLDivElement, health: Int, maxHealth: Int = 100) {
    val healthFill = healthBarContainer.querySelector("#health-fill") as? HTMLDivElement
    if (healthFill != null) {
        val percentage = max(0.0, health.toDouble() / maxHealth * 100)
        healthFill.style.width = "$percentage%"
    }
}
