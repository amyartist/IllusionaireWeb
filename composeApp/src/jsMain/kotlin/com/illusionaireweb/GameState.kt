package com.illusionaireweb

import kotlinx.coroutines.flow.StateFlow

/**
 * A data class that holds all the current state information for the game.
 * This makes it easy to pass the entire game state to the UI layer.
 */
data class GameState(
    val currentRoom: Room,
    val equippedWeapon: Weapon,
    val currentAvatar: Avatar
    // You can add more state here later, like playerHealth, score, etc.
)
