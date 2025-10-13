package com.illusionaireweb

import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLImageElement

/**
 * Injects the game screen into a specified container element on the page.
 * @param containerId The ID of the div element where the game should be embedded.
 */
fun showGameScreen(containerId: String) {
    // 1. Find the host container element by its ID.
    val hostContainer = document.getElementById(containerId) as? HTMLElement

    if (hostContainer != null) {
        // --- ViewModel and State Management ---
        val viewModel = GameViewModel()
        val scope = CoroutineScope(Dispatchers.Main)

        // --- UI Setup ---
        hostContainer.style.display = "flex"
        hostContainer.style.alignItems = "center"
        hostContainer.style.justifyContent = "center"
        hostContainer.style.width = "100%"
        hostContainer.style.height = "100%"

        // Main Game Container
        val gameContainer = document.createElement("div") as HTMLDivElement
        with(gameContainer.style) {
            position = "relative"
            maxWidth = "1024px"
            maxHeight = "1024px"
//            maxHeight = "1126.4px" // 1024px * 1.1
//            width = "min(100%, 100vw, 90vh * (1 / 1.1))"
            width = "min(100%, 100vw, 90vh)"
//            setProperty("aspect-ratio", "1 / 1.1")
            border = "2px solid yellow"
            backgroundColor = "black"
        }

        // Create the image element for the room background
        val roomImage = document.createElement("img") as HTMLImageElement
        with(roomImage.style) {
            width = "100%"
            height = "auto"
            objectFit = "cover"
        }

        // Add the image to the game container
        gameContainer.appendChild(roomImage)

        // Health Bar
        val healthBar = createHealthBarElement()
        gameContainer.appendChild(healthBar)

        // Avatar Display
        val avatarDisplay = createAvatarElement()
        gameContainer.appendChild(avatarDisplay)

        // Create the buttons container and add it to the game area
        val buttonsContainer = createButtonsContainer()
        gameContainer.appendChild(buttonsContainer)

        // --- State Observation ---
        viewModel.gameState.onEach { state ->
            roomImage.src = state.currentRoom.image
            updateHealthBar(healthBar, state.playerHealth)
            updateAvatarDisplay(avatarDisplay, state.currentAvatar) // Update the avatar's image
            updateGameButtons(buttonsContainer, state.currentRoom.actions) { actionId ->
                viewModel.onPlayerAction(actionId)
            }
        }.launchIn(scope)


        hostContainer.innerHTML = ""
        hostContainer.appendChild(gameContainer)
    } else {
        console.error("IllusionaireWeb Error: Container element with ID '$containerId' was not found.")
    }
}
