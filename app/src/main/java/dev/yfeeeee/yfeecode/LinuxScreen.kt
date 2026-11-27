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
    val sessions = mutableStateListOf<TerminalSession>()
    var cur by mutableStateOf(0)
    val session: TerminalSession? get() = sessions.getOrNull(cur)
    var view: TerminalView? = null
    var ctrl by mutableStateOf(false)
    var alt by mutableStateOf(false)
    /** closing chars waiting after auto-paired brackets (auto-close in the Linux tab). Developer: @yfeeeee */
    var pending = StringBuilder()
    var titleTick by mutableStateOf(0)
    var fontSp = 13

    fun setFont(c: Context, sp: Int) {
        fontSp = sp.coerceIn(8, 28)
        try { c.getSharedPreferences("session", Context.MODE_PRIVATE).edit().putInt("lx_font", fontSp).apply() } catch (_: Exception) {}
        view?.setTextSize((fontSp * c.resources.displayMetrics.scaledDensity).toInt())
        view?.onScreenUpdated()
    }
    val finished: Boolean get() = sessions.isEmpty()

    fun newSession(c: Context, store: FileStore) {
        val (args, env) = Linux.shell(c, store)
        val s = TerminalSession(args[0], store.root.absolutePath, args, env, 3000, sessionClient)
        sessions.add(s); cur = sessions.size - 1
        LinuxService.begin(c)
        view?.attachSession(s)
    }

    fun select(i: Int) {
        if (i !in sessions.indices) return
        cur = i
        view?.attachSession(sessions[i])
        view?.onScreenUpdated()
    }

    val sessionClient = object : TerminalSessionClient {
        override fun onTextChanged(changedSession: TerminalSession) { view?.onScreenUpdated() }
        override fun onTitleChanged(changedSession: TerminalSession) { titleTick++ }
        override fun onSessionFinished(finishedSession: TerminalSession) {
            val i = sessions.indexOf(finishedSession)
            if (i >= 0) {
                sessions.removeAt(i)
                cur = cur.coerceAtMost(sessions.size - 1).coerceAtLeast(0)
                sessions.getOrNull(cur)?.let { view?.attachSession(it) }
            }
            view?.context?.let { LinuxService.end(it) }
        }
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
            // typing goes through the in-app keyboard (DevKeyboard), the system keyboard is never opened
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
                LinuxTerm.fontSp = try { c.getSharedPreferences("session", Context.MODE_PRIVATE).getInt("lx_font", 13) } catch (_: Exception) { 13 }
                v.setTextSize((LinuxTerm.fontSp * c.resources.displayMetrics.scaledDensity).toInt())
                LinuxTerm.view = v
                if (LinuxTerm.sessions.isEmpty()) LinuxTerm.newSession(c, store)
                v.isFocusable = false
                v.isFocusableInTouchMode = false
                LinuxTerm.session?.let { v.attachSession(it) }
                v
            },
            update = { LinuxTerm.view = it }
        )
        val kbOn = remember { mutableStateOf(true) }
        Row(Modifier.fillMaxWidth().background(Color(0xFF1A0707)).horizontalScroll(rememberScrollState()).padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            LinuxTerm.sessions.forEachIndexed { i, sess ->
                TextButton(onClick = { LinuxTerm.select(i) }, contentPadding = PaddingValues(horizontal = 12.dp)) {
                    Text(if (LinuxTerm.titleTick >= 0 && !sess.title.isNullOrBlank()) "${i + 1} ${sess.title.take(12)}" else "${i + 1}", fontSize = 14.sp, color = if (i == LinuxTerm.cur) Color(0xFFEF4444) else Color(0xFF8B7FA8))
                }
            }
            TextButton(onClick = { kbOn.value = !kbOn.value }, contentPadding = PaddingValues(horizontal = 12.dp)) { Text(if (kbOn.value) "⌨↓" else "⌨↑", fontSize = 14.sp) }
            fun key(label: String, send: String) = @Composable {
                TextButton(onClick = { LinuxTerm.session?.write(send) }, contentPadding = PaddingValues(horizontal = 10.dp)) { Text(label, fontSize = 14.sp) }
            }
            TextButton(onClick = { LinuxTerm.ctrl = !LinuxTerm.ctrl }, contentPadding = PaddingValues(horizontal = 10.dp)) {
                Text("Ctrl", fontSize = 14.sp, color = if (LinuxTerm.ctrl) Color(0xFFF472B6) else Color.Unspecified)
            }
            TextButton(onClick = { LinuxTerm.alt = !LinuxTerm.alt }, contentPadding = PaddingValues(horizontal = 10.dp)) {
                Text("Alt", fontSize = 14.sp, color = if (LinuxTerm.alt) Color(0xFFF472B6) else Color.Unspecified)
            }
            key("Esc", "\u001b")()
            key("^C", "\u0003")()
            key("^D", "\u0004")()
            key("^X", "\u0018")()
            key("^O", "\u000f")()
            key("^Z", "\u001a")()
            key("^L", "\u000c")()
            key("^R", "\u0012")()
            key("^A", "\u0001")()
            key("^E", "\u0005")()
            key("^W", "\u0017")()
            key("^U", "\u0015")()
            key("/", "/")()
            key("-", "-")()
            key("|", "|")()
            key("~", "~")()
            key("PgUp", "\u001b[5~")()
            key("PgDn", "\u001b[6~")()
            key("Del", "\u001b[3~")()
            key("Ins", "\u001b[2~")()
            key("\\", "\\")()
            key("&", "&")()
            key("*", "*")()
            key("F1", "\u001bOP")()
            key("F2", "\u001bOQ")()
            key("F3", "\u001bOR")()
            key("F4", "\u001bOS")()
            key("F5", "\u001b[15~")()
            key("F6", "\u001b[17~")()
            key("F7", "\u001b[18~")()
            key("F8", "\u001b[19~")()
            key("F9", "\u001b[20~")()
            key("F10", "\u001b[21~")()
            key("F11", "\u001b[23~")()
            key("F12", "\u001b[24~")()

            TextButton(onClick = { LinuxTerm.setFont(ctx, LinuxTerm.fontSp - 1) }, contentPadding = PaddingValues(horizontal = 12.dp)) { Text("A−", fontSize = 14.sp) }
            TextButton(onClick = { LinuxTerm.setFont(ctx, LinuxTerm.fontSp + 1) }, contentPadding = PaddingValues(horizontal = 12.dp)) { Text("A+", fontSize = 14.sp) }
            TextButton(onClick = { LinuxTerm.newSession(ctx, store) }, contentPadding = PaddingValues(horizontal = 12.dp)) { Text("+ сессия", fontSize = 14.sp) }
            if (LinuxTerm.sessions.isNotEmpty()) TextButton(onClick = { LinuxTerm.session?.finishIfRunning() }, contentPadding = PaddingValues(horizontal = 12.dp)) { Text("закрыть", fontSize = 14.sp) }
            TextButton(onClick = {
                try {
                    val tx = LinuxTerm.session?.emulator?.screen?.transcriptText ?: ""
                    val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    cm.setPrimaryClip(android.content.ClipData.newPlainText("linux", tx.trimEnd().takeLast(20000)))
                } catch (_: Exception) {}
            }, contentPadding = PaddingValues(horizontal = 12.dp)) { Text("Копир. вывод", fontSize = 14.sp) }
            TextButton(onClick = {
                try {
                    val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    val out = java.io.File(store.root, ".yfee_clip")
                    if (out.exists()) { cm.setPrimaryClip(android.content.ClipData.newPlainText("linux", out.readText())); out.delete() }
                    else cm.primaryClip?.getItemAt(0)?.text?.toString()?.let { java.io.File(store.root, ".yfee_clipin").writeText(it) }
                } catch (_: Exception) {}
            }, contentPadding = PaddingValues(horizontal = 12.dp)) { Text("Буфер", fontSize = 14.sp) }
            TextButton(onClick = {
                try {
                    ctx.startActivity(android.content.Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, android.net.Uri.parse("package:" + ctx.packageName)).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK))
                } catch (_: Exception) {}
            }, contentPadding = PaddingValues(horizontal = 12.dp)) { Text("Файлы", fontSize = 14.sp) }
        }
        if (kbOn.value) {
        DevKeyboard(
            onText = { t ->
                val sess = LinuxTerm.session
                if (sess != null) {
                    if (LinuxTerm.ctrl && t.length == 1 && t[0].lowercaseChar() in 'a'..'z') {
                        sess.write(String(charArrayOf((t[0].lowercaseChar().code and 0x1f).toChar())))
                        LinuxTerm.ctrl = false
                    } else if (LinuxTerm.alt) { sess.write("\u001b" + t); LinuxTerm.alt = false }
                    else if (t.length == 1 && t[0] in "([{\"" && !LinuxTerm.ctrl) {
                        val close = when (t[0]) { '(' -> ')'; '[' -> ']'; '{' -> '}'; else -> '"' }
                        if (t[0] == '"' && LinuxTerm.pending.lastOrNull() == '"') { sess.write("\u001b[C"); LinuxTerm.pending.setLength(LinuxTerm.pending.length - 1) }
                        else { sess.write(t + close + "\u001b[D"); LinuxTerm.pending.append(close) }
                    }
                    else if (t.length == 1 && t[0] in ")]}" && LinuxTerm.pending.lastOrNull() == t[0]) {
                        sess.write("\u001b[C"); LinuxTerm.pending.setLength(LinuxTerm.pending.length - 1)
                    }
                    else sess.write(t)
                }
            },
            onCmd = { c ->
                val sess = LinuxTerm.session
                if (c != Cmd.LEFT && c != Cmd.RIGHT) LinuxTerm.pending.setLength(0)
                if (sess != null) when (c) {
                    Cmd.BACK -> sess.write("\u007f")
                    Cmd.ENTER -> sess.write("\r")
                    Cmd.TAB -> sess.write("\t")
                    Cmd.LEFT -> sess.write("\u001b[D")
                    Cmd.RIGHT -> sess.write("\u001b[C")
                    Cmd.UP -> sess.write("\u001b[A")
                    Cmd.DOWN -> sess.write("\u001b[B")
                    Cmd.HOME -> sess.write("\u001b[H")
                    Cmd.END -> sess.write("\u001b[F")
                    Cmd.DELWORD -> sess.write("\u0017")
                    Cmd.PASTE -> {
                        val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        cm.primaryClip?.getItemAt(0)?.text?.toString()?.let { sess.write(it) }
                    }
                }
            }
        )
        }
    }
}
