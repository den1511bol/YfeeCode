// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import android.content.Context
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient
import com.termux.view.TerminalView
import com.termux.view.TerminalViewClient

/** Real PTY terminal (vim, nano, top) inside Alpine, rendered by Termux's terminal-view. */
object LinuxTerm {
    var session: TerminalSession? = null
    var view: TerminalView? = null
    var ctrl by mutableStateOf(false)
    var finished by mutableStateOf(false)

    val sessionClient = object : TerminalSessionClient {
        override fun onTextChanged(changedSession: TerminalSession) { view?.onScreenUpdated() }
        override fun onTitleChanged(changedSession: TerminalSession) {}
        override fun onSessionFinished(finishedSession: TerminalSession) { session = null; finished = true }
        override fun onCopyTextToClipboard(session: TerminalSession, text: String?) {
            val cm = view?.context?.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
            cm?.setPrimaryClip(android.content.ClipData.newPlainText("term", text ?: ""))
        }
        override fun onPasteTextFromClipboard(session: TerminalSession?) {
            val cm = view?.context?.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
            val t = cm?.primaryClip?.getItemAt(0)?.text?.toString()
            if (t != null) this@LinuxTerm.session?.write(t)
        }
        override fun onBell(session: TerminalSession) {}
        override fun onColorsChanged(session: TerminalSession) {}
        override fun onTerminalCursorStateChange(state: Boolean) {}
        override fun setTerminalShellPid(session: TerminalSession, pid: Int) {}
        override fun getTerminalCursorStyle(): Int? = null
        override fun logError(tag: String?, message: String?) {}
        override fun logWarn(tag: String?, message: String?) {}
        override fun logInfo(tag: String?, message: String?) {}
        override fun logDebug(tag: String?, message: String?) {}
        override fun logVerbose(tag: String?, message: String?) {}
        override fun logStackTraceWithMessage(tag: String?, message: String?, e: Exception?) {}
        override fun logStackTrace(tag: String?, e: Exception?) {}
    }

    val viewClient = object : TerminalViewClient {
        override fun onScale(scale: Float): Float = 1f
        override fun onSingleTapUp(e: MotionEvent?) {
            val v = view ?: return
            v.requestFocus()
            (v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(v, 0)
        }
        override fun shouldBackButtonBeMappedToEscape(): Boolean = false
        override fun shouldEnforceCharBasedInput(): Boolean = true
        override fun shouldUseCtrlSpaceWorkaround(): Boolean = false
        override fun isTerminalViewSelected(): Boolean = true
        override fun copyModeChanged(copyMode: Boolean) {}
        override fun onKeyDown(keyCode: Int, e: KeyEvent?, session: TerminalSession?): Boolean = false
        override fun onKeyUp(keyCode: Int, e: KeyEvent?): Boolean = false
        override fun onLongPress(event: MotionEvent?): Boolean = false
        override fun readControlKey(): Boolean = ctrl
        override fun readAltKey(): Boolean = false
        override fun readShiftKey(): Boolean = false
        override fun readFnKey(): Boolean = false
        override fun onCodePoint(codePoint: Int, ctrlDown: Boolean, session: TerminalSession?): Boolean { ctrl = false; return false }
        override fun onEmulatorSet() {}
        override fun logError(tag: String?, message: String?) {}
        override fun logWarn(tag: String?, message: String?) {}
        override fun logInfo(tag: String?, message: String?) {}
        override fun logDebug(tag: String?, message: String?) {}
        override fun logVerbose(tag: String?, message: String?) {}
        override fun logStackTraceWithMessage(tag: String?, message: String?, e: Exception?) {}
        override fun logStackTrace(tag: String?, e: Exception?) {}
    }
}

@Composable
fun LinuxScreen(store: FileStore) {
    val ctx = LocalContext.current
    if (!Linux.installed(ctx)) {
        Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Linux ещё не установлен", color = Color(0xFFE9D5FF), fontSize = 18.sp)
            Spacer(Modifier.height(10.dp))
            Text("Открой вкладку «Терминал» и выполни:\nlinux install\nlinux setup", color = Color(0xFF8B7FA8), fontSize = 14.sp)
        }
        return
    }
    Column(Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            factory = { c ->
                val v = TerminalView(c, null)
                v.setTerminalViewClient(LinuxTerm.viewClient)
                v.setTextSize((13 * c.resources.displayMetrics.scaledDensity).toInt())
                LinuxTerm.view = v
                if (LinuxTerm.session == null) {
                    val (args, env) = Linux.shell(c, store)
                    LinuxTerm.finished = false
                    LinuxTerm.session = TerminalSession(args[0], store.root.absolutePath, args, env, 3000, LinuxTerm.sessionClient)
                }
                v.attachSession(LinuxTerm.session)
                v.requestFocus()
                v
            },
            update = { LinuxTerm.view = it }
        )
        if (LinuxTerm.finished) {
            Row(Modifier.fillMaxWidth().padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Сессия завершена", color = Color(0xFF8B7FA8), modifier = Modifier.weight(1f))
                TextButton(onClick = {
                    val (args, env) = Linux.shell(ctx, store)
                    LinuxTerm.finished = false
                    val s = TerminalSession(args[0], store.root.absolutePath, args, env, 3000, LinuxTerm.sessionClient)
                    LinuxTerm.session = s
                    LinuxTerm.view?.attachSession(s)
                }) { Text("Новая сессия") }
            }
        }
        Row(Modifier.fillMaxWidth().background(Color(0xFF240B0B)).horizontalScroll(rememberScrollState()).padding(horizontal = 4.dp)) {
            fun key(label: String, send: String) = @Composable {
                TextButton(onClick = { LinuxTerm.session?.write(send) }, contentPadding = PaddingValues(horizontal = 10.dp)) { Text(label, fontSize = 14.sp) }
            }
            TextButton(onClick = { LinuxTerm.ctrl = !LinuxTerm.ctrl }, contentPadding = PaddingValues(horizontal = 10.dp)) {
                Text("Ctrl", fontSize = 14.sp, color = if (LinuxTerm.ctrl) Color(0xFFF472B6) else Color.Unspecified)
            }
            key("Esc", "\u001b")()
            key("Tab", "\t")()
            key("↑", "\u001b[A")()
            key("↓", "\u001b[B")()
            key("←", "\u001b[D")()
            key("→", "\u001b[C")()
            key("^C", "\u0003")()
            key("^D", "\u0004")()
            key("^X", "\u0018")()
            key("^O", "\u000f")()
            key("/", "/")()
            key("-", "-")()
            key("|", "|")()
            key("~", "~")()
            key("PgUp", "\u001b[5~")()
            key("PgDn", "\u001b[6~")()
        }
    }
}
