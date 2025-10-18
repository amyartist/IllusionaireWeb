package com.illusionaireweb

import kotlinx.browser.document
import org.w3c.dom.*

// --- The two existing functions are unchanged ---

/**
 * Creates the full dialog element, including the overlay. It's hidden by default.
 * @param onOkClick A lambda function that will be executed when the "OK" button is clicked.
 * @return The top-level overlay element for the dialog.
 */
fun createDialogElement(onOkClick: () -> Unit): HTMLDivElement {
    // ... (existing implementation is perfect, no changes needed here)
    // 1. Create the overlay (the semi-transparent background)
    val overlay = document.createElement("div") as HTMLDivElement
    overlay.id = "dialog-overlay"
    with(overlay.style) {
        position = "fixed" // Use fixed to cover the whole screen regardless of scroll
        top = "0"
        left = "0"
        width = "100%"
        height = "100%"
        backgroundColor = "rgba(0, 0, 0, 0.7)" // Darken the background
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
 * @param overlay The dialog element created by `createDialogElement`.
 * @param message The message to display. If null, the dialog is hidden.
 */
fun updateDialog(overlay: HTMLDivElement, message: String?) {
    // ... (existing implementation is perfect, no changes needed here)
    if (message != null) {
        val messageText = overlay.querySelector("#dialog-message") as? HTMLParagraphElement
        messageText?.textContent = message
        overlay.style.display = "flex" // Show the dialog
    } else {
        overlay.style.display = "none" // Hide the dialog
    }
}


// --- Add the following new functions for the riddle dialog ---

/**
 * Creates and displays a special riddle dialog.
 * This is an imperative function that directly manipulates the DOM.
 *
 * @param question The riddle text to display.
 * @param onSubmit A lambda that will be called with the user's answer.
 * @param onDismiss A lambda that will be called if the user gives up.
 */
fun showRiddleDialog(question: String, onSubmit: (String) -> Unit, onDismiss: () -> Unit) {
    // 1. Create the overlay (similar to the simple dialog)
    val overlay = document.createElement("div") as HTMLDivElement
    overlay.id = "riddle-dialog-overlay" // Use a unique ID to find and remove it later
    with(overlay.style) {
        position = "fixed"
        top = "0"; left = "0"; width = "100%"; height = "100%"
        backgroundColor = "rgba(0, 0, 0, 0.7)"
        display = "flex"
        alignItems = "center"
        justifyContent = "center"
        zIndex = "101" // Higher z-index in case another dialog is open
    }

    // 2. Create the dialog box
    val dialogBox = document.createElement("div") as HTMLDivElement
    with(dialogBox.style) {
        backgroundColor = GameColors.DIALOG_BACKGROUND
        border = "2px solid ${GameColors.BORDER_YELLOW}"
        borderRadius = "8px"
        padding = "25px"
        maxWidth = "450px"
        textAlign = "center"
        display = "flex"
        flexDirection = "column"
        columnGap = "15px"
    }

    // 3. Create the riddle question text
    val questionText = document.createElement("p") as HTMLParagraphElement
    questionText.textContent = question
    with(questionText.style) {
        color = GameColors.DIALOG_TEXT
        fontSize = "18px"
        lineHeight = "1.5"
    }

    // 4. Create the text input for the answer
    val answerInput = document.createElement("input") as HTMLInputElement
    answerInput.type = "text"
    answerInput.id = "riddle-answer-input"
    answerInput.placeholder = "Your answer..."
    with(answerInput.style) {
        width = "90%"
        padding = "10px"
        fontSize = "16px"
    }

    // 5. Create a container for the buttons
    val buttonContainer = document.createElement("div") as HTMLDivElement
    with(buttonContainer.style) {
        display = "flex"
        justifyContent = "space-around"
        width = "100%"
    }

    // 6. Create the "Submit" button
    val submitButton = document.createElement("button") as HTMLButtonElement
    submitButton.textContent = "Answer"
    with(submitButton.style) { /* Add your button styles */
        backgroundImage = "url('images/button1.png')"
        backgroundSize = "cover"; backgroundPosition = "center"
        width = "150px"; height = "50px"; border = "none"; backgroundColor = "transparent"
        color = GameColors.BUTTON_TEXT_GOLD; fontSize = "16px"; fontWeight = "bold"; cursor = "pointer"
    }
    submitButton.onclick = {
        val userAnswer = (document.getElementById("riddle-answer-input") as HTMLInputElement).value
        if (userAnswer.isNotBlank()) {
            onSubmit(userAnswer)
        }
    }

    // 7. Create the "Give Up" button
    val giveUpButton = document.createElement("button") as HTMLButtonElement
    giveUpButton.textContent = "Give Up"
    with(giveUpButton.style) { /* Add your button styles, maybe a different color */
        backgroundImage = "url('images/button2.png')" // Assuming a different button image
        backgroundSize = "cover"; backgroundPosition = "center"
        width = "150px"; height = "50px"; border = "none"; backgroundColor = "transparent"
        color = GameColors.DIALOG_TEXT; fontSize = "16px"; fontWeight = "bold"; cursor = "pointer"
    }
    giveUpButton.onclick = { onDismiss() }

    // 8. Assemble the dialog
    buttonContainer.appendChild(giveUpButton)
    buttonContainer.appendChild(submitButton)
    dialogBox.appendChild(questionText)
    dialogBox.appendChild(answerInput)
    dialogBox.appendChild(buttonContainer)
    overlay.appendChild(dialogBox)

    // 9. Add the finished dialog to the page
    document.body?.appendChild(overlay)
}

/**
 * Finds and removes the riddle dialog from the DOM.
 */
fun hideRiddleDialog() {
    val overlay = document.getElementById("riddle-dialog-overlay")
    overlay?.remove()
}