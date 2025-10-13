package com.illusionaireweb

import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLImageElement
import kotlin.text.endsWith

/**
 * Creates the container element for displaying a monster. It is hidden by default.
 *
 * @return A HTMLDivElement that will hold the monster image.
 */
fun createMonsterDisplayElement(): HTMLDivElement {
    val monsterContainer = document.createElement("div") as HTMLDivElement
    monsterContainer.id = "monster-container"

    with(monsterContainer.style) {
        position = "absolute"
        top = "50%"
        right = "50%"
        transform = "translate(50%, -50%)"
        backgroundColor = "transparent"
        width = "300px"

        // Flexbox to center the image inside
        display = "flex"
        flexDirection = "column"
        alignItems = "center"

        // Initially hidden
        display = "none"
        zIndex = "10"
    }

    val monsterImage = document.createElement("img") as HTMLImageElement
    monsterImage.id = "monster-image"
    with(monsterImage.style) {
        maxWidth = "100%"
        height = "auto"
    }

    monsterContainer.appendChild(monsterImage)
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
    revealedMonsterActionIds: Set<String>
) {
    val monsterAction = currentRoom.actions.find {
        it.id in revealedMonsterActionIds && it.monster != null
    }

    val monsterImage = monsterContainer.querySelector("#monster-image") as? HTMLImageElement

    if (monsterAction != null && monsterImage != null) {
        val monster = monsterAction.monster!!
        if (!monsterImage.src.endsWith(monster.image)) {
            monsterImage.src = monster.image
            monsterImage.title = monster.description
        }
        monsterContainer.style.display = "flex"
    } else {
        monsterContainer.style.display = "none"
    }
}
