package qrsimple.franz.com.qrsimple.feature.scanner.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import qrsimple.franz.com.qrsimple.core.theme.CyanAccent
import qrsimple.franz.com.qrsimple.core.theme.EmeraldPrimary

@Composable
fun ScannerOverlay(
    isScanningActive: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanner_laser")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Calculate viewfinder box size (e.g., 70% of screen width, min 240dp, max 300dp)
            val boxSize = (canvasWidth * 0.72f).coerceIn(240.dp.toPx(), 320.dp.toPx())
            val left = (canvasWidth - boxSize) / 2f
            val top = (canvasHeight - boxSize) / 2.2f // slightly above center for ergonomic preview
            val right = left + boxSize
            val bottom = top + boxSize
            val cornerRadius = 24.dp.toPx()
            val bracketLength = 32.dp.toPx()
            val strokeWidth = 4.dp.toPx()

            // 1. Draw dark scrim around cutout using Path with EvenOdd or BlendMode.Clear
            val scrimColor = Color.Black.copy(alpha = 0.58f)
            val path = Path().apply {
                // Outer bounds
                addRect(Rect(0f, 0f, canvasWidth, canvasHeight))
                // Inner cutout
                addRoundRect(
                    RoundRect(
                        left = left,
                        top = top,
                        right = right,
                        bottom = bottom,
                        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                    )
                )
            }
            drawPath(path = path, color = scrimColor, blendMode = BlendMode.SrcOver)

            // 2. Cut out inner rectangle completely
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(boxSize, boxSize),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                blendMode = BlendMode.Clear
            )

            // 3. Draw 4 modern Emerald corner brackets
            val bracketColor = EmeraldPrimary

            // Top-Left Corner
            drawLine(
                color = bracketColor,
                start = Offset(left, top + bracketLength),
                end = Offset(left, top + cornerRadius),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawArc(
                color = bracketColor,
                startAngle = 180f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(left, top),
                size = Size(cornerRadius * 2, cornerRadius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawLine(
                color = bracketColor,
                start = Offset(left + cornerRadius, top),
                end = Offset(left + bracketLength, top),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Top-Right Corner
            drawLine(
                color = bracketColor,
                start = Offset(right - bracketLength, top),
                end = Offset(right - cornerRadius, top),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawArc(
                color = bracketColor,
                startAngle = 270f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(right - cornerRadius * 2, top),
                size = Size(cornerRadius * 2, cornerRadius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawLine(
                color = bracketColor,
                start = Offset(right, top + cornerRadius),
                end = Offset(right, top + bracketLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Bottom-Left Corner
            drawLine(
                color = bracketColor,
                start = Offset(left, bottom - bracketLength),
                end = Offset(left, bottom - cornerRadius),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawArc(
                color = bracketColor,
                startAngle = 90f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(left, bottom - cornerRadius * 2),
                size = Size(cornerRadius * 2, cornerRadius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawLine(
                color = bracketColor,
                start = Offset(left + cornerRadius, bottom),
                end = Offset(left + bracketLength, bottom),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Bottom-Right Corner
            drawLine(
                color = bracketColor,
                start = Offset(right - bracketLength, bottom),
                end = Offset(right - cornerRadius, bottom),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawArc(
                color = bracketColor,
                startAngle = 0f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(right - cornerRadius * 2, bottom - cornerRadius * 2),
                size = Size(cornerRadius * 2, cornerRadius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawLine(
                color = bracketColor,
                start = Offset(right, bottom - cornerRadius),
                end = Offset(right, bottom - bracketLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // 4. Draw Animated Laser Line (only when active)
            if (isScanningActive) {
                val laserY = top + (boxSize * laserProgress)
                val laserBrush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        EmeraldPrimary.copy(alpha = 0.8f),
                        CyanAccent,
                        EmeraldPrimary.copy(alpha = 0.8f),
                        Color.Transparent
                    ),
                    startX = left + 10.dp.toPx(),
                    endX = right - 10.dp.toPx()
                )

                drawLine(
                    brush = laserBrush,
                    start = Offset(left + 12.dp.toPx(), laserY),
                    end = Offset(right - 12.dp.toPx(), laserY),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Instructional Text below reticle
        Text(
            text = "Apunta la cámara a un código QR",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 340.dp)
        )
    }
}

