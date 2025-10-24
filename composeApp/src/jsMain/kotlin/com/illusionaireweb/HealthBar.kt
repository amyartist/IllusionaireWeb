package com.illusionaireweb

import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import kotlin.math.max

fun createHealthBarElement(): HTMLDivElement {
    val healthBarContainer = document.createElement("div") as HTMLDivElement
    healthBarContainer.id = "health-bar-container"
    with(healthBarContainer.style) {
        width = "200px"
        height = "25px"
        border = "2px solid ${GameColors.BORDER_YELLOW}"
        backgroundColor = GameColors.HEALTH_BAR_BACKGROUND
        borderRadius = "5px"
        boxSizing = "border-box"
        display = "block"
    }

    val healthFill = document.createElement("div") as HTMLDivElement
    healthFill.id = "health-fill"
    with(healthFill.style) {
        height = "100%"
        backgroundColor = GameColors.HEALTH_BAR_FILL
        borderRadius = "2px"
        width = "100%"
        transition = "width 0.5s ease-in-out"
    }

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
