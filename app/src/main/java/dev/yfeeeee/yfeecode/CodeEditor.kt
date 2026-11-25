// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Custom editor: does not use the system keyboard at all. */
@Composable
fun CodeEditor(state: EditorState, modifier: Modifier = Modifier) {
    val vScroll = rememberScrollState()
    val hScroll = rememberScrollState()
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    var viewportH by remember { mutableIntStateOf(0) }

    val styled = remember(state.text, state.lang) { Highlighter.highlight(state.text, state.lang) }
    val style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, lineHeight = 20.sp, color = Color(0xFFE6EDF3))
    val lineCount = state.text.count { it == '\n' } + 1
    val gutter = remember(lineCount) { (1..lineCount).joinToString("\n") }

    // keep the caret visible while typing
    LaunchedEffect(state.cursor, state.text, layout) {
        val l = layout ?: return@LaunchedEffect
        if (state.cursor > l.layoutInput.text.length) return@LaunchedEffect
        val r = l.getCursorRect(state.cursor)
        val top = r.top.toInt(); val bottom = r.bottom.toInt()
        if (top < vScroll.value) vScroll.scrollTo(top)
        else if (bottom > vScroll.value + viewportH) vScroll.scrollTo(bottom - viewportH)
        val left = r.left.toInt()
        if (left < hScroll.value) hScroll.scrollTo(maxOf(0, left - 40))
    }

    Row(
        modifier
            .background(Color(0xFF0F1115))
            .onSizeChanged { viewportH = it.height }
            .verticalScroll(vScroll)
    ) {
        Text(
            gutter,
            style = style.copy(color = Color(0xFF5C6370)),
            modifier = Modifier.width(40.dp).padding(start = 6.dp, end = 6.dp, top = 6.dp)
        )
        Box(Modifier.horizontalScroll(hScroll)) {
            Text(
                styled,
                style = style,
                softWrap = false,
                onTextLayout = { layout = it },
                modifier = Modifier
                    .padding(top = 6.dp, end = 60.dp, bottom = 120.dp)
                    .defaultMinSize(minWidth = 300.dp, minHeight = 300.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { pos ->
                            layout?.let { state.setCursor(it.getOffsetForPosition(pos)) }
                        }
                    }
                    .drawWithContent {
                        val l0 = layout
                        if (l0 != null && state.hasSel && state.selEnd <= l0.layoutInput.text.length) {
                            drawPath(l0.getPathForRange(state.selStart, state.selEnd), Color(0x663B82F6))
                        }
                        drawContent()
                        val l = layout
                        if (l != null && state.cursor <= l.layoutInput.text.length) {
                            val r = l.getCursorRect(state.cursor)
                            drawRect(Color(0xFF7CE0A3), Offset(r.left, r.top), Size(2.5f, r.height))
                        }
                    }
            )
        }
    }
}
