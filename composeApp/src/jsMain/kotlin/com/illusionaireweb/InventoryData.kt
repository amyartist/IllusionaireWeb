package com.illusionaireweb

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class WeaponType {
    UNARMED, DAGGER, SWORD, STAFF, MACE
}

open class Item(
    val name: String,
    val description: String,
)

class Weapon(
    name: String,
    description: String,
    val strength: Int,
    val type: WeaponType,
    val iconPath: String
): Item(name, description)

object Weapons {
    val FISTS = Weapon(
        name = "Fists",
        type = WeaponType.UNARMED,
        strength = 1,
        description = "Your bare hands.",
        iconPath = "images/fist_icon.png"
    )
    val RUSTY_DAGGER = Weapon(
        name = "Rusty Dagger",
        type = WeaponType.DAGGER,
        strength = 3,
        description = "A small, rusty dagger.",
        iconPath = "images/dagger_icon.png"
    )
    val IRON_SWORD = Weapon(
        name = "Iron Sword",
        type = WeaponType.SWORD,
        strength = 4,
        description = "A sturdy iron sword.",
        iconPath = "images/sword_icon.png"
    )
    val MACE = Weapon(
        name = "Fire Mace",
        type = WeaponType.MACE,
        strength = 7,
        description = "A magical mace of fire.",
        iconPath = "images/mace_icon.png"
    )
}

object InventoryData {
    val equippedWeaponFlow = MutableStateFlow<Weapon>(Weapons.FISTS)
    val equippedWeapon: StateFlow<Weapon> = equippedWeaponFlow.asStateFlow()

    fun equipWeapon(newWeapon: Weapon) {
        equippedWeaponFlow.value = newWeapon
    }

    fun getCurrentWeapon(): Weapon {
        return equippedWeaponFlow.value
    }
}