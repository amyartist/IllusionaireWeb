package com.illusionaireweb

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLImageElement

/**
 * Creates the visual avatar image element.
 */
fun createAvatarElement(): HTMLImageElement {
    injectInPlaceShakeAnimation()

    val avatarImage = document.createElement("img") as HTMLImageElement
    avatarImage.id = "avatar-display"

    with(avatarImage.style) {
        position = "absolute" // Position it relative to the game container
        top = "10px"
        right = "10px" // Position to the top right
        width = "200px" // Set a fixed width for the avatar
        height = "auto" // Maintain aspect ratio
    }

    return avatarImage
}

/**
 * Updates the source of the avatar image element.
 */
fun updateAvatarDisplay(avatarImage: HTMLImageElement, currentAvatar: Avatar) {
    if (avatarImage.src.endsWith(currentAvatar.avatarPath).not()) {
        avatarImage.src = currentAvatar.avatarPath
    }
}

/**
 * Triggers a shake animation on the avatar element for 1.2 seconds.
 * This is called when the player takes damage.
 * @param avatarImage The <img> element for the avatar.
 */
fun triggerAvatarShake(avatarImage: HTMLImageElement) {
    SoundManager.play("hurt")
    avatarImage.style.animation = "shake-in-place 1.2s cubic-bezier(.36,.07,.19,.97)"
    window.setTimeout({
        avatarImage.style.animation = ""
    }, 1200)
}