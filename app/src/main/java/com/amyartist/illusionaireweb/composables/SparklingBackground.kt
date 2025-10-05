package com.amyartist.illusionaireweb.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.isActive
import kotlin.random.Random

data class Sparkle(
    var x: Float,
    var y: Float,
    var speed: Float,
    var size: Dp,
    var color: Color,
    var alpha: Float = 1f,
    val rotation: Float = Random.nextFloat() * 360
)

@Composable
fun SparklingBackground(
    modifier: Modifier = Modifier,
    particleCount: Int = 100,
    minSparkleSize: Dp = 1.dp,
    maxSparkleSize: Dp = 4.dp,
    minSpeed: Float = 10f, // pixels per second
    maxSpeed: Float = 50f, // pixels per second
    sparkleColors: List<Color> = listOf(Color.White, Color.LightGray, Color(0xFFF0E68C))
) {
    val particles = remember { mutableStateListOf<Sparkle>() }
    val density = LocalDensity.current.density
    var lastFrameTimeNanos by remember { mutableLongStateOf(System.nanoTime()) }

    LaunchedEffect(density) {
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            val currentFrameTimeNanos = withFrameNanos { it }
            val deltaTimeSeconds = (currentFrameTimeNanos - lastFrameTimeNanos) / 1_000_000_000f
            lastFrameTimeNanos = currentFrameTimeNanos

            val newParticles = particles.map { particle ->
                var newY = particle.y + particle.speed * deltaTimeSeconds * density
                var newAlpha = particle.alpha + Random.nextFloat() * 0.1f - 0.05f
                newAlpha = newAlpha.coerceIn(0.3f, 1f)

                particle.copy(y = newY, alpha = newAlpha)
            }.toMutableList()
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val currentFrameTimeNanos = System.nanoTime()
        val deltaTimeSeconds = if (lastFrameTimeNanos == 0L) 0f else (currentFrameTimeNanos - lastFrameTimeNanos) / 1_000_000_000f

        if (particles.isEmpty() && canvasWidth > 0 && canvasHeight > 0) {
            for (i in 0 until particleCount) {
                particles.add(
                    Sparkle(
                        x = Random.nextFloat() * canvasWidth,
                        y = Random.nextFloat() * canvasHeight,
                        speed = (Random.nextFloat() * (maxSpeed - minSpeed) + minSpeed),
                        size = Dp(Random.nextFloat() * (maxSparkleSize.value - minSparkleSize.value) + minSparkleSize.value),
                        color = sparkleColors.random(),
                        alpha = Random.nextFloat() * 0.5f + 0.5f
                    )
                )
            }
            lastFrameTimeNanos = System.nanoTime()
        } else if (canvasWidth > 0 && canvasHeight > 0) {
            val newParticlesList = mutableListOf<Sparkle>()
            particles.forEach { particle ->
                var updatedX = particle.x
                var updatedY = particle.y + particle.speed * deltaTimeSeconds * density
                var updatedSpeed = particle.speed
                var updatedSize = particle.size
                var updatedColor = particle.color
                var updatedAlpha = particle.alpha + Random.nextFloat() * 0.1f - 0.05f
                updatedAlpha = updatedAlpha.coerceIn(0.3f, 1f)

                if (updatedY > canvasHeight + particle.size.toPx()) {
                    updatedY = -particle.size.toPx() // Reset above the screen
                    updatedX = Random.nextFloat() * canvasWidth
                    updatedSpeed = (Random.nextFloat() * (maxSpeed - minSpeed) + minSpeed)
                    updatedSize = Dp(Random.nextFloat() * (maxSparkleSize.value - minSparkleSize.value) + minSparkleSize.value)
                    updatedColor = sparkleColors.random()
                }
                newParticlesList.add(
                    particle.copy(
                        x = updatedX,
                        y = updatedY,
                        speed = updatedSpeed,
                        size = updatedSize,
                        color = updatedColor,
                        alpha = updatedAlpha
                    )
                )
            }
            particles.clear()
            particles.addAll(newParticlesList)
            lastFrameTimeNanos = currentFrameTimeNanos // Update for next frame
        }

        // Draw particles
        particles.forEach { particle ->
            drawSparkle(particle)
        }
    }
}

fun DrawScope.drawSparkle(sparkle: Sparkle) {
    drawCircle(
        color = sparkle.color,
        radius = sparkle.size.toPx() / 2,
        center = Offset(sparkle.x, sparkle.y),
        alpha = sparkle.alpha
    )
}

