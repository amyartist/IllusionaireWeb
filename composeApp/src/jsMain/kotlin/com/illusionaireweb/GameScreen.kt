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
            width = "min(100%, 100vw, 90vh)"
            border = "2px solid ${GameColors.BORDER_YELLOW}"
            backgroundColor = GameColors.BACKGROUND_BLACK
            borderRadius = "10px"
            display = "flex"
            alignItems = "center"
            justifyContent = "center"
        }

        val roomImage = document.createElement("img") as HTMLImageElement
        with(roomImage.style) {
            width = "100%"
            height = "auto"
            borderRadius = "10px"
        }
        gameContainer.appendChild(roomImage)

        val healthBar = createHealthBarElement()
        gameContainer.appendChild(healthBar)

        val avatarDisplay = createAvatarElement()
        gameContainer.appendChild(avatarDisplay)

        val buttonsContainer = createButtonsContainer()
        gameContainer.appendChild(buttonsContainer)

        val dialog = createDialogElement {
            viewModel.dismissDialog()
        }
        gameContainer.appendChild(dialog)

        // --- State Observation ---
        viewModel.gameState.onEach { state ->
            roomImage.src = state.currentRoom.image
            updateHealthBar(healthBar, state.playerHealth)
            updateAvatarDisplay(avatarDisplay, state.currentAvatar) // Update the avatar's image
            updateGameButtons(buttonsContainer, state.currentRoom.actions) { actionId ->
                viewModel.onPlayerAction(actionId)
            }
            updateDialog(dialog, state.dialogMessage)
        }.launchIn(scope)


        hostContainer.innerHTML = ""
        hostContainer.appendChild(gameContainer)
    } else {
        console.error("IllusionaireWeb Error: Container element with ID '$containerId' was not found.")
    }
}
