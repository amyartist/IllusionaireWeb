package com.illusionaireweb

import kotlinx.browser.document
import org.w3c.dom.HTMLStyleElement

fun injectGlobalStyles() {
    val style = document.createElement("style") as HTMLStyleElement
    style.type = "text/css"
    style.innerHTML = """
        @media (max-width: 768px) {
            #avatar-display {
                width: 120px !important;
            }
            #buttons-container button {
                width: 150px !important;
                height: 45px !important;
                font-size: 14px !important;
            }
            #buttons-container {
                 column-gap: 10px !important;
                 row-gap: 5px !important;
                 bottom: 10px !important;
            }
            #monster-container {
                width: 220px !important;
            }
            #monster-container button {
                width: 120px !important;
                height: 40px !important;
                font-size: 14px !important;
            }
            #equipped-weapon-icon {
                width: 40px !important;
                height: 40px !important;
            }
            #health-bar-container {
                width: 150px !important;
                height: 20px !important;
            }
        }

        @media (max-width: 480px) {
            #avatar-display {
                width: 80px !important;
                top: 5px !important;
                right: 5px !important;
            }
            #buttons-container button {
                width: 120px !important;
                height: 40px !important;
                font-size: 12px !important;
            }
            #buttons-container {
                 column-gap: 5px !important;
                 row-gap: 5px !important;
                 bottom: 5px !important;
            }
            #monster-container {
                width: 180px !important;
            }
            #monster-container button {
                width: 100px !important;
                height: 35px !important;
                font-size: 12px !important;
            }
            #equipped-weapon-icon {
                width: 35px !important;
                height: 35px !important;
            }
            #health-bar-container {
                width: 120px !important;
                height: 18px !important;
            }
        }
    """.trimIndent()
    document.head?.appendChild(style)
}
