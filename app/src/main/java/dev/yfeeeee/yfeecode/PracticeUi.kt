// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

private fun extOf(l: Lang) = when (l) {
    Lang.PYTHON -> "py"; Lang.JS -> "js"; Lang.HTML -> "html"; Lang.CSS -> "css"
    Lang.CPP -> "cpp"; Lang.CSHARP -> "cs"; else -> "txt"
}

@Composable
fun PracticeScreen(p: Practice, prog: Progress, onExit: () -> Unit) {
    val ctx = LocalContext.current
    val state = remember(p.id) { EditorState(File(ctx.filesDir, "learn/" + p.id + "." + extOf(p.lang)), p.starter) }
    var verdict by remember(p.id) { mutableStateOf<String?>(null) }
    var ok by remember(p.id) { mutableStateOf(false) }
    var busy by remember(p.id) { mutableStateOf(false) }
    var gained by remember(p.id) { mutableIntStateOf(0) }
    val clip = LocalClipboardManager.current
    val hints = remember(state.text, state.cursor) { Hints.analyze(state.text, state.lang, state.lineIndex) }

    BackHandler { onExit() }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onExit) { Text("✕", color = Color(0xFF8B7FA8), fontSize = 18.sp) }
            Text(p.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(p.lang.label, color = Color(0xFFC084FC), fontSize = 12.sp, modifier = Modifier.padding(end = 8.dp))
        }
        Text(
            p.task, color = Color(0xFFE6DDF7), fontSize = 14.sp, lineHeight = 20.sp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)
                .background(Color(0xFF150B24), RoundedCornerShape(10.dp)).padding(10.dp)
        )
        if (hints.isNotEmpty()) {
            Column(Modifier.fillMaxWidth().padding(top = 4.dp).background(Color(0xFF2A1733)).padding(horizontal = 10.dp, vertical = 3.dp)) {
                hints.forEach { Text("💡 $it", color = Color(0xFFE3B341), fontSize = 11.sp) }
            }
        }
        CodeEditor(state, Modifier.weight(1f).fillMaxWidth().padding(top = 4.dp))
        verdict?.let { v ->
            Text(
                (if (ok) "✓ Верно! " else "✗ ") + v + if (ok && gained > 0) "  +$gained XP" else "",
                color = if (ok) Color(0xFF4ADE80) else Color(0xFFF87171), fontFamily = FontFamily.Monospace, fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().background(if (ok) Color(0x224ADE80) else Color(0x22F87171)).padding(10.dp)
            )
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = {
                    busy = true
                    Practices.verify(p, state.text) { good, msg ->
                        busy = false; ok = good; verdict = msg
                        if (good && !prog.practiceDone(p.id)) { prog.completePractice(p.id, 15); gained = 15 }
                    }
                },
                enabled = !busy,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC084FC), contentColor = Color.Black)
            ) { Text(if (busy) "Проверяю…" else "Проверить") }
            TextButton(onClick = { state.replaceAll(p.starter); verdict = null }) { Text("Сбросить", color = Color(0xFFC084FC)) }
        }
        DevKeyboard(
            onText = { state.insert(it) },
            onCmd = { c ->
                when (c) {
                    Cmd.BACK -> state.backspace()
                    Cmd.ENTER -> state.enter()
                    Cmd.TAB -> state.tab()
                    Cmd.LEFT -> state.left()
                    Cmd.RIGHT -> state.right()
                    Cmd.UP -> state.up()
                    Cmd.DOWN -> state.down()
                    Cmd.HOME -> state.home()
                    Cmd.END -> state.end()
                    Cmd.PASTE -> clip.getText()?.text?.let { state.insert(it) }
                    else -> {}
                }
            }
        )
    }
}
