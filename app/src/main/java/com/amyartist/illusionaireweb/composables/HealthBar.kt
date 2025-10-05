package com.amyartist.illusionaireweb.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HealthBar(
    currentHealth: Int,
    maxHealth: Int,
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFF67DA76),
    backgroundColor: Color = Color.DarkGray.copy(alpha = 0.5f),
    borderColor: Color = Color.White.copy(alpha = 0.7f)
) {
    val healthPercentage = if (maxHealth > 0) currentHealth.toFloat() / maxHealth.toFloat() else 0f

    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(28.dp)
                .clip(MaterialTheme.shapes.small)
                .background(backgroundColor)
                .border(1.dp, borderColor, MaterialTheme.shapes.small)
                .padding(horizontal = 4.dp, vertical = 2.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            LinearProgressIndicator(
                progress = healthPercentage,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.extraSmall),
                color = barColor,
                trackColor = Color.Transparent,
                strokeCap = StrokeCap.Round
            )

            Text(
                text = "HP: $currentHealth / $maxHealth",
                modifier = Modifier.align(Alignment.Center),
                color = Color.White,
                fontSize = 12.sp,
                style = LocalTextStyle.current.copy(shadow = Shadow(Color.Black, blurRadius = 3f))
            )
        }
    }
}

