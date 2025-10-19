package com.illusionaireweb

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.HTMLStyleElement

private const val SHAKE_ANIMATION_ID = "shake-animation-style"

/**
 * Injects the CSS keyframe animation for the "shake" effect into the document's head.
 * This is done idempotently, so it only adds the style once.
 */
private fun injectShakeAnimation() {
    // If the style element already exists, do nothing.
    if (document.getElementById(SHAKE_ANIMATION_ID) != null) {
        return
    }

    val styleElement = document.createElement("style") as HTMLStyleElement
    styleElement.id = SHAKE_ANIMATION_ID
    styleElement.innerHTML = """
        @keyframes shake {
            /* The base transform is to center the element. We add translateX to it for the shake. */
            10%, 90% {
                transform: translate(-50%, -50%) translateX(-2px);
            }
            20%, 80% {
                transform: translate(-50%, -50%) translateX(4px);
            }
            30%, 50%, 70% {
                transform: translate(-50%, -50%) translateX(-8px);
            }
            40%, 60% {
                transform: translate(-50%, -50%) translateX(8px);
            }
        }
    """.trimIndent()

    document.head?.appendChild(styleElement)
}

/**
 * Creates the blood splatter image element and styles it.
 * The element is hidden by default.
 *
 * @return The HTMLImageElement for the blood splatter.
 */
fun createBloodSplatterElement(): HTMLImageElement {
    // Ensure the CSS animation rule is available in the document.
    injectShakeAnimation()

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
        display = "none" // Start hidden
    }

    return splatterImage
}

/**
 * Triggers the blood splatter effect.
 * It makes the image visible, applies a shake animation, then hides it again.
 *
 * @param splatterImage The element created by `createBloodSplatterElement`.
 */
fun showBloodSplatterEffect(splatterImage: HTMLImageElement) {
    splatterImage.style.display = "block"
    // Apply the shake animation. The duration (0.8s) is less than the total display time (1.2s).
    splatterImage.style.animation = "shake 0.8s cubic-bezier(.36,.07,.19,.97)"

    window.setTimeout({
        splatterImage.style.display = "none"
        // IMPORTANT: Reset the animation property so it can be triggered again next time.
        splatterImage.style.animation = ""
    }, 1200)
}