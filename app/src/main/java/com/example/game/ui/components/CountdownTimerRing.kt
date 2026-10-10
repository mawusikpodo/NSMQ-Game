package com.example.game.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun CountdownTimerRing(
    secondsRemaining: Int,
    totalSeconds: Int,
    isUntimed: Boolean = false,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    strokeWidth: Dp = 3.dp
) {
    val progress = if (isUntimed || totalSeconds <= 0) {
        1f
    } else {
        (secondsRemaining.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "TimerProgressAnimation"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val canvasSize = this.size.minDimension
            val radius = (canvasSize - strokePx) / 2

            // Dim track
            drawCircle(
                color = Color.White.copy(alpha = 0.25f),
                radius = radius,
                style = Stroke(width = strokePx)
            )

            // Dynamic progress arc
            if (animatedProgress > 0f) {
                drawArc(
                    color = Color.White,
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        val displaySeconds = if (isUntimed) {
            "∞"
        } else {
            String.format(Locale.getDefault(), "%02d", secondsRemaining.coerceAtLeast(0))
        }

        Text(
            text = displaySeconds,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = if (isUntimed) 20.sp else 16.sp
        )
    }
}
