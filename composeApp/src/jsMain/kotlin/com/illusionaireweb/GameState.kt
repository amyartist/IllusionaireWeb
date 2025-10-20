package com.illusionaireweb

data class GameState(
    val currentRoom: Room,
    val equippedWeapon: Weapon,
    val currentAvatar: Avatar,
    val playerHealth: Int = 100,
    val dialogMessage: String? = null,
    val lootedActionIds: Set<String> = emptySet(),
    val revealedMonsterActionIds: Set<String> = emptySet(),
    val isRiddleLoading: Boolean = false,
    val isCheckingRiddleAnswer: Boolean = false,
    val riddleQuestion: String? = null,
    val monsterActionIdToAppease: String? = null,
    val riddleToDisplay: String? = null,
    val fightEffectKey: Long? = null,
    val monsterDefeatAnimationIds: Set<String> = emptySet(),
    val failedAppeaseActionIds: Set<String> = emptySet()
)
