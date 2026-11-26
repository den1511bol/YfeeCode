// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/** Text prompt that uses the in-app keyboard (no system IME). */
@Composable
fun NameDialog(title: String, initial: String = "", onOk: (String) -> Unit, onCancel: () -> Unit) {
    var value by remember { mutableStateOf(initial) }
    Dialog(onDismissRequest = onCancel, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(Modifier.fillMaxWidth().background(Color(0xFF240B0B)).padding(8.dp)) {
            Text(title, color = Color.White, modifier = Modifier.padding(8.dp))
            Text(
                value + "▏", color = Color(0xFFEF4444), fontFamily = FontFamily.Monospace,
                modifier = Modifier.fillMaxWidth().background(Color(0xFF120606)).padding(12.dp)
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCancel) { Text("Отмена") }
                TextButton(onClick = { if (value.isNotBlank()) onOk(value.trim()) }) { Text("OK") }
            }
            DevKeyboard(
                onText = { value += it.replace("()", "(").replace("[]", "[").replace("{}", "{") },
                onCmd = { c ->
                    when (c) {
                        Cmd.BACK -> value = value.dropLast(1)
                        Cmd.ENTER -> if (value.isNotBlank()) onOk(value.trim())
                        else -> {}
                    }
                }
            )
        }
    }
}
