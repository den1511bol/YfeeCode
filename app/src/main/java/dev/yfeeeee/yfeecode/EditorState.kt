// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.io.File

class EditorState(val file: File, initial: String) {
    var text by mutableStateOf(initial)
        private set
    var cursor by mutableStateOf(0)
        private set
    var dirty by mutableStateOf(false)
        private set
    var autoFix by mutableStateOf(true)
    val lang = Lang.fromName(file.name)

    var anchor by mutableStateOf<Int?>(null)
        private set
    var selectMode by mutableStateOf(false)
        private set
    val hasSel: Boolean get() = anchor != null && anchor != cursor
    val selStart: Int get() = minOf(anchor ?: cursor, cursor)
    val selEnd: Int get() = maxOf(anchor ?: cursor, cursor)
    val selectedText: String get() = if (hasSel) text.substring(selStart, selEnd) else ""

    fun toggleSelect() {
        selectMode = !selectMode
        anchor = if (selectMode) cursor else null
    }
    fun selectAll() { selectMode = true; anchor = 0; cursor = text.length }
    private fun afterMove() { if (!selectMode) anchor = null }
    fun clearSel() { anchor = null; selectMode = false }

    fun deleteSel() {
        if (!hasSel) return
        snapshot()
        val a = selStart
        set(text.removeRange(a, selEnd), a)
        anchor = null; selectMode = false
    }

    fun selectWordAt(off: Int) {
        val o = off.coerceIn(0, text.length)
        var a = o
        var b = o
        while (a > 0 && (text[a - 1].isLetterOrDigit() || text[a - 1] == '_')) a--
        while (b < text.length && (text[b].isLetterOrDigit() || text[b] == '_')) b++
        if (a == b) return
        selectMode = false
        anchor = a
        cursor = b
    }

    fun cutSel(): String { val t = selectedText; deleteSel(); return t }

    // line helpers
    fun toggleComment() {
        val prefix = when (lang) { Lang.PYTHON -> "# "; Lang.CSS -> return; Lang.HTML -> return; else -> "// " }
        val a = if (hasSel) selStart else cursor
        val b = if (hasSel) selEnd else cursor
        val ls = text.lastIndexOf('\n', a - 1) + 1
        val le = text.indexOf('\n', b).let { if (it < 0) text.length else it }
        val lines = text.substring(ls, le).split("\n")
        val all = lines.all { it.isBlank() || it.trimStart().startsWith(prefix.trim()) }
        val out = lines.joinToString("\n") { l ->
            if (l.isBlank()) l
            else if (all) { val i = l.indexOf(prefix.trim()); l.removeRange(i, i + prefix.trim().length + (if (l.startsWith(" ", i + prefix.trim().length)) 1 else 0)) }
            else { val n = l.length - l.trimStart().length; l.substring(0, n) + prefix + l.substring(n) }
        }
        snapshot(); set(text.substring(0, ls) + out + text.substring(le), cursor); anchor = null
    }

    fun duplicateLine() {
        val ls = text.lastIndexOf('\n', cursor - 1) + 1
        val le = text.indexOf('\n', cursor).let { if (it < 0) text.length else it }
        val line = text.substring(ls, le)
        snapshot(); set(text.substring(0, le) + "\n" + line + text.substring(le), cursor + line.length + 1)
    }

    fun goToLine(n: Int) {
        val lines = text.split("\n")
        val k = (n - 1).coerceIn(0, lines.size - 1)
        cursor = lines.take(k).sumOf { it.length + 1 }; afterMove()
    }

    // search / replace
    fun findNext(q: String, from: Int = cursor, ignoreCase: Boolean = true): Boolean {
        if (q.isEmpty()) return false
        var i = text.indexOf(q, from, ignoreCase)
        if (i < 0) i = text.indexOf(q, 0, ignoreCase)
        if (i < 0) return false
        anchor = i; cursor = i + q.length; selectMode = false
        return true
    }
    fun replaceCurrent(q: String, r: String) {
        if (hasSel && selectedText.equals(q, true)) {
            snapshot(); val a = selStart
            set(text.substring(0, a) + r + text.substring(selEnd), a + r.length); anchor = null
        }
        findNext(q)
    }
    fun replaceAllText(q: String, r: String): Int {
        if (q.isEmpty()) return 0
        val n = Regex(Regex.escape(q), RegexOption.IGNORE_CASE).findAll(text).count()
        if (n > 0) { snapshot(); set(Regex(Regex.escape(q), RegexOption.IGNORE_CASE).replace(text) { r }, cursor) }
        return n
    }

    private val undo = ArrayDeque<Pair<String, Int>>()
    private val redo = ArrayDeque<Pair<String, Int>>()

    val lineIndex: Int get() = text.substring(0, cursor).count { it == '\n' }

    private fun snapshot() {
        undo.addLast(text to cursor)
        if (undo.size > 200) undo.removeFirst()
        redo.clear()
    }

    private fun set(t: String, c: Int) { text = t; cursor = c.coerceIn(0, t.length); dirty = true }

    fun moveTo(c: Int) { cursor = c.coerceIn(0, text.length); afterMove() }

    fun markSaved() { dirty = false }

    fun replaceAll(t: String) { snapshot(); set(t, minOf(cursor, t.length)) }

    fun insert(s: String) {
        if (hasSel) deleteSel()
        snapshot()
        // paired characters typed from the keyboard row, e.g. "()" -> place cursor in the middle
        if (s.length == 2 && AutoFix.pairs[s[0]] == s[1]) {
            set(text.substring(0, cursor) + s + text.substring(cursor), cursor + 1)
            return
        }
        if (autoFix && s.length == 1 && s[0] in "([{\"'") {
            val c = s[0]
            val close = AutoFix.pairs[c]!!
            val next = text.getOrNull(cursor)
            val prev = text.getOrNull(cursor - 1)
            val nextOk = next == null || next.isWhitespace() || next in ")]}"
            val isQuote = c == '"' || c == '\''
            val prevWord = prev != null && (prev.isLetterOrDigit() || prev == '_')
            val stepOver = isQuote && next == c
            if (nextOk && !stepOver && !(isQuote && prevWord)) {
                set(text.substring(0, cursor) + c + close + text.substring(cursor), cursor + 1)
                return
            }
        }
        if (autoFix && s == " ") {
            val before = text.getOrNull(cursor - 1)
            // phone keyboards like to double a space: ignore second space inside a line
            val lineStart = text.lastIndexOf('\n', cursor - 1) + 1
            val onlyIndentBefore = text.substring(lineStart, cursor).isBlank()
            if (before == ' ' && !onlyIndentBefore) { undo.removeLastOrNull(); return }
        }
        if (autoFix && s.length == 1 && s[0] in ")]}\"'" && text.getOrNull(cursor) == s[0]) {
            undo.removeLastOrNull(); cursor += 1; return // step over auto-closed char
        }
        set(text.substring(0, cursor) + s + text.substring(cursor), cursor + s.length)
    }

    fun backspace() {
        if (hasSel) { deleteSel(); return }
        if (cursor == 0) return
        snapshot()
        val prev = text[cursor - 1]
        val next = text.getOrNull(cursor)
        // delete an empty pair together: (|) -> |
        if (next != null && AutoFix.pairs[prev] == next) {
            set(text.removeRange(cursor - 1, cursor + 1), cursor - 1); return
        }
        set(text.removeRange(cursor - 1, cursor), cursor - 1)
    }

    fun enter() {
        if (hasSel) deleteSel()
        snapshot()
        val lineStart = text.lastIndexOf('\n', cursor - 1) + 1
        var prevLine = text.substring(lineStart, cursor)
        if (autoFix) {
            val cleaned = AutoFix.cleanLine(prevLine)
            if (cleaned != prevLine) {
                text = text.substring(0, lineStart) + cleaned + text.substring(cursor)
                cursor = lineStart + cleaned.length
                prevLine = cleaned
            }
        }
        val indent = if (autoFix) AutoFix.nextIndent(prevLine, lang) else ""
        val nextCh = text.getOrNull(cursor)
        // between braces: { | } -> open a block on its own line
        if (nextCh != null && nextCh in "}])" && prevLine.trimEnd().lastOrNull()?.let { it in "{[(" } == true) {
            val base = indent.dropLast(4)
            val ins = "\n$indent\n$base"
            set(text.substring(0, cursor) + ins + text.substring(cursor), cursor + 1 + indent.length)
        } else {
            set(text.substring(0, cursor) + "\n" + indent + text.substring(cursor), cursor + 1 + indent.length)
        }
    }

    fun tab() = insert("    ")

    /** Replaces the word before the cursor; [cursorBack] = how many chars before the end to put the cursor. */
    fun complete(s: Suggestion) {
        var i = cursor
        while (i > 0 && (text[i - 1].isLetterOrDigit() || text[i - 1] == '_')) i--
        snapshot()
        val nt = text.substring(0, i) + s.insert + text.substring(cursor)
        set(nt, i + s.insert.length - s.cursorBack)
    }

    fun left() { if (cursor > 0) cursor -= 1; afterMove() }
    fun right() { if (cursor < text.length) cursor += 1; afterMove() }

    fun home() { cursor = text.lastIndexOf('\n', cursor - 1) + 1; afterMove() }
    fun end() { val e = text.indexOf('\n', cursor); cursor = if (e < 0) text.length else e; afterMove() }

    fun up() { vertical(-1); afterMove() }
    fun down() { vertical(1); afterMove() }

    private fun vertical(d: Int) {
        val lineStart = text.lastIndexOf('\n', cursor - 1) + 1
        val col = cursor - lineStart
        if (d < 0) {
            if (lineStart == 0) { cursor = 0; return }
            val pStart = text.lastIndexOf('\n', lineStart - 2) + 1
            cursor = minOf(pStart + col, lineStart - 1)
        } else {
            val e = text.indexOf('\n', cursor)
            if (e < 0) { cursor = text.length; return }
            val nEnd = text.indexOf('\n', e + 1).let { if (it < 0) text.length else it }
            cursor = minOf(e + 1 + col, nEnd)
        }
    }

    fun undo() {
        val s = undo.removeLastOrNull() ?: return
        redo.addLast(text to cursor); text = s.first; cursor = s.second; dirty = true
    }

    fun redo() {
        val s = redo.removeLastOrNull() ?: return
        undo.addLast(text to cursor); text = s.first; cursor = s.second; dirty = true
    }
}
