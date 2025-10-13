package com.illusionaireweb

import kotlinx.browser.document
import org.w3c.dom.HTMLImageElement

/**
 * Creates the HTML image element for the equipped weapon icon.
 * It is positioned to the right of the health bar.
 *
 * @return An HTMLImageElement ready to be added to the game container.
 */
fun createEquippedWeaponIconElement(): HTMLImageElement {
    val iconElement = document.createElement("img") as HTMLImageElement
    iconElement.id = "equipped-weapon-icon"

    with(iconElement.style) {
//        position = "absolute"
//        top = "5px"
//        left = "300px"
        width = "50px"
        height = "50px"
        borderRadius = "5px"
        backgroundColor = "transparent"
    }

    return iconElement
}

/**
 * Updates the source of the weapon icon element based on the currently equipped weapon.
 *
 * @param iconElement The <img> element created by `createEquippedWeaponIconElement`.
 * @param weapon The currently equipped `Weapon` object from the game state.
 */
fun updateEquippedWeaponIcon(iconElement: HTMLImageElement, weapon: Weapon) {
    // Check if the source needs updating to prevent unnecessary DOM manipulation
    if (iconElement.src.endsWith(weapon.iconPath).not()) {
        iconElement.src = weapon.iconPath
        // Also update the 'title' attribute for a nice mouse-over tooltip
        iconElement.title = "Equipped: ${weapon.name}"
    }
}