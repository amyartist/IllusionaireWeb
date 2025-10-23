package com.illusionaireweb

import kotlinx.browser.document
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLImageElement
import kotlin.text.endsWith

fun createMonsterDisplayElement(
    onFightClick: () -> Unit,
    onAppeaseClick: () -> Unit
): HTMLDivElement {
    val monsterContainer = document.createElement("div") as HTMLDivElement
    monsterContainer.id = "monster-container"

    with(monsterContainer.style) {
        position = "absolute"
        top = "50%"
        left = "50%"
        transform = "translate(-50%, -50%)"
        backgroundColor = "transparent"
        width = "300px"
        display = "flex"
        flexDirection = "column"
        alignItems = "center"
        columnGap = "15px"
        display = "none"
        zIndex = "10"
    }

    val monsterImage = document.createElement("img") as HTMLImageElement
    monsterImage.id = "monster-image"
    with(monsterImage.style) {
        maxWidth = "100%"
        height = "auto"
    }

    val buttonContainer = document.createElement("div") as HTMLDivElement
    with(buttonContainer.style) {
        display = "flex"
        justifyContent = "center"
        columnGap = "10px"
    }

    // Create Fight and Appease buttons
    val buttonStyle: HTMLButtonElement.() -> Unit = {
        with(style) {
            backgroundImage = "url('images/button1.png')"
            backgroundSize = "cover"
            backgroundPosition = "center"
            width = "150px"
            height = "50px"
            border = "none"
            backgroundColor = "transparent"
            color = GameColors.BUTTON_TEXT_GOLD
            fontSize = "16px"
            fontWeight = "bold"
            cursor = "pointer"
        }
    }

    val fightButton = (document.createElement("button") as HTMLButtonElement).apply {
        id = "fight-button" // Add ID for potential future use
        textContent = "Fight!"
        buttonStyle()
        onclick = {
            SoundManager.play("select")
            onFightClick()
        }
    }

    val appeaseButton = (document.createElement("button") as HTMLButtonElement).apply {
        id = "appease-button" // Add ID to select this button specifically
        textContent = "Appease"
        buttonStyle()
        onclick = {
            SoundManager.play("select")
            onAppeaseClick()
        }
    }

    buttonContainer.appendChild(fightButton)
    buttonContainer.appendChild(appeaseButton)

    monsterContainer.appendChild(monsterImage)
    monsterContainer.appendChild(buttonContainer) // Add the button container below the image

    return monsterContainer
}

fun updateMonsterDisplay(
    monsterContainer: HTMLDivElement,
    currentRoom: Room,
    revealedMonsterActionIds: Set<String>,
    defeatAnimationIds: Set<String>,
    failedAppeaseActionIds: Set<String>
) {
    val monsterAction = currentRoom.actions.find {
        it.id in revealedMonsterActionIds && it.monster != null
    }

    val monsterImage = monsterContainer.querySelector("#monster-image") as? HTMLImageElement
    val buttonContainer = monsterContainer.querySelector("div > div") as? HTMLDivElement
    val appeaseButton = monsterContainer.querySelector("#appease-button") as? HTMLButtonElement

    if (monsterAction != null && monsterImage != null) {
        val monster = monsterAction.monster!!
        if (!monsterImage.src.endsWith(monster.image)) {
            monsterImage.src = monster.image
            monsterImage.title = monster.description
        }
        monsterContainer.style.display = "flex"
        val isAnimatingDefeat = monsterAction.id in defeatAnimationIds

        if (isAnimatingDefeat) {
            buttonContainer?.style?.display = "none"
        } else {
            buttonContainer?.style?.display = "flex"
            appeaseButton?.let { button ->
                val hasFailedAppease = monsterAction.id in failedAppeaseActionIds
                button.disabled = hasFailedAppease

                if (hasFailedAppease) {
                    button.style.opacity = "0.5"
                    button.style.cursor = "not-allowed"
                } else {
                    button.style.opacity = "1.0"
                    button.style.cursor = "pointer"
                }
            }
        }
    } else {
        monsterContainer.style.display = "none"
    }
}