package com.illusionaireweb

import kotlinx.browser.document
import org.w3c.dom.HTMLImageElement

fun createEquippedWeaponIconElement(): HTMLImageElement {
    val iconElement = document.createElement("img") as HTMLImageElement
    iconElement.id = "equipped-weapon-icon"

    with(iconElement.style) {
        width = "50px"
        height = "50px"
        borderRadius = "5px"
        backgroundColor = "transparent"
    }

    return iconElement
}

fun updateEquippedWeaponIcon(iconElement: HTMLImageElement, weapon: Weapon) {
    if (iconElement.src.endsWith(weapon.iconPath).not()) {
        iconElement.src = weapon.iconPath
        iconElement.title = "Equipped: ${weapon.name}"
    }
}