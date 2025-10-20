package com.illusionaireweb

import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLImageElement

/**
 * Creates the initial "Start Game" screen overlay.
 * This screen is shown first to get the necessary user interaction to enable audio.
 * @param onStartClick A lambda function to be executed when the start button is clicked.
 */
private fun createStartScreen(onStartClick: () -> Unit): HTMLDivElement {
    val overlay = document.createElement("div") as HTMLDivElement
    with(overlay.style) {
        position = "absolute"
        top = "0"
        left = "0"
        width = "100%"
        height = "100%"
        backgroundColor = "rgba(0, 0, 0, 0.95)"
        display = "flex"
        flexDirection = "column"
        justifyContent = "center"
        alignItems = "center"
        zIndex = "1000" // Ensure it's on top of other game elements
        columnGap = "20px"
        // Match the parent container's border radius for a seamless look
        borderRadius = "8px"
    }

    val title = document.createElement("h1") as HTMLElement
    title.textContent = "Illusionaire"
    with(title.style) {
        color = GameColors.DIALOG_TEXT
        fontFamily = "'MedievalSharp', cursive"
        fontSize = "5rem"
        textShadow = "2px 2px 8px ${GameColors.BORDER_YELLOW}"
    }

    val startButton = document.createElement("button") as HTMLButtonElement
    startButton.textContent = "Click to Begin"
    with(startButton.style) {
        backgroundColor = GameColors.BACKGROUND_BLACK
        color = GameColors.DIALOG_TEXT
        border = "2px solid ${GameColors.BORDER_YELLOW}"
        padding = "15px 30px"
        fontSize = "1.5rem"
        fontFamily = "'MedievalSharp', cursive"
        cursor = "pointer"
        borderRadius = "5px"
    }
    // Assign the click action provided to the function
    startButton.onclick = { onStartClick() }

    overlay.appendChild(title)
    overlay.appendChild(startButton)

    return overlay
}

fun showGameScreen(containerId: String) {
    val hostContainer = document.getElementById(containerId) as? HTMLElement
    if (hostContainer == null) {
        console.error("IllusionaireWeb Error: Container element with ID '$containerId' was not found.")
        return
    }

    val viewModel = GameViewModel()
    val scope = CoroutineScope(Dispatchers.Main)

    // Launch a coroutine to handle the entire asynchronous setup process.
    scope.launch {
        // --- STEP 1: PRELOAD ASSETS ---
        hostContainer.textContent = "Loading assets..." // Show a loading message
        try {
            SoundManager.preloadSounds(listOf("select", "creak", "footsteps", "hm", "magic", "scary", "hurt", "background_music", "monster_hit"))
        } catch (e: Error) {
            hostContainer.textContent = "Error loading assets. Please refresh the page."
            console.error("Failed to preload sounds:", e)
            return@launch
        }

        // --- STEP 2: BUILD UI ---
        hostContainer.innerHTML = "" // Clear the "Loading..." message

        hostContainer.style.display = "flex"
        hostContainer.style.alignItems = "center"
        hostContainer.style.justifyContent = "center"
        hostContainer.style.width = "100%"
        hostContainer.style.height = "100%"

        // Create the game container. It will be VISIBLE from the start.
        val gameContainer = document.createElement("div") as HTMLDivElement
        gameContainer.id = "game-container"

        with(gameContainer.style) {
            position = "relative" // Crucial for positioning the child start screen
            maxWidth = "1024px"
            width = "min(100%, 100vw, 90vh)"
            border = "2px solid ${GameColors.BORDER_YELLOW}"
            backgroundColor = GameColors.BACKGROUND_BLACK
            borderRadius = "10px"
            display = "flex"
            alignItems = "center"
            justifyContent = "center"
        }

        // --- Create all game elements and add them to the game container ---
        val roomImage = document.createElement("img") as HTMLImageElement
        with(roomImage.style) {
            width = "100%"
            height = "auto"
            borderRadius = "10px"
        }
        gameContainer.appendChild(roomImage)

        val topStatusContainer = document.createElement("div") as HTMLDivElement
        with(topStatusContainer.style) {
            position = "absolute"
            top = "10px"
            left = "10px"
            display = "flex"
            alignItems = "center"
            columnGap = "15px"
        }
        gameContainer.appendChild(topStatusContainer)

        val healthBar = createHealthBarElement()
        topStatusContainer.appendChild(healthBar)

        val weaponIcon = createEquippedWeaponIconElement()
        topStatusContainer.appendChild(weaponIcon)

        val avatarDisplay = createAvatarElement()
        gameContainer.appendChild(avatarDisplay)

        val monsterDisplay = createMonsterDisplayElement(
            onFightClick = { viewModel.onFightMonster() },
            onAppeaseClick = { viewModel.onAppeaseMonster() }
        )
        gameContainer.appendChild(monsterDisplay)

        val bloodSplatter = createBloodSplatterElement()
        monsterDisplay.appendChild(bloodSplatter)

        val buttonsContainer = createButtonsContainer()
        gameContainer.appendChild(buttonsContainer)

        val dialog = createDialogElement { viewModel.dismissDialog() }
        gameContainer.appendChild(dialog)

        val riddleDialog = createRiddleDialog(
            onSubmit = { userAnswer -> viewModel.submitRiddleAnswer(userAnswer) },
            onDismiss = { viewModel.dismissRiddle() }
        )
        gameContainer.appendChild(riddleDialog)

        var lastSeenFightKey: Long? = null
        lateinit var startScreen: HTMLDivElement

        // --- STEP 3: CREATE THE START SCREEN ---
        startScreen = createStartScreen {
            // This code runs when the "Click to Begin" button is clicked.
            console.log("User interaction detected. Starting music.")

            // 1. Start the background music
            SoundManager.playLoop("background_music")

            // 2. Hide the start screen to reveal the game underneath
            startScreen.style.display = "none"
        }
        // Add the start screen as a child of the game container
        gameContainer.appendChild(startScreen)

        // --- STEP 4: START GAME LOGIC OBSERVER ---
        viewModel.gameState.onEach { state ->
            // UI update logic remains unchanged
            roomImage.src = state.currentRoom.image
            updateHealthBar(healthBar, state.playerHealth)
            updateEquippedWeaponIcon(weaponIcon, state.equippedWeapon)
            updateAvatarDisplay(avatarDisplay, state.currentAvatar)
            updateMonsterDisplay(
                monsterDisplay,
                state.currentRoom,
                state.revealedMonsterActionIds,
                state.monsterDefeatAnimationIds,
                state.failedAppeaseActionIds
            )
            updateDialog(dialog, state.dialogMessage)
            updateRiddleDialog(riddleDialog, state.riddleToDisplay)

            val isMonsterVisible = state.currentRoom.actions.any {
                it.id in state.revealedMonsterActionIds && it.monster != null
            }
            if (isMonsterVisible) {
                buttonsContainer.style.display = "none"
            } else {
                buttonsContainer.style.display = "flex"
            }
            updateGameButtons(buttonsContainer, state.currentRoom.actions) { actionId ->
                viewModel.onPlayerAction(actionId)
            }

            if (state.fightEffectKey != null && state.fightEffectKey != lastSeenFightKey) {
                showBloodSplatterEffect(bloodSplatter)
                triggerAvatarShake(avatarDisplay)
                lastSeenFightKey = state.fightEffectKey
            }
        }.launchIn(scope)

        // Finally, add the fully assembled game container to the page
        hostContainer.appendChild(gameContainer)
    }
}