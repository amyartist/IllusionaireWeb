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
        zIndex = "1000"
        columnGap = "20px"
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
    startButton.onclick = { onStartClick() }

    overlay.appendChild(title)
    overlay.appendChild(startButton)

    return overlay
}

@OptIn(ExperimentalJsExport::class)
@JsExport
fun showGameScreen(containerId: String) {
    val hostContainer = document.getElementById(containerId) as? HTMLElement
    if (hostContainer == null) {
        console.error("IllusionaireWeb Error: Container element with ID '$containerId' was not found.")
        return
    }

    val viewModel = GameViewModel()
    val scope = CoroutineScope(Dispatchers.Main)

    scope.launch {
        hostContainer.textContent = "Loading assets..."
        try {
            SoundManager.preloadSounds(listOf("select", "creak", "footsteps", "hm", "magic", "scary", "hurt", "background_music", "monster_hit"))
        } catch (e: Error) {
            hostContainer.textContent = "Error loading assets. Please refresh the page."
            console.error("Failed to preload sounds:", e)
            return@launch
        }

        hostContainer.innerHTML = ""

        hostContainer.style.display = "flex"
        hostContainer.style.alignItems = "center"
        hostContainer.style.justifyContent = "center"
        hostContainer.style.width = "100%"
        hostContainer.style.height = "100%"

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
        lateinit var startScreen: HTMLDivElement

        startScreen = createStartScreen {
            SoundManager.playLoop("background_music")
            startScreen.style.display = "none"
        }
        gameContainer.appendChild(startScreen)

        viewModel.gameState.onEach { state ->
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

        hostContainer.appendChild(gameContainer)
    }
}