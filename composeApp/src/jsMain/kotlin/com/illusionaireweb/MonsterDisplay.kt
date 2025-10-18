package com.illusionaireweb

import kotlinx.browser.document
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLImageElement
import kotlin.text.endsWith

/**
 * Creates the container element for displaying a monster, now with action buttons.
 *
 * @param onFightClick Lambda to execute when the "Fight!" button is clicked.
 * @param onAppeaseClick Lambda to execute when the "Appease" button is clicked.
 * @return A HTMLDivElement that will hold the monster image and buttons.
 */
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

    // Monster Image
    val monsterImage = document.createElement("img") as HTMLImageElement
    monsterImage.id = "monster-image"
    with(monsterImage.style) {
        maxWidth = "100%"
        height = "auto"
    }

    // Container for the buttons
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
        textContent = "Fight!"
        buttonStyle()
        onclick = { onFightClick() }
    }

    val appeaseButton = (document.createElement("button") as HTMLButtonElement).apply {
        textContent = "Appease"
        buttonStyle()
        onclick = { onAppeaseClick() }
    }

    // Assemble the parts
    buttonContainer.appendChild(fightButton)
    buttonContainer.appendChild(appeaseButton)

    monsterContainer.appendChild(monsterImage)
    monsterContainer.appendChild(buttonContainer) // Add the button container below the image

    return monsterContainer
}

/**
 * Updates the visibility and image of the monster display.
 *
 * It finds the first monster associated with a revealed action in the current room
 * and displays it. If no revealed monsters are in the room, it hides the container.
 *
 * @param monsterContainer The UI element for the monster display.
 * @param currentRoom The player's current room.
 * @param revealedMonsterActionIds The set of action IDs that have revealed a monster.
 */
fun updateMonsterDisplay(
    monsterContainer: HTMLDivElement,
    currentRoom: Room,
    revealedMonsterActionIds: Set<String>,
    defeatAnimationIds: Set<String>
) {
    val monsterAction = currentRoom.actions.find {
        it.id in revealedMonsterActionIds && it.monster != null
    }

    val monsterImage = monsterContainer.querySelector("#monster-image") as? HTMLImageElement
    val buttonContainer = monsterContainer.querySelector("div > div") as? HTMLDivElement // More robust selector for button container

    if (monsterAction != null && monsterImage != null) {
        val monster = monsterAction.monster!!
        if (!monsterImage.src.endsWith(monster.image)) {
            monsterImage.src = monster.image
            monsterImage.title = monster.description
        }
        monsterContainer.style.display = "flex"
        val isAnimatingDefeat = monsterAction.id in defeatAnimationIds

        // Only show the buttons if the monster is NOT animating its defeat.
        if (isAnimatingDefeat) {
            buttonContainer?.style?.display = "none"
        } else {
            buttonContainer?.style?.display = "flex"
        }
    } else {
        monsterContainer.style.display = "none"
    }
}