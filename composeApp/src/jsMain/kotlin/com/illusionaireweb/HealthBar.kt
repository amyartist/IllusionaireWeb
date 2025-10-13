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
//        position = "absolute"
//        top = "10px"
//        left = "10px"
        width = "200px"
        height = "25px"
        border = "2px solid ${GameColors.BORDER_YELLOW}"
        backgroundColor = GameColors.HEALTH_BAR_BACKGROUND
        borderRadius = "5px"
        boxSizing = "border-box"
        display = "block"
    }

    // 2. Create the inner bar (the green fill)
    val healthFill = document.createElement("div") as HTMLDivElement
    healthFill.id = "health-fill"
    with(healthFill.style) {
        height = "100%"
        backgroundColor = GameColors.HEALTH_BAR_FILL
        borderRadius = "2px"
        width = "100%"
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
