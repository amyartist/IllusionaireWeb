package com.illusionaireweb

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLImageElement

fun createBloodSplatterElement(): HTMLImageElement {
    injectCenteredShakeAnimation()

    val splatterImage = document.createElement("img") as HTMLImageElement
    splatterImage.src = "images/blood_splatter.png"
    splatterImage.id = "blood-splatter-effect"

    with(splatterImage.style) {
        position = "absolute"
        top = "50%"
        left = "50%"
        transform = "translate(-50%, -50%)"

        width = "300px"
        height = "auto"
        opacity = "0.8"
        zIndex = "20"
        setProperty("pointer-events", "none")
        display = "none"
    }

    return splatterImage
}

/**
 * Triggers the blood splatter effect.
 */
fun showBloodSplatterEffect(splatterImage: HTMLImageElement) {
    SoundManager.play("monster_hit")
    splatterImage.style.display = "block"
    splatterImage.style.animation = "shake-from-center 0.8s cubic-bezier(.36,.07,.19,.97)"

    window.setTimeout({
        splatterImage.style.display = "none"
        splatterImage.style.animation = ""
    }, 1200)
}