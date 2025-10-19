package com.illusionaireweb

import kotlinx.browser.document
import org.w3c.dom.HTMLStyleElement

private const val IN_PLACE_SHAKE_ANIMATION_ID = "in-place-shake-animation-style"
private const val CENTERED_SHAKE_ANIMATION_ID = "centered-shake-animation-style"

/**
 * Injects a shake animation that moves an element relative to its CURRENT position.
 * Ideal for elements that are already positioned, like the avatar.
 * The keyframe name is `shake-in-place`.
 */
internal fun injectInPlaceShakeAnimation() {
    if (document.getElementById(IN_PLACE_SHAKE_ANIMATION_ID) != null) return

    val styleElement = document.createElement("style") as HTMLStyleElement
    styleElement.id = IN_PLACE_SHAKE_ANIMATION_ID
    styleElement.innerHTML = """
        @keyframes shake-in-place {
            10%, 90% { transform: translateX(-2px); }
            20%, 80% { transform: translateX(4px); }
            30%, 50%, 70% { transform: translateX(-8px); }
            40%, 60% { transform: translateX(8px); }
        }
    """.trimIndent()
    document.head?.appendChild(styleElement)
}

/**
 * Injects a shake animation that ALSO handles centering the element.
 * Ideal for effects that appear in the middle of the screen, like the blood splatter.
 * The keyframe name is `shake-from-center`.
 */
internal fun injectCenteredShakeAnimation() {
    if (document.getElementById(CENTERED_SHAKE_ANIMATION_ID) != null) return

    val styleElement = document.createElement("style") as HTMLStyleElement
    styleElement.id = CENTERED_SHAKE_ANIMATION_ID
    styleElement.innerHTML = """
        @keyframes shake-from-center {
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