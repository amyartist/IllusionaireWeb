package com.illusionaireweb

import kotlinx.browser.document

/**
 * The main entry point for the web application.
 * This function will be executed when the JavaScript file is loaded in the browser.
 */
fun main() {
    document.addEventListener("DOMContentLoaded", {
        showGameScreen()
    })
}
