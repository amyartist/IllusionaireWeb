package com.illusionaireweb

import kotlinx.browser.document
import org.w3c.dom.*


// --- Simple "OK" Dialog (Unchanged) ---

/**
 * Creates the full simple dialog element, including the overlay. It's hidden by default.
 * This should be called only ONCE during UI setup.
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
        backgroundColor = "rgba(0, 0, 0, 0.7)"
        display = "flex"
        alignItems = "center"
        justifyContent = "center"
        zIndex = "100"
        display = "none" // Start hidden
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
    okButton.onclick = {
        SoundManager.play("select")
        onOkClick()
    }

    // 5. Assemble the parts
    dialogBox.appendChild(messageText)
    dialogBox.appendChild(okButton)
    overlay.appendChild(dialogBox)

    return overlay
}

/**
 * Updates the simple dialog's visibility and message.
 * @param overlay The dialog element created by `createDialogElement`.
 * @param message The message to display. If null, the dialog is hidden.
 */
fun updateDialog(overlay: HTMLDivElement, message: String?) {
    if (message != null) {
        val messageText = overlay.querySelector("#dialog-message") as? HTMLParagraphElement
        messageText?.textContent = message
        overlay.style.display = "flex"
    } else {
        overlay.style.display = "none"
    }
}


// --- Riddle Dialog (Refactored to Create/Update Pattern) ---

/**
 * Creates the entire riddle dialog structure, hidden by default.
 * This should be called only ONCE during UI setup.
 *
 * @param onSubmit A lambda that will be called with the user's answer.
 * @param onDismiss A lambda that will be called if the user gives up.
 * @return The main HTMLDivElement for the riddle overlay.
 */
fun createRiddleDialog(onSubmit: (String) -> Unit, onDismiss: () -> Unit): HTMLDivElement {
    // 1. Create the overlay
    val overlay = document.createElement("div") as HTMLDivElement
    overlay.id = "riddle-dialog-overlay"
    with(overlay.style) {
        position = "absolute"
        top = "0"; left = "0"; width = "100%"; height = "100%"
        backgroundColor = "rgba(0, 0, 0, 0.7)"
        display = "flex"
        alignItems = "center"
        justifyContent = "center"
        zIndex = "101"
        display = "none" // Start hidden
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
    questionText.id = "riddle-question-text" // ID to update the text
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
        margin = "0 auto" // Center the input
    }

    // 5. Create a container for the buttons
    val buttonContainer = document.createElement("div") as HTMLDivElement
    with(buttonContainer.style) {
        display = "flex"
        justifyContent = "space-around"
        width = "100%"
        marginTop = "10px"
    }

    // 6. Create the "Submit" button
    val submitButton = document.createElement("button") as HTMLButtonElement
    submitButton.textContent = "Answer"
    with(submitButton.style) {
        backgroundImage = "url('images/button1.png')"
        backgroundSize = "cover"; backgroundPosition = "center"
        width = "150px"; height = "50px"; border = "none"; backgroundColor = "transparent"
        color = GameColors.BUTTON_TEXT_GOLD; fontSize = "16px"; fontWeight = "bold"; cursor = "pointer"
    }
    submitButton.onclick = {
        SoundManager.play("select")
        val userAnswer = (overlay.querySelector("#riddle-answer-input") as HTMLInputElement).value
        if (userAnswer.isNotBlank()) {
            onSubmit(userAnswer)
        }
    }

    // 7. Create the "Give Up" button
    val giveUpButton = document.createElement("button") as HTMLButtonElement
    giveUpButton.textContent = "Give Up"
    with(giveUpButton.style) {
        backgroundImage = "url('images/button1.png')"
        backgroundSize = "cover"; backgroundPosition = "center"
        width = "150px"; height = "50px"; border = "none"; backgroundColor = "transparent"
        color = GameColors.BUTTON_TEXT_GOLD; fontSize = "16px"; fontWeight = "bold"; cursor = "pointer"
    }
    giveUpButton.onclick = {
        SoundManager.play("select")
        onDismiss()
    }

    // 8. Assemble the dialog
    buttonContainer.appendChild(giveUpButton)
    buttonContainer.appendChild(submitButton)
    dialogBox.appendChild(questionText)
    dialogBox.appendChild(answerInput)
    dialogBox.appendChild(buttonContainer)
    overlay.appendChild(dialogBox)

    return overlay
}

/**
 * Updates the riddle dialog's visibility and content.
 * @param riddleOverlay The element created by `createRiddleDialog`.
 * @param question The riddle text to display. If null, the dialog is hidden.
 */
fun updateRiddleDialog(riddleOverlay: HTMLDivElement, question: String?) {
    if (question != null) {
        val questionText = riddleOverlay.querySelector("#riddle-question-text") as? HTMLParagraphElement
        val answerInput = riddleOverlay.querySelector("#riddle-answer-input") as? HTMLInputElement

        questionText?.textContent = question
        answerInput?.value = "" // Clear previous answer

        riddleOverlay.style.display = "flex"
        answerInput?.focus() // Automatically focus the input for the user
    } else {
        riddleOverlay.style.display = "none"
    }
}
