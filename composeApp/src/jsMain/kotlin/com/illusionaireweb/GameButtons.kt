package com.illusionaireweb

import kotlinx.browser.document
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement

/**
 * Creates the main container for the action buttons.
 * This container is centered at the bottom of the game area.
 *
 * @return An HTMLDivElement that will hold the action buttons.
 */
fun createButtonsContainer(): HTMLDivElement {
    val buttonsContainer = document.createElement("div") as HTMLDivElement
    buttonsContainer.id = "buttons-container"

    with(buttonsContainer.style) {
        position = "absolute"
        bottom = "20px"
        display = "flex"
        flexWrap = "wrap"
        flexDirection = "row"
        justifyContent = "center"
        alignItems = "center"
        columnGap = "15px"
        setProperty("row-gap", "10px")
        padding = "10px"
        boxSizing = "border-box"
        left = "0"
        width = "100%"
    }
    return buttonsContainer
}

/**
 * Clears and then rebuilds the buttons based on the actions available in the current room.
 *
 * @param container The container element created by `createButtonsContainer`.
 * @param actions The list of `Action` objects for the current room.
 * @param onActionClick A lambda function to be called when a button is clicked, passing the action's ID.
 */
fun updateGameButtons(container: HTMLDivElement, actions: List<Action>, onActionClick: (String) -> Unit) {
    container.innerHTML = ""

    actions.forEach { action ->
        val button = document.createElement("button") as HTMLButtonElement
        with(button.style) {
            // Background image styling
            backgroundImage = "url('images/button1.png')"
            backgroundSize = "cover"
            backgroundPosition = "center"
            backgroundRepeat = "no-repeat"

            // Sizing and layout
            width = "200px"
            maxWidth = "200px"
            height = "60px"
            border = "none"
            backgroundColor = "transparent"

            // Text styling
//            color = "#B8860B"
            color = "#B1800A"
            fontSize = "16px"
            fontWeight = "bold"
            fontFamily = "serif"

            // Make it behave like a button
            cursor = "pointer"
        }

        // Set the button's text content from the action
        button.textContent = when (action.type) {
            ActionType.LOOK -> "Look Around"
            ActionType.OPEN -> "Open ${action.item}"
            ActionType.GO -> "Go ${action.direction}"
        }

        // Add the click listener
        button.onclick = {
            onActionClick(action.id)
        }

        container.appendChild(button)
    }
}
