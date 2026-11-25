// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class Cmd { BACK, ENTER, TAB, LEFT, RIGHT, UP, DOWN, HOME, END, PASTE, DELWORD }

private val en = listOf("qwertyuiop", "asdfghjkl", "zxcvbnm")
private val ru = listOf("йцукенгшщзхъ", "фывапролджэ", "ячсмитьбю")
private val sym = listOf("1234567890", "@#$%&*-+=_", "!?:;,.|\\/~")
private val code = listOf("()", "[]", "{}", "\"\"", "''", "<>", "``")

private val keyBg = Color(0xFF2A1647)
private val keyBgAlt = Color(0xFF3B2068)
private val accent = Color(0xFF7C3AED)

@Composable
fun DevKeyboard(onText: (String) -> Unit, onCmd: (Cmd) -> Unit, modifier: Modifier = Modifier) {
    var layer by remember { mutableIntStateOf(0) } // 0 EN, 1 RU, 2 SYM
    var shift by remember { mutableStateOf(false) }

    Column(
        modifier.fillMaxWidth().diamondPattern(Color(0xFF0E0720), Color(0x22C084FC), 22.dp).padding(horizontal = 3.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // developer row: pairs and navigation
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Key("Tab", 1.3f, keyBgAlt) { onCmd(Cmd.TAB) }
            code.forEach { p -> Key(p, 1f, keyBgAlt) { onText(p) } }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Key("←", 1f, keyBgAlt, repeat = true) { onCmd(Cmd.LEFT) }
            Key("↑", 1f, keyBgAlt, repeat = true) { onCmd(Cmd.UP) }
            Key("↓", 1f, keyBgAlt, repeat = true) { onCmd(Cmd.DOWN) }
            Key("→", 1f, keyBgAlt, repeat = true) { onCmd(Cmd.RIGHT) }
            Key("Home", 1.3f, keyBgAlt) { onCmd(Cmd.HOME) }
            Key("End", 1.3f, keyBgAlt) { onCmd(Cmd.END) }
            Key("Вставить", 2f, keyBgAlt) { onCmd(Cmd.PASTE) }
        }

        val rows = when (layer) { 1 -> ru; 2 -> sym; else -> en }
        rows.forEachIndexed { idx, row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                if (idx == 2 && layer != 2) Key(if (shift) "⇧" else "⇪", 1.4f, keyBgAlt) { shift = !shift }
                row.forEach { ch ->
                    val s = if (shift && layer != 2) ch.toString().uppercase() else ch.toString()
                    Key(s, 1f, keyBg) { onText(s); if (shift) shift = false }
                }
                if (idx == 2) Key("⌫", 1.4f, keyBgAlt, repeat = true) { onCmd(Cmd.BACK) }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Key(if (layer == 2) "ABC" else "123", 1.4f, keyBgAlt) { layer = if (layer == 2) 0 else 2 }
            Key(if (layer == 1) "EN" else "RU", 1.2f, keyBgAlt) { layer = if (layer == 1) 0 else 1 }
            Key(".", 1f, keyBg) { onText(".") }
            Key("пробел", 4f, keyBg) { onText(" ") }
            Key(",", 1f, keyBg) { onText(",") }
            Key("⏎", 1.6f, accent) { onCmd(Cmd.ENTER) }
        }
    }
}

@Composable
private fun RowScope.Key(
    label: String, weight: Float, bg: Color, repeat: Boolean = false, onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val current by rememberUpdatedState(onClick)
    Box(
        Modifier
            .weight(weight)
            .height(42.dp)
            .background(
                if (pressed) Brush.verticalGradient(listOf(Color(0xFFA78BFA), Color(0xFF7C3AED)))
                else Brush.verticalGradient(listOf(bg, bg.copy(red = bg.red * 0.6f, green = bg.green * 0.6f, blue = bg.blue * 0.75f))),
                RoundedCornerShape(9.dp)
            )
            .border(1.dp, Color(0x55C084FC), RoundedCornerShape(9.dp))
            .pointerInput(repeat) {
                detectTapGestures(onPress = {
                    pressed = true
                    current()
                    if (repeat) {
                        coroutineScope {
                            val job = launch { delay(400); while (true) { current(); delay(45) } }
                            tryAwaitRelease()
                            job.cancel()
                        }
                    } else tryAwaitRelease()
                    pressed = false
                })
            },
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = Color.White, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
    }
}
