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

/** Finds the bracket pair next to the caret: returns (a, b) offsets or null. */
fun matchBracket(t: String, cur: Int): Pair<Int, Int>? {
    if (t.isEmpty() || t.length > 60_000) return null
    val open = "([{"; val close = ")]}"
    for (i in intArrayOf(cur - 1, cur)) {
        if (i < 0 || i >= t.length) continue
        val c = t[i]
        val o = open.indexOf(c); val k = close.indexOf(c)
        if (o >= 0) {
            var d = 0; var j = i
            while (j < t.length && j - i < 20_000) {
                if (t[j] == c) d++ else if (t[j] == close[o]) { d--; if (d == 0) return i to j }
                j++
            }
        } else if (k >= 0) {
            var d = 0; var j = i
            while (j >= 0 && i - j < 20_000) {
                if (t[j] == c) d++ else if (t[j] == open[k]) { d--; if (d == 0) return j to i }
                j--
            }
        }
    }
    return null
}

/** Custom editor: does not use the system keyboard at all. */
@Composable
fun CodeEditor(state: EditorState, modifier: Modifier = Modifier) {
    val vScroll = rememberScrollState()
    val hScroll = rememberScrollState()
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    var viewportH by remember { mutableIntStateOf(0) }

    val styled = remember(state.text, state.lang) { Highlighter.highlight(state.text, state.lang) }
    val style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = Settings.fontSize.sp, lineHeight = (Settings.fontSize + 6).sp, color = Color(0xFFE6EDF3))
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
            .diamondPattern(Color(0xFF120606), Color(0x0FB91C1C), 44.dp)
            .onSizeChanged { viewportH = it.height }
            .verticalScroll(vScroll)
    ) {
        Text(
            gutter,
            style = style.copy(color = Color(0xFF8C5B5D)),
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
                        detectTapGestures(
                            onTap = { pos -> layout?.let { state.moveTo(it.getOffsetForPosition(pos)) } },
                            onDoubleTap = { pos -> layout?.let { state.selectWordAt(it.getOffsetForPosition(pos)) } },
                            onLongPress = { pos ->
                                layout?.let {
                                    state.clearSel()
                                    state.moveTo(it.getOffsetForPosition(pos))
                                    state.toggleSelect()
                                }
                            }
                        )
                    }
                    .drawWithContent {
                        val l0 = layout
                        if (l0 != null && state.hasSel && state.selEnd <= l0.layoutInput.text.length) {
                            drawPath(l0.getPathForRange(state.selStart, state.selEnd), Color(0x663B82F6))
                        }
                        val l1 = layout
                        if (l1 != null) {
                            val m = matchBracket(state.text, state.cursor)
                            if (m != null && m.second < l1.layoutInput.text.length) {
                                for (idx in intArrayOf(m.first, m.second)) {
                                    drawPath(l1.getPathForRange(idx, idx + 1), Color(0x55B91C1C))
                                }
                            }
                        }
                        drawContent()
                        val l = layout
                        if (l != null && state.cursor <= l.layoutInput.text.length) {
                            val r = l.getCursorRect(state.cursor)
                            drawRect(Color(0xFFEF4444), Offset(r.left, r.top), Size(2.5f, r.height))
                        }
                    }
            )
        }
    }
}
