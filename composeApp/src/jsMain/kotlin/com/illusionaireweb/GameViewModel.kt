package com.illusionaireweb

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Manages the game's state and business logic.
 * It is completely separate from the UI (DOM manipulation).
 */
class GameViewModel {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val aiService = AiService()

    // We store the riddle question internally to check the answer
    private var activeRiddleQuestion: String? = null

    // A private mutable state that holds the current game state.
    private val _gameState = MutableStateFlow(
        GameState(
            currentRoom = gameRooms.getValue("starting_room"),
            equippedWeapon = Weapons.FISTS,
            currentAvatar = Avatars.NEUTRAL
        )
    )

    val gameState = _gameState.asStateFlow()


    /**
     * Main entry point for all player actions from buttons.
     * It finds the action by its ID and delegates to the appropriate handler.
     */
    fun onPlayerAction(actionId: String) {
        // Find the room and the specific action that was clicked
        val currentRoom = _gameState.value.currentRoom
        val action = currentRoom.actions.find { it.id == actionId }

        if (action == null) {
            console.error("Action with ID '$actionId' not found in room '${currentRoom.id}'.")
            return
        }

        // Delegate to the correct function based on the action type
        when (action.type) {
            ActionType.LOOK -> handleLookAction(action)
            ActionType.OPEN -> handleOpenAction(action)
            ActionType.GO -> handleGoAction(action)
        }
    }

    private fun handleLookAction(action: Action) {
        console.log("Executing LOOK action: ${action.id}")
        SoundManager.play("hm")
        _gameState.update {
            it.copy(
                dialogMessage = action.message,
                currentAvatar = action.avatar ?: it.currentAvatar
            )
        }
    }

    private fun handleOpenAction(action: Action) {
        SoundManager.play("creak")
        _gameState.update { currentState ->
            if (action.id in currentState.lootedActionIds) {
                return@update currentState.copy(
                    dialogMessage = "You search the ${action.item} again, but it's empty.",
                    currentAvatar = Avatars.NEUTRAL
                )
            }

            if (action.monster != null) {
                SoundManager.play("scary")
                val newRevealedIds = currentState.revealedMonsterActionIds + action.id
                return@update currentState.copy(
                    dialogMessage = action.message,
                    currentAvatar = action.avatar ?: currentState.currentAvatar,
                    revealedMonsterActionIds = newRevealedIds
                )
            }

            val foundItems = action.contents
            if (foundItems.isNullOrEmpty()) {
                SoundManager.play("magic")
                val newLootedIds = currentState.lootedActionIds + action.id
                return@update currentState.copy(
                    dialogMessage = action.message ?: "You open the ${action.item} and find nothing.",
                    currentAvatar = action.avatar ?: currentState.currentAvatar,
                    lootedActionIds = newLootedIds
                )
            }

            val weaponToEquip = foundItems.filterIsInstance<Weapon>().firstOrNull()
            val itemNames = foundItems.joinToString(", ") { it.name }
            val dialogMessage = "You open the ${action.item} and find: $itemNames."
            val newLootedIds = currentState.lootedActionIds + action.id

            currentState.copy(
                dialogMessage = dialogMessage,
                equippedWeapon = weaponToEquip ?: currentState.equippedWeapon,
                currentAvatar = action.avatar ?: currentState.currentAvatar,
                lootedActionIds = newLootedIds
            )
        }    }

    private fun handleGoAction(action: Action) {
        console.log("Executing GO action: ${action.id}")
        val destinationId = action.destinationRoomId
        if (destinationId != null) {
            SoundManager.play("footsteps")
            moveToRoom(destinationId)
        }
    }

    fun onFightMonster() {
        console.log("Player chose to FIGHT!")
        val monsterAction = _gameState.value.currentRoom.actions.find {
            it.id in _gameState.value.revealedMonsterActionIds && it.monster != null
        } ?: return // Exit if no monster found

        // --- PART 1: IMMEDIATE STATE UPDATE (Trigger Animation) ---
        _gameState.update { currentState ->
            val monster = monsterAction.monster!!
            val weapon = currentState.equippedWeapon
            val damageTaken = (monster.strength - weapon.strength).coerceAtLeast(0)
            val newHealth = (currentState.playerHealth - damageTaken).coerceAtLeast(0)

            currentState.copy(
                playerHealth = newHealth,
                fightEffectKey = js("Date.now()").unsafeCast<Double>().toLong(),
                monsterDefeatAnimationIds = currentState.monsterDefeatAnimationIds + monsterAction.id,
                currentAvatar = Avatars.HURT
            )
        }

        viewModelScope.launch {
            // Wait for the 1.2-second shake animation to finish.
            delay(1200L)

            // After the delay, change the avatar back to NEUTRAL.
            _gameState.update { currentState ->
                // Only revert if the current avatar is still HURT, to avoid race conditions
                // if another event changed the avatar in the meantime.
                if (currentState.currentAvatar.type == AvatarType.HURT) {
                    currentState.copy(currentAvatar = Avatars.NEUTRAL)
                } else {
                    currentState
                }
            }
        }

        viewModelScope.launch {
            delay(1500L)

            _gameState.update { currentState ->
                currentState.copy(
                    revealedMonsterActionIds = currentState.revealedMonsterActionIds - monsterAction.id,
                    lootedActionIds = currentState.lootedActionIds + monsterAction.id,
                    monsterDefeatAnimationIds = currentState.monsterDefeatAnimationIds - monsterAction.id
                )
            }
        }    }

    fun onAppeaseMonster() {
        console.log("Player chose to APPEASE!")
        val monsterAction = _gameState.value.currentRoom.actions.find {
            it.monster != null && it.id in _gameState.value.revealedMonsterActionIds
        }

        if (monsterAction?.monster == null) {
            console.error("onAppeaseMonster called, but no active monster was found.")
            return
        }
        val monsterName = monsterAction.monster.description

        _gameState.update { it.copy(dialogMessage = "The monster is pondering a riddle...") }

        viewModelScope.launch {
            val riddleText = aiService.getRiddle(monsterName)
            if (riddleText != null) {
                _gameState.update {
                    it.copy(
                        dialogMessage = null,
                        riddleToDisplay = riddleText
                    )
                }
            } else {
                _gameState.update { it.copy(dialogMessage = "The spirits are silent. The monster is not in the mood for riddles.") }
            }
        }
    }

    fun submitRiddleAnswer(userAnswer: String) {
        val riddleQuestion = _gameState.value.riddleToDisplay ?: return

        _gameState.update {
            it.copy(
                riddleToDisplay = null,
                isCheckingRiddleAnswer = true,
                dialogMessage = "The monster considers your answer..."
            )
        }

        viewModelScope.launch {
            val isCorrect = aiService.checkRiddleAnswer(riddleQuestion, userAnswer)
            handleRiddleResult(isCorrect)
        }
    }

    private fun handleRiddleResult(isCorrect: Boolean) {
        _gameState.update { currentState ->
            val monsterActionId = currentState.revealedMonsterActionIds.find { id ->
                currentState.currentRoom.actions.any { it.id == id && it.monster != null }
            }!!
            val monsterName = currentState.currentRoom.actions
                .find { it.id == monsterActionId }?.monster?.description ?: "creature"

            val resultMessage: String
            val newState: GameState

            if (isCorrect) {
                resultMessage = "Correct! The $monsterName is pleased and lets you pass."
                newState = currentState.copy(
                    lootedActionIds = currentState.lootedActionIds + monsterActionId,
                    revealedMonsterActionIds = currentState.revealedMonsterActionIds - monsterActionId,
                    currentAvatar = Avatars.HAPPY
                )
            } else {
                val damage = 15
                resultMessage = "Wrong! The $monsterName gets angry and strikes you for $damage damage!"
                newState = currentState.copy(
                    playerHealth = (currentState.playerHealth - damage).coerceAtLeast(0),
                    currentAvatar = Avatars.HURT
                )
            }

            // Final update: clear loading flag and set the result message
            newState.copy(isCheckingRiddleAnswer = false, dialogMessage = resultMessage)
        }
    }

    fun dismissRiddle() {
        // Just update the state. The UI will react and hide the dialog.
        _gameState.update {
            it.copy(
                riddleToDisplay = null,
                dialogMessage = "You decide not to test your wits right now.",
                currentAvatar = Avatars.NEUTRAL
            )
        }
    }

    fun dismissDialog() {
        _gameState.update { it.copy(dialogMessage = null) }
    }

    fun moveToRoom(roomId: String) {
        val newRoom = gameRooms[roomId]
        if (newRoom != null) {
            _gameState.update { currentState ->
                currentState.copy(currentRoom = newRoom)
            }
        } else {
            console.error("GameViewModel: Room with ID '$roomId' not found.")
        }
    }
}
