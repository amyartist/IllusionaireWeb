package com.amyartist.illusionaireweb.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun imageButton(
    onButtonClick: () -> Unit,
    @DrawableRes iconResId: Int,
    width: Dp,
    height: Dp,
    text: String?,
    fontSize: TextUnit = 16.sp,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val imageAlpha = if (isPressed) 0.3f else 1.0f
    val defaultTextStyle = TextStyle(
        color = Color(red = 65, green = 42, blue = 11, alpha = 255),
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false),
                onClick = onButtonClick
            )
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = "Custom Action",
            modifier = Modifier
                .size(width, height)
                .alpha(imageAlpha)
        )
        text?.let {
            Text(
                text = it,
                style = defaultTextStyle,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(4.dp)
            )
        }
    }

}