// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

/**
 * Black-violet gradient with a diamond lattice. Every line and node breathes
 * very slowly with its own phase, so the pulse drifts across the screen in no fixed order.
 */
fun Modifier.diamondPattern(
    base: Color = Color(0xFF120606),
    line: Color = Color(0x1AB91C1C),
    step: Dp = 26.dp
): Modifier = composed {
    val t = rememberInfiniteTransition(label = "pattern")
    val phase by t.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(16000, easing = LinearEasing), RepeatMode.Restart),
        label = "phase"
    )
    Modifier.drawBehind {
        drawRect(Brush.verticalGradient(listOf(base, Color(0xFF2A0809), base)))
        val s = step.toPx()
        val h = size.height
        // soft 0.35..1.0 breathing, different phase for each element
        val ph = if (Settings.pulse) phase else 0.25f
        fun pulse(k: Float): Float =
            0.35f + 0.65f * (0.5f + 0.5f * sin(2.0 * PI * (ph + k)).toFloat())

        var i = 0
        var x = -h
        while (x < size.width) {
            val a = line.alpha
            drawLine(line.copy(alpha = a * pulse(i * 0.173f)), Offset(x, 0f), Offset(x + h, h), 1f)
            drawLine(line.copy(alpha = a * pulse(i * 0.291f + 0.5f)), Offset(x + h, 0f), Offset(x, h), 1f)
            x += s
            i++
        }
        // glowing nodes on a coarser grid, each with its own rhythm
        val ns = s * 2f
        var gx = 0
        var px = 0f
        while (px < size.width) {
            var gy = 0
            var py = 0f
            while (py < size.height) {
                val k = gx * 0.37f + gy * 0.61f
                drawCircle(line.copy(alpha = 0.55f * pulse(k)), 2f, Offset(px, py))
                py += ns; gy++
            }
            px += ns; gx++
        }
    }
}
