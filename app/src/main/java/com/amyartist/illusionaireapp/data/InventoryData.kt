package com.amyartist.illusionaireapp.data

import android.util.Log
import androidx.annotation.DrawableRes
import com.amyartist.illusionaireapp.R
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
    @DrawableRes val iconResId: Int
): Item(name, description)

object Weapons {
    val FISTS = Weapon(
        name = "Fists",
        type = WeaponType.UNARMED,
        strength = 1,
        description = "Your bare hands.",
        iconResId = R.drawable.fist_icon
    )
    val RUSTY_DAGGER = Weapon(
        name = "Rusty Dagger",
        type = WeaponType.DAGGER,
        strength = 3,
        description = "A small, rusty dagger.",
        iconResId = R.drawable.dagger_icon
    )
    val IRON_SWORD = Weapon(
        name = "Iron Sword",
        type = WeaponType.SWORD,
        strength = 4,
        description = "A sturdy iron sword.",
        iconResId = R.drawable.sword_icon
    )
    val MACE = Weapon(
        name = "Fire Mace",
        type = WeaponType.MACE,
        strength = 7,
        description = "A magical mace of fire.",
        iconResId = R.drawable.mace_icon
    )
}

object InventoryData {
    val equippedWeaponFlow = MutableStateFlow<Weapon>(Weapons.FISTS)
    val equippedWeapon: StateFlow<Weapon> = equippedWeaponFlow.asStateFlow()

    fun equipWeapon(newWeapon: Weapon) {
        equippedWeaponFlow.value = newWeapon
        Log.d("InventoryData", "Weapon equipped: ${newWeapon.name}")
    }

    fun getCurrentWeapon(): Weapon {
        return equippedWeaponFlow.value
    }
}