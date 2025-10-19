package com.illusionaireweb

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLImageElement

/**
 * Creates the visual avatar image element.
 */
fun createAvatarElement(): HTMLImageElement {
    // Ensure the shared CSS animation rule is available for use.
    injectInPlaceShakeAnimation()

    val avatarImage = document.createElement("img") as HTMLImageElement
    avatarImage.id = "avatar-display"

    with(avatarImage.style) {
        position = "absolute" // Position it relative to the game container
        top = "10px"
        right = "10px" // Position to the top right
        width = "200px" // Set a fixed width for the avatar
        height = "auto" // Maintain aspect ratio
        // Since the PNGs have transparency, no special border or background is needed.
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
    // Apply the animation.
    avatarImage.style.animation = "shake-in-place 1.2s cubic-bezier(.36,.07,.19,.97)"

    // Set a timer to remove the animation property after it finishes.
    // This is crucial so the animation can be re-triggered on the next hit.
    window.setTimeout({
        avatarImage.style.animation = ""
    }, 1200)
}