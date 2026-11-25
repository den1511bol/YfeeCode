// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Black-violet gradient with a soft diamond lattice and glowing nodes. */
fun Modifier.diamondPattern(
    base: Color = Color(0xFF0A0612),
    line: Color = Color(0x1AC084FC),
    step: Dp = 26.dp
): Modifier = drawBehind {
    drawRect(Brush.verticalGradient(listOf(base, Color(0xFF12082A), base)))
    val s = step.toPx()
    val h = size.height
    var x = -h
    while (x < size.width) {
        drawLine(line, Offset(x, 0f), Offset(x + h, h), 1f)
        drawLine(line, Offset(x + h, 0f), Offset(x, h), 1f)
        x += s
    }
    // small glowing nodes where lines cross on the top edge
    var nx = 0f
    while (nx < size.width) {
        drawCircle(line.copy(alpha = 0.35f), 1.6f, Offset(nx, 0f))
        nx += s
    }
}
