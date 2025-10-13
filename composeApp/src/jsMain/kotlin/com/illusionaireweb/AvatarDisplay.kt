package com.illusionaireweb

import kotlinx.browser.document
import org.w3c.dom.HTMLImageElement

/**
 * Creates the visual avatar image element.
 * The avatar is positioned in the top right corner.
 *
 * @return An HTMLImageElement ready to be added to the game container.
 */
fun createAvatarElement(): HTMLImageElement {
    // Create an <img> element for the avatar
    val avatarImage = document.createElement("img") as HTMLImageElement
    avatarImage.id = "avatar-display" // Assign an ID for easy access

    with(avatarImage.style) {
        position = "absolute" // Position it relative to the game container
        top = "10px"
        right = "10px" // Position to the top right
        width = "150px" // Set a fixed width for the avatar
        height = "auto" // Maintain aspect ratio
        // Since the PNGs have transparency, no special border or background is needed.
    }

    return avatarImage
}

/**
 * Updates the source of the avatar image element based on the current Avatar state.
 *
 * @param avatarImage The <img> element created by `createAvatarElement`.
 * @param currentAvatar The Avatar object from the game state.
 */
fun updateAvatarDisplay(avatarImage: HTMLImageElement, currentAvatar: Avatar) {
    // The 'avatarPath' property contains the relative path (e.g., "images/avatar_happy.png")
    // The development server will serve it directly from the resources folder.
    if (avatarImage.src.endsWith(currentAvatar.avatarPath).not()) {
        avatarImage.src = currentAvatar.avatarPath
    }
}
