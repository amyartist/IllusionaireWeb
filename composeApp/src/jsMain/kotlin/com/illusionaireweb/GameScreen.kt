package com.illusionaireweb

import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLImageElement

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
            // Add all sounds you want to preload to this list.
            SoundManager.preloadSounds(listOf("select"))
        } catch (e: Error) {
            hostContainer.textContent = "Error loading assets. Please refresh the page."
            console.error("Failed to preload sounds:", e)
            return@launch
        }

        // --- STEP 2: BUILD UI (only after sounds are loaded) ---
        hostContainer.innerHTML = "" // Clear the "Loading..." message

        hostContainer.style.display = "flex"
        hostContainer.style.alignItems = "center"
        hostContainer.style.justifyContent = "center"
        hostContainer.style.width = "100%"
        hostContainer.style.height = "100%"

        // All UI element creation now happens inside this coroutine.
        val gameContainer = document.createElement("div") as HTMLDivElement
        gameContainer.id = "game-container"

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

        // --- STEP 3: START GAME LOGIC OBSERVER ---
        viewModel.gameState.onEach { state ->
            // The UI update logic here remains completely unchanged.
            roomImage.src = state.currentRoom.image
            updateHealthBar(healthBar, state.playerHealth)
            updateEquippedWeaponIcon(weaponIcon, state.equippedWeapon)
            updateAvatarDisplay(avatarDisplay, state.currentAvatar)
            updateMonsterDisplay(
                monsterDisplay,
                state.currentRoom,
                state.revealedMonsterActionIds,
                state.monsterDefeatAnimationIds
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

        hostContainer.appendChild(gameContainer)
    }
}