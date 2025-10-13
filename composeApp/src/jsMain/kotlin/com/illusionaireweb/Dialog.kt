package com.illusionaireweb

import kotlinx.browser.document
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLParagraphElement

/**
 * Creates the full dialog element, including the overlay. It's hidden by default.
 *
 * @param onOkClick A lambda function that will be executed when the "OK" button is clicked.
 * @return The top-level overlay element for the dialog.
 */
fun createDialogElement(onOkClick: () -> Unit): HTMLDivElement {
    // 1. Create the overlay (the semi-transparent background)
    val overlay = document.createElement("div") as HTMLDivElement
    overlay.id = "dialog-overlay"
    with(overlay.style) {
        position = "absolute"
        top = "0"
        left = "0"
        width = "100%"
        height = "100%"
        backgroundColor = "transparent"
        display = "flex"
        alignItems = "center"
        justifyContent = "center"
        display = "none"
        zIndex = "100"
    }

    // 2. Create the dialog box itself
    val dialogBox = document.createElement("div") as HTMLDivElement
    with(dialogBox.style) {
        backgroundColor = GameColors.DIALOG_BACKGROUND
        border = "2px solid ${GameColors.BORDER_YELLOW}"
        borderRadius = "8px"
        padding = "25px"
        maxWidth = "450px"
        textAlign = "center"
    }

    // 3. Create the message paragraph
    val messageText = document.createElement("p") as HTMLParagraphElement
    messageText.id = "dialog-message" // ID to easily update the text
    with(messageText.style) {
        color = GameColors.DIALOG_TEXT
        fontSize = "18px"
        lineHeight = "1.5"
        marginBottom = "20px"
    }

    // 4. Create the "OK" button
    val okButton = document.createElement("button") as HTMLButtonElement
    okButton.textContent = "OK"
    with(okButton.style) {
        backgroundImage = "url('images/button1.png')"
        backgroundSize = "cover"
        backgroundPosition = "center"
        width = "150px"
        height = "50px"
        border = "none"
        backgroundColor = "transparent"
        color = GameColors.BUTTON_TEXT_GOLD
        fontSize = "16px"
        fontWeight = "bold"
        cursor = "pointer"
    }
    // Attach the dismiss logic to the button's click event
    okButton.onclick = { onOkClick() }

    // 5. Assemble the parts
    dialogBox.appendChild(messageText)
    dialogBox.appendChild(okButton)
    overlay.appendChild(dialogBox)

    return overlay
}

/**
 * Shows or hides the dialog based on the presence of a message.
 *
 * @param overlay The dialog element created by `createDialogElement`.
 * @param message The message to display. If null, the dialog is hidden.
 */
fun updateDialog(overlay: HTMLDivElement, message: String?) {
    if (message != null) {
        val messageText = overlay.querySelector("#dialog-message") as? HTMLParagraphElement
        messageText?.textContent = message
        overlay.style.display = "flex" // Show the dialog
    } else {
        overlay.style.display = "none" // Hide the dialog
    }
}