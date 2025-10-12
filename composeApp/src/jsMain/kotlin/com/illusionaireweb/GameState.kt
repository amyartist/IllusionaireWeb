package com.illusionaireweb

data class GameState(
    val currentRoom: Room,
    val equippedWeapon: Weapon,
    val currentAvatar: Avatar,
    val playerHealth: Int = 100
)
