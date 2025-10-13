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

    // A public, read-only flow that the UI can observe for changes.
    val gameState = _gameState.asStateFlow()

    /**
     * Moves the player to a new room.
     * @param roomId The ID of the destination room.
     */
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
