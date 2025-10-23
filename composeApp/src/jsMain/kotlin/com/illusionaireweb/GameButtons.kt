package com.illusionaireweb

import kotlinx.browser.document
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement

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

fun updateGameButtons(container: HTMLDivElement, actions: List<Action>, onActionClick: (String) -> Unit) {
    container.innerHTML = ""

    actions.forEach { action ->
        val button = document.createElement("button") as HTMLButtonElement
        with(button.style) {
            backgroundImage = "url('images/button1.png')"
            backgroundSize = "cover"
            backgroundPosition = "center"
            backgroundRepeat = "no-repeat"
            width = "200px"
            maxWidth = "200px"
            height = "60px"
            border = "none"
            backgroundColor = "transparent"
            color = GameColors.BUTTON_TEXT_GOLD
            fontSize = "16px"
            fontWeight = "bold"
            fontFamily = "serif"
            cursor = "pointer"
        }

        button.textContent = when (action.type) {
            ActionType.LOOK -> "Look Around"
            ActionType.OPEN -> "Open ${action.item}"
            ActionType.GO -> "Go ${action.direction}"
        }

        button.onclick = {
            SoundManager.play("select")
            onActionClick(action.id)
        }

        container.appendChild(button)
    }
}
