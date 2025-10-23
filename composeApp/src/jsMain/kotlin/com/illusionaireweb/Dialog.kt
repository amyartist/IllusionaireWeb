package com.illusionaireweb

import kotlinx.browser.document
import org.w3c.dom.*

fun createDialogElement(onOkClick: () -> Unit): HTMLDivElement {
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

    val dialogBox = document.createElement("div") as HTMLDivElement
    with(dialogBox.style) {
        backgroundColor = GameColors.DIALOG_BACKGROUND
        border = "2px solid ${GameColors.BORDER_YELLOW}"
        borderRadius = "8px"
        padding = "25px"
        maxWidth = "450px"
        textAlign = "center"
    }

    val messageText = document.createElement("p") as HTMLParagraphElement
    messageText.id = "dialog-message" // ID to easily update the text
    with(messageText.style) {
        color = GameColors.DIALOG_TEXT
        fontSize = "18px"
        lineHeight = "1.5"
        marginBottom = "20px"
    }

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

    dialogBox.appendChild(messageText)
    dialogBox.appendChild(okButton)
    overlay.appendChild(dialogBox)

    return overlay
}

fun updateDialog(overlay: HTMLDivElement, message: String?) {
    if (message != null) {
        val messageText = overlay.querySelector("#dialog-message") as? HTMLParagraphElement
        messageText?.textContent = message
        overlay.style.display = "flex"
    } else {
        overlay.style.display = "none"
    }
}

fun createRiddleDialog(onSubmit: (String) -> Unit, onDismiss: () -> Unit): HTMLDivElement {
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

    val questionText = document.createElement("p") as HTMLParagraphElement
    questionText.id = "riddle-question-text"
    with(questionText.style) {
        color = GameColors.DIALOG_TEXT
        fontSize = "18px"
        lineHeight = "1.5"
    }

    val answerInput = document.createElement("input") as HTMLInputElement
    answerInput.type = "text"
    answerInput.id = "riddle-answer-input"
    answerInput.placeholder = "Your answer..."
    with(answerInput.style) {
        width = "90%"
        padding = "10px"
        fontSize = "16px"
        margin = "0 auto"
    }

    val buttonContainer = document.createElement("div") as HTMLDivElement
    with(buttonContainer.style) {
        display = "flex"
        justifyContent = "space-around"
        width = "100%"
        marginTop = "10px"
    }

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

    buttonContainer.appendChild(giveUpButton)
    buttonContainer.appendChild(submitButton)
    dialogBox.appendChild(questionText)
    dialogBox.appendChild(answerInput)
    dialogBox.appendChild(buttonContainer)
    overlay.appendChild(dialogBox)

    return overlay
}

fun updateRiddleDialog(riddleOverlay: HTMLDivElement, question: String?) {
    if (question != null) {
        val questionText = riddleOverlay.querySelector("#riddle-question-text") as? HTMLParagraphElement
        val answerInput = riddleOverlay.querySelector("#riddle-answer-input") as? HTMLInputElement

        questionText?.textContent = question
        answerInput?.value = ""

        riddleOverlay.style.display = "flex"
        answerInput?.focus()
    } else {
        riddleOverlay.style.display = "none"
    }
}
