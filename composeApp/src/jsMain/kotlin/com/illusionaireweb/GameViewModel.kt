package com.illusionaireweb

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Manages the game's state and business logic.
 * It is completely separate from the UI (DOM manipulation).
 */
class GameViewModel {
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
        _gameState.update {
            it.copy(
                dialogMessage = action.message,
                currentAvatar = action.avatar ?: it.currentAvatar
            )
        }
    }

    private fun handleOpenAction(action: Action) {
        console.log("Executing OPEN action: ${action.id}")
        // Future logic: check inventory, maybe trigger a monster, give an item.
        _gameState.update { it.copy(currentAvatar = action.avatar ?: it.currentAvatar) }
    }

    private fun handleGoAction(action: Action) {
        console.log("Executing GO action: ${action.id}")
        // Future logic: move the player to a new room.
        val destinationId = action.destinationRoomId
        if (destinationId != null) {
            moveToRoom(destinationId)
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

    fun takeDamage(amount: Int) {
        _gameState.update { currentState ->
            val newHealth = currentState.playerHealth - amount
            currentState.copy(playerHealth = newHealth)
        }
    }
}
