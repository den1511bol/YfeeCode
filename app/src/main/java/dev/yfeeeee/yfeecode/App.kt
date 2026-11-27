// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import com.chaquo.python.Python
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    store: FileStore,
    term: TerminalState,
    refreshKey: Int,
    onImport: (File) -> Unit,
    onExport: (File) -> Unit,
    onZipExport: () -> Unit = {},
    onZipImport: (File) -> Unit = {}
) {
    val ctx = LocalContext.current
    val sp = remember { ctx.getSharedPreferences("session", android.content.Context.MODE_PRIVATE) }
    var tab by remember { mutableIntStateOf(sp.getInt("tab", 0).coerceIn(0, 4)) }
    LaunchedEffect(tab) { sp.edit().putInt("tab", tab).apply() }
    var showSettings by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }
    val open = remember {
        mutableStateListOf<EditorState>().apply {
            (sp.getString("tabs", "") ?: "").split("\n").filter { it.isNotBlank() }.map { File(it) }
                .filter { it.isFile }.forEach { f -> add(EditorState(f, store.read(f)).also { it.moveTo(sp.getInt("cur_" + f.absolutePath, 0)) }) }
        }
    }
    var active by remember { mutableIntStateOf(sp.getInt("active", 0).coerceIn(0, maxOf(0, open.size - 1))) }
    LaunchedEffect(open.size, active) {
        sp.edit().putString("tabs", open.joinToString("\n") { it.file.absolutePath }).putInt("active", active).apply()
    }
    var dir by remember {
        mutableStateOf(File(sp.getString("dir", "") ?: "").takeIf {
            it.isDirectory && it.absolutePath.startsWith(store.root.absolutePath)
        } ?: store.root)
    }
    LaunchedEffect(dir) { sp.edit().putString("dir", dir.absolutePath).apply() }
    // Flush unsaved text and cursor positions whenever the app goes to background
    DisposableEffect(Unit) {
        val lc = (ctx as? androidx.activity.ComponentActivity)?.lifecycle
        val obs = androidx.lifecycle.LifecycleEventObserver { _, ev ->
            if (ev == androidx.lifecycle.Lifecycle.Event.ON_STOP) {
                val e = sp.edit()
                open.forEach { st ->
                    if (st.dirty && st.file.exists()) { store.write(st.file, st.text); st.markSaved() }
                    e.putInt("cur_" + st.file.absolutePath, st.cursor)
                }
                e.apply()
            }
        }
        lc?.addObserver(obs)
        onDispose { lc?.removeObserver(obs) }
    }
    LaunchedEffect(Unit) { Updater.autoCheck(ctx) }
    var dismissedUpdate by remember { mutableStateOf(false) }
    if (Updater.available && !dismissedUpdate && !showSettings) AlertDialog(
        onDismissRequest = { dismissedUpdate = true },
        title = { Text("Доступно обновление") },
        text = { Text(if (Updater.ready) "Новая версия 𝒴𝒻𝑒𝑒𝒞𝑜𝒹𝑒 уже скачана. Файлы и настройки сохранятся." else "Вышла новая версия 𝒴𝒻𝑒𝑒𝒞𝑜𝒹𝑒. Файлы и настройки сохранятся.") },
        confirmButton = { TextButton(onClick = { dismissedUpdate = true; if (!Updater.ready) showSettings = true; Updater.downloadAndInstall(ctx) }) { Text(if (Updater.ready) "Установить" else "Обновить") } },
        dismissButton = { TextButton(onClick = { dismissedUpdate = true }) { Text("Позже") } }
    )

    Column(Modifier.fillMaxSize().diamondPattern().statusBarsPadding().navigationBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().background(Color(0xFF240B0B)).padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "𝒴𝒻𝑒𝑒𝒞𝑜𝒹𝑒",
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                letterSpacing = 1.5.sp,
                style = androidx.compose.ui.text.TextStyle(
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(Color(0xFFEF4444), Color(0xFFF48972))
                    )
                )
            )
            Spacer(Modifier.weight(1f))
            Text("by @yfeeeee", color = Color(0xFFEF4444), fontSize = 12.sp)
            IconButton(onClick = { showHelp = true }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Info, "Инструкция", tint = Color(0xFFEF4444))
            }
            IconButton(onClick = { showSettings = true }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Settings, "Настройки", tint = Color(0xFFEF4444))
            }
        }
        Box(Modifier.fillMaxWidth().height(2.dp).background(
            androidx.compose.ui.graphics.Brush.horizontalGradient(
                listOf(Color(0x00B91C1C), Color(0xFFEF4444), Color(0xFFF48972), Color(0x00F48972)))))
        Box(Modifier.weight(1f)) {
            when (tab) {
                0 -> FilesScreen(store, dir, refreshKey, { dir = it }, onImport, onExport, onZipExport, onZipImport,
                    onGone = { gone ->
                        val prefix = gone.absolutePath
                        val dead = open.filter { it.file.absolutePath == prefix || it.file.absolutePath.startsWith(prefix + File.separator) }
                        dead.forEach { it.markSaved() }
                        open.removeAll(dead.toSet())
                        active = active.coerceIn(0, maxOf(0, open.size - 1))
                    }) { f, line ->
                    val i = open.indexOfFirst { it.file == f }
                    if (i >= 0) active = i else { open.add(EditorState(f, store.read(f))); active = open.size - 1 }
                    if (line != null) open[active].goToLine(line)
                    tab = 1
                }
                1 -> EditorScreen(
                    open, active, store, term, onExport,
                    onSelect = { active = it },
                    onClose = { i ->
                        val e = open[i]
                        if (e.dirty) store.write(e.file, e.text)
                        open.removeAt(i); active = active.coerceIn(0, maxOf(0, open.size - 1))
                    },
                    goTerminal = { tab = 2 }
                )
                2 -> TerminalScreen(term, open.getOrNull(active)?.file, dir) { p, line ->
                    val f = File(p).takeIf { it.isFile } ?: store.root.walkTopDown().firstOrNull { it.isFile && it.name == File(p).name }
                    if (f != null) {
                        val i = open.indexOfFirst { it.file == f }
                        if (i >= 0) active = i else { open.add(EditorState(f, store.read(f))); active = open.size - 1 }
                        open[active].goToLine(line)
                        tab = 1
                    }
                }
                4 -> LinuxScreen(store)
                else -> LearnScreen()
            }
        }
        if (showHelp) HelpDialog { showHelp = false }
        if (showSettings) AlertDialog(
            onDismissRequest = { showSettings = false; Settings.save(ctx) },
            confirmButton = { TextButton(onClick = { showSettings = false; Settings.save(ctx) }) { Text("Готово") } },
            title = { Text("Настройки") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Размер шрифта: ${Settings.fontSize}", modifier = Modifier.weight(1f))
                        TextButton(onClick = { Settings.fontSize = (Settings.fontSize - 1).coerceAtLeast(10) }) { Text("−") }
                        TextButton(onClick = { Settings.fontSize = (Settings.fontSize + 1).coerceAtMost(24) }) { Text("+") }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Пульсация узоров", modifier = Modifier.weight(1f))
                        Switch(checked = Settings.pulse, onCheckedChange = { Settings.pulse = it })
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Автопробелы вокруг = + - ,", modifier = Modifier.weight(1f))
                        Switch(checked = Settings.autoSpace, onCheckedChange = { Settings.autoSpace = it })
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Автопроверка обновлений", modifier = Modifier.weight(1f))
                        Switch(checked = Settings.autoUpdate, onCheckedChange = { Settings.autoUpdate = it })
                    }
                    Text("Версия: ${Updater.currentLabel}", color = Color(0xFFA87F81), fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
                    if (Updater.status.isNotEmpty()) Text(Updater.status, color = Color(0xFFEF4444), fontSize = 12.sp)
                    if (Updater.progress >= 0) LinearProgressIndicator(progress = { Updater.progress / 100f }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                    Row {
                        TextButton(onClick = { Updater.check(ctx) }, enabled = !Updater.busy) { Text("Проверить") }
                        if (Updater.available) TextButton(onClick = { Updater.downloadAndInstall(ctx) }, enabled = !Updater.busy) { Text("Обновить") }
                    }
                    Text("Цвет акцента", modifier = Modifier.padding(top = 10.dp, bottom = 6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Settings.accents.forEachIndexed { i, c ->
                            Box(
                                Modifier.size(34.dp)
                                    .background(Color(c), androidx.compose.foundation.shape.CircleShape)
                                    .border(if (Settings.accent == i) 3.dp else 0.dp, Color.White, androidx.compose.foundation.shape.CircleShape)
                                    .clickable { Settings.accent = i }
                            )
                        }
                    }
                }
            }
        )
        NavigationBar(containerColor = Color(0xFF240B0B)) {
            listOf("Файлы" to Icons.Default.Folder, "Редактор" to Icons.Default.Code, "Терминал" to Icons.Default.Terminal, "Уроки" to Icons.Default.School, "Linux" to Icons.Default.Memory)
                .forEachIndexed { i, (label, icon) ->
                    NavigationBarItem(selected = tab == i, onClick = { tab = i },
                        icon = { Icon(icon, label) }, label = { Text(label) })
                }
        }
    }
}

@Composable
fun FilesScreen(
    store: FileStore, dir: File, refreshKey: Int, onDir: (File) -> Unit,
    onImport: (File) -> Unit, onExport: (File) -> Unit,
    onZipExport: () -> Unit, onZipImport: (File) -> Unit, onGone: (File) -> Unit, onOpen: (File, Int?) -> Unit
) {
    var tick by remember { mutableIntStateOf(0) }
    var dialog by remember { mutableStateOf<String?>(null) } // "file" | "dir"
    var renaming by remember { mutableStateOf<File?>(null) }
    var deleting by remember { mutableStateOf<File?>(null) }
    val items = remember(dir, tick, refreshKey) { store.list(dir) }
    val atRoot = dir.canonicalPath == store.root.canonicalPath

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            TextButton(onClick = { dialog = "file" }) { Text("+ Файл") }
            TextButton(onClick = { dialog = "dir" }) { Text("+ Папка") }
            TextButton(onClick = { dialog = "tpl" }) { Text("Шаблон") }
            TextButton(onClick = { dialog = "search" }) { Text("Поиск") }
            TextButton(onClick = { onImport(dir) }) { Text("Импорт") }
            TextButton(onClick = { onZipImport(dir) }) { Text("ZIP↓") }
            TextButton(onClick = onZipExport) { Text("ZIP↑") }
        }
        Text(
            "~/" + dir.relativeToOrSelf(store.root).path, color = Color(0xFFA87F81),
            fontFamily = FontFamily.Monospace, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp)
        )
        deleting?.let { d ->
            val n = if (d.isDirectory) d.walkTopDown().count { it.isFile } else 0
            AlertDialog(
                onDismissRequest = { deleting = null },
                title = { Text(if (d.isDirectory) "Удалить папку?" else "Удалить файл?") },
                text = { Text("«" + d.name + "»" + (if (d.isDirectory) " и все файлы внутри ($n шт.)" else "") + "\n\nЭто нельзя отменить. Сначала можно сделать ZIP-экспорт проекта.") },
                confirmButton = { TextButton(onClick = { onGone(d); store.delete(d); tick++; deleting = null }) { Text("Удалить", color = Color(0xFFF48972)) } },
                dismissButton = { TextButton(onClick = { deleting = null }) { Text("Отмена") } }
            )
        }
        LazyColumn(Modifier.weight(1f)) {
            if (!atRoot) item {
                FileRow("..", true, {}, {}) { onDir(dir.parentFile ?: store.root) }
            }
            items(items) { f ->
                FileRow(f.name, f.isDirectory, onRename = { renaming = f },
                    onDelete = { deleting = f },
                    onDuplicate = if (f.isFile) ({ store.duplicate(f); tick++ }) else null) {
                    if (f.isDirectory) onDir(f) else onOpen(f, null)
                }
            }
        }
        Text(
            "Совет: файлы лежат внутри приложения. «Импорт» копирует файл с телефона сюда, " +
                "экспорт — кнопка в редакторе.",
            color = Color(0xFFA87F81), fontSize = 11.sp, modifier = Modifier.padding(10.dp)
        )
    }

    renaming?.let { target ->
        NameDialog(
            title = "Новое имя", initial = target.name, onCancel = { renaming = null },
            onOk = { name ->
                val clean = name.trim().replace('\\', '/').substringAfterLast('/').trim()
                val dest = File(target.parentFile, clean)
                if (clean.isNotEmpty() && clean != ".." && clean != "." && !dest.exists()) { onGone(target); target.renameTo(dest) }
                renaming = null; tick++
            }
        )
    }
    var chosen by remember { mutableStateOf<Template?>(null) }
    var hits by remember { mutableStateOf<List<FileStore.Hit>?>(null) }
    if (dialog == "search") {
        NameDialog(
            title = "Найти во всех файлах", onCancel = { dialog = null },
            onOk = { q -> hits = store.searchAll(q); dialog = null }
        )
    }
    hits?.let { list ->
        AlertDialog(
            onDismissRequest = { hits = null },
            confirmButton = { TextButton(onClick = { hits = null }) { Text("Закрыть") } },
            title = { Text("Найдено: ${list.size}") },
            text = {
                LazyColumn {
                    items(list) { h ->
                        Text(
                            h.file.name + ":" + h.line + "  " + h.text, fontSize = 12.sp, fontFamily = FontFamily.Monospace,
                            modifier = Modifier.fillMaxWidth().clickable { hits = null; onOpen(h.file, h.line) }.padding(vertical = 6.dp)
                        )
                    }
                }
            }
        )
    }
    if (dialog == "tpl") {
        AlertDialog(
            onDismissRequest = { dialog = null },
            confirmButton = {},
            title = { Text("Новый файл из шаблона") },
            text = {
                Column {
                    Templates.all.forEach { t ->
                        TextButton(onClick = { chosen = t; dialog = "tplname" }) { Text(t.title) }
                    }
                }
            }
        )
    }
    if (dialog == "tplname" && chosen != null) {
        NameDialog(
            title = "Имя файла", initial = chosen!!.fileName,
            onCancel = { dialog = null },
            onOk = { name ->
                val f = store.create(dir, name, chosen!!.body)
                dialog = null; tick++; onOpen(f, null)
            }
        )
    }
    dialog?.takeIf { it == "file" || it == "dir" }?.let { kind ->
        NameDialog(
            title = if (kind == "file") "Имя файла (например bot.py)" else "Имя папки",
            onCancel = { dialog = null },
            onOk = { name ->
                if (kind == "file") {
                    val f = store.create(dir, name, store.template(Lang.fromName(name)))
                    onOpen(f, null)
                } else store.mkdir(dir, name)
                dialog = null; tick++
            }
        )
    }
}

@Composable
private fun FileRow(name: String, isDir: Boolean, onRename: () -> Unit, onDelete: () -> Unit, onDuplicate: (() -> Unit)? = null, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(if (isDir) Icons.Default.Folder else Icons.Default.InsertDriveFile, null,
            tint = if (isDir) Color(0xFFE3B341) else Color(0xFF79C0FF))
        Spacer(Modifier.width(10.dp))
        Text(name, color = Color.White, modifier = Modifier.weight(1f))
        if (onDuplicate != null) IconButton(onClick = onDuplicate) { Icon(Icons.Default.ContentCopy, "Дублировать", tint = Color(0xFFA87F81)) }
        if (name != "..") IconButton(onClick = onRename) { Icon(Icons.Default.Edit, "Переименовать", tint = Color(0xFFA87F81)) }
        if (name != "..") IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Удалить", tint = Color(0xFFA87F81)) }
    }
}

@Composable
fun EditorScreen(
    open: List<EditorState>, active: Int, store: FileStore, term: TerminalState,
    onExport: (File) -> Unit, onSelect: (Int) -> Unit, onClose: (Int) -> Unit, goTerminal: () -> Unit
) {
    val state = open.getOrNull(active)
    if (state == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Открой файл во вкладке «Файлы»", color = Color(0xFFA87F81))
        }
        return
    }
    var showKb by remember { mutableStateOf(true) }
    var preview by remember { mutableStateOf(false) }
    var findOpen by remember { mutableStateOf(false) }
    var gotoOpen by remember { mutableStateOf(false) }
    var symOpen by remember { mutableStateOf(false) }
    var findQ by remember { mutableStateOf("") }
    var replQ by remember { mutableStateOf("") }
    var focusRepl by remember { mutableStateOf(false) }
    var caseSens by remember { mutableStateOf(false) }
    var info by remember { mutableStateOf("") }
    val clip = LocalClipboardManager.current
    val hints = remember(state.text, state.cursor) { Hints.analyze(state.text, state.lang, state.lineIndex) }
    val save = { store.write(state.file, state.text); state.markSaved() }
    var syntaxErr by remember(state) { mutableStateOf("") }
    val spE = LocalContext.current.getSharedPreferences("session", android.content.Context.MODE_PRIVATE)
    LaunchedEffect(state.text) {
        delay(1500) // autosave 1.5 s after the last edit
        if (state.dirty) { store.write(state.file, state.text); state.markSaved() }
        spE.edit().putInt("cur_" + state.file.absolutePath, state.cursor).apply()
    }

    LaunchedEffect(state.text) {
        if (state.lang == Lang.PYTHON) {
            delay(700)
            syntaxErr = withContext(Dispatchers.Default) {
                runCatching { Python.getInstance().getModule("syntax_check").callAttr("check", state.text).toString() }
                    .getOrDefault("")
            }
        } else syntaxErr = ""
    }
    LaunchedEffect(state.text) {
        if (state.dirty) { delay(1500); store.write(state.file, state.text); state.markSaved() }
    }
    DisposableEffect(state) {
        onDispose { if (state.dirty) { store.write(state.file, state.text); state.markSaved() } }
    }

    Column(Modifier.fillMaxSize()) {
        // open file tabs
        Row(Modifier.fillMaxWidth().background(Color(0xFF20090A)).horizontalScroll(rememberScrollState())) {
            open.forEachIndexed { i, e ->
                Row(
                    Modifier.clickable { onSelect(i) }
                        .background(if (i == active) Color(0xFF471617) else Color.Transparent)
                        .padding(start = 10.dp, top = 6.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(e.file.name + if (e.dirty) " •" else "", color = Color.White, fontSize = 12.sp)
                    IconButton(onClick = { onClose(i) }, modifier = Modifier.size(26.dp)) {
                        Icon(Icons.Default.Close, "Закрыть", tint = Color(0xFFA87F81), modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().background(Color(0xFF20090A)).horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically) {
            ToolIcon(Icons.Default.Save, "Сохранить") { save() }
            ToolIcon(Icons.Default.Undo, "Отмена") { state.undo() }
            ToolIcon(Icons.Default.Redo, "Повтор") { state.redo() }
            ToolIcon(Icons.Default.AutoFixHigh, "Исправить файл") {
                state.replaceAll(AutoFix.fixAll(state.text, state.lang))
            }
            ToolIcon(Icons.Default.PlayArrow, "Запуск") { save(); goTerminal(); term.runFile(state.file) }
            if (state.lang == Lang.PYTHON) ToolIcon(Icons.Default.Memory, "Запуск в Linux (pip, ввод)") { save(); goTerminal(); term.runLinuxPython(state.file) }
            if (state.lang == Lang.HTML) ToolIcon(Icons.Default.Language, "Просмотр") { save(); preview = true }
            ToolIcon(Icons.Default.Search, "Поиск") { findOpen = !findOpen }
            ToolIcon(Icons.Default.Share, "Экспорт") { save(); onExport(state.file) }
            ToolIcon(Icons.Default.Keyboard, "Клавиатура") { showKb = !showKb }
        }
        Row(Modifier.fillMaxWidth().background(Color(0xFF240B0B)).horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically) {
            run {
                val col = state.cursor - (state.text.lastIndexOf('\n', state.cursor - 1) + 1) + 1
                Text("Стр ${state.lineIndex + 1}:$col", color = Color(0xFFA87F81), fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp))
            }
            MiniBtn(if (state.selectMode) "✓ выделение" else "выделить", state.selectMode) { state.toggleSelect() }
            MiniBtn("всё") { state.selectAll() }
            MiniBtn("копия") { if (state.hasSel) clip.setText(androidx.compose.ui.text.AnnotatedString(state.selectedText)) }
            MiniBtn("вырезать") { if (state.hasSel) clip.setText(androidx.compose.ui.text.AnnotatedString(state.cutSel())) }
            MiniBtn("//") { state.toggleComment() }
            MiniBtn("дубль") { state.duplicateLine() }
            MiniBtn("⇥") { state.indentSel() }
            MiniBtn("⇤") { state.dedentSel() }
            MiniBtn("удалить строку") { state.deleteLine() }
            MiniBtn("←слово") { state.wordLeft() }
            MiniBtn("слово→") { state.wordRight() }
            MiniBtn("в начало") { state.fileStart() }
            MiniBtn("в конец") { state.fileEnd() }
            MiniBtn("строка↑") { state.moveLine(-1) }
            MiniBtn("строка↓") { state.moveLine(1) }
            MiniBtn("строка…") { gotoOpen = true }
            MiniBtn("символы") { symOpen = true }
            MiniBtn("отмена выд.") { state.clearSel() }
        }
        if (findOpen) {
            Column(Modifier.fillMaxWidth().background(Color(0xFF240B0B)).padding(6.dp)) {
                FieldBox("Найти", findQ, !focusRepl) { focusRepl = false }
                Spacer(Modifier.height(4.dp))
                FieldBox("Заменить на", replQ, focusRepl) { focusRepl = true }
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                    MiniBtn("далее") { info = if (state.findNext(findQ, state.cursor.let { if (state.hasSel) state.selEnd else it }, !caseSens)) "найдено: " + state.countMatches(findQ, !caseSens) else "не найдено" }
                    MiniBtn("назад") { info = if (state.findPrev(findQ, !caseSens)) "найдено: " + state.countMatches(findQ, !caseSens) else "не найдено" }
                    MiniBtn("Aa", caseSens) { caseSens = !caseSens }
                    MiniBtn("заменить") { state.replaceCurrent(findQ, replQ, !caseSens) }
                    MiniBtn("все") { info = "заменено: " + state.replaceAllText(findQ, replQ, !caseSens) }
                    MiniBtn("×") { findOpen = false }
                    Text(info, color = Color(0xFFE3B341), fontSize = 11.sp)
                }
            }
        }
        if (syntaxErr.isNotEmpty()) {
            val ln = syntaxErr.substringBefore("|").toIntOrNull()
            Text(
                "⛔ строка $ln: " + syntaxErr.substringAfter("|"), color = Color(0xFFF87171), fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().background(Color(0x33F87171))
                    .clickable { ln?.let { state.goToLine(it) } }.padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
        if (hints.isNotEmpty()) {
            Column(Modifier.fillMaxWidth().background(Color(0xFF331817)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                hints.forEach { Text("💡 $it", color = Color(0xFFE3B341), fontSize = 12.sp) }
            }
        }
        CodeEditor(state, Modifier.weight(1f).fillMaxWidth())
        val sugg = remember(state.text, state.cursor) { Completer.suggest(state.text, state.cursor, state.lang) }
        if (showKb && !findOpen && sugg.isNotEmpty()) {
            Row(
                Modifier.fillMaxWidth().background(Color(0xFF20090A)).horizontalScroll(rememberScrollState())
                    .padding(horizontal = 4.dp, vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                sugg.forEach { sg ->
                    Text(
                        sg.label, color = if (sg.snippet) Color(0xFFF48972) else Color(0xFFF7DDDE),
                        fontFamily = FontFamily.Monospace, fontSize = 13.sp,
                        modifier = Modifier.background(Color(0xFF471617), RoundedCornerShape(8.dp))
                            .clickable { state.complete(sg) }.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
        if (showKb) DevKeyboard(
            onText = { t ->
                if (findOpen) { if (focusRepl) replQ += t else findQ += t }
                else state.insert(t)
            },
            onCmd = { c ->
                if (findOpen && (c == Cmd.BACK || c == Cmd.ENTER || c == Cmd.PASTE)) {
                    val cur = if (focusRepl) replQ else findQ
                    val nv = when (c) {
                        Cmd.BACK -> cur.dropLast(1)
                        Cmd.PASTE -> cur + (clip.getText()?.text ?: "").replace("\n", " ")
                        else -> { info = if (state.findNext(findQ, if (state.hasSel) state.selEnd else state.cursor, !caseSens)) "" else "не найдено"; cur }
                    }
                    if (focusRepl) replQ = nv else findQ = nv
                } else when (c) {
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

    if (preview) HtmlPreview(state.text) { preview = false }
    if (symOpen) {
        val syms = remember(state.text, state.lang) { Symbols.list(state.text, state.lang) }
        AlertDialog(
            onDismissRequest = { symOpen = false },
            confirmButton = { TextButton(onClick = { symOpen = false }) { Text("Закрыть") } },
            title = { Text("Функции и классы") },
            text = {
                Column(Modifier.heightIn(max = 400.dp).verticalScroll(rememberScrollState())) {
                    if (syms.isEmpty()) Text("Ничего не найдено")
                    syms.forEach { (n, label) ->
                        TextButton(onClick = { state.goToLine(n); symOpen = false }) {
                            Text("$n  $label", fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                        }
                    }
                }
            }
        )
    }
    if (gotoOpen) NameDialog("Номер строки", onCancel = { gotoOpen = false }, onOk = {
        it.toIntOrNull()?.let { n -> state.goToLine(n) }; gotoOpen = false
    })
}

@Composable
private fun MiniBtn(label: String, on: Boolean = false, onClick: () -> Unit) {
    TextButton(onClick = onClick, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)) {
        Text(label, fontSize = 12.sp, color = if (on) Color(0xFFEF4444) else Color(0xFFF0CCCD))
    }
}

@Composable
private fun FieldBox(hint: String, value: String, focused: Boolean, onClick: () -> Unit) {
    Text(
        if (value.isEmpty() && !focused) hint else value + if (focused) "▏" else "",
        color = if (value.isEmpty() && !focused) Color(0xFF8C5B5D) else Color(0xFFEF4444),
        fontFamily = FontFamily.Monospace, fontSize = 13.sp,
        modifier = Modifier.fillMaxWidth().background(Color(0xFF120606)).clickable { onClick() }.padding(8.dp)
    )
}

@Composable
private fun ToolIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, desc: String, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(38.dp)) {
        Icon(icon, desc, tint = Color(0xFFF0CCCD), modifier = Modifier.size(20.dp))
    }
}

@Composable
fun TerminalScreen(term: TerminalState, current: File?, dir: File, onOpen: (String, Int) -> Unit = { _, _ -> }) {
    var input by remember { mutableStateOf("") }
    // cursor distance from the end of the input line (0 = cursor at the end). Developer: @yfeeeee
    var gap by remember { mutableIntStateOf(0) }
    val history = remember { mutableStateListOf<String>() }
    var hIdx by remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()
    val clip = LocalClipboardManager.current

    LaunchedEffect(term.lines.size) {
        if (term.lines.isNotEmpty()) listState.scrollToItem(term.lines.size - 1)
    }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().background(Color(0xFF20090A)), verticalAlignment = Alignment.CenterVertically) {
            TermMode.values().forEach { m ->
                TextButton(onClick = { term.mode = m }) {
                    Text(m.label, color = if (term.mode == m) Color(0xFFEF4444) else Color(0xFFA87F81))
                }
            }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { clip.setText(androidx.compose.ui.text.AnnotatedString(term.lines.joinToString("\n"))) }) {
                Text("копия", color = Color(0xFFA87F81))
            }
            TextButton(onClick = { term.lines.clear() }) { Text("очистить", color = Color(0xFFA87F81)) }
            TextButton(onClick = { current?.let { term.runFile(it) } }) { Text("▶ файл") }
            TextButton(onClick = { term.stopAll() }) { Text("■ стоп", color = Color(0xFFF48972)) }
        }
        LazyColumn(Modifier.weight(1f).fillMaxWidth().diamondPattern(Color(0xFF0D0404), Color(0x14B91C1C), 32.dp).padding(6.dp), state = listState) {
            items(term.lines) { l ->
                val c = when {
                    l.startsWith("[error]") || l.contains("Traceback") || l.contains("Error") -> Color(0xFFF48972)
                    l.startsWith("[") -> Color(0xFF79C0FF)
                    else -> Color(0xFFD0D7DE)
                }
                val tb = Regex("File \"([^\"]+)\", line (\\d+)").find(l)
                Text(l, color = if (tb != null) Color(0xFF79C0FF) else c, fontFamily = FontFamily.Monospace, fontSize = 12.sp,
                    modifier = if (tb != null) Modifier.clickable { onOpen(tb.groupValues[1], tb.groupValues[2].toInt()) } else Modifier)
            }
        }
        val dynamic = TermHints.suggest(input, term.mode, term.cwd, history)
        val quick = if (term.mode == TermMode.SH) listOf("ls", "ls -la", "pwd", "cd ..", "cd ~", "help", "get ", "run ", "log", "mod ", "token ")
            else listOf("print()", "import os", "import asyncio", "dir()", "help()", "exit()")
        Row(Modifier.fillMaxWidth().background(Color(0xFF160707)).horizontalScroll(rememberScrollState()).padding(horizontal = 4.dp)) {
            if (dynamic.isNotEmpty()) {
                // tapping a suggestion only writes the word into the input line, nothing is sent
                dynamic.forEach { q ->
                    TextButton(onClick = { input = TermHints.apply(input, q); gap = 0 }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)) {
                        Text(q, color = Color(0xFFF48972), fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                    }
                }
            } else {
                quick.forEach { q ->
                    TextButton(onClick = {
                        input = if (input.isBlank()) q else if (input.endsWith(" ")) input + q else "$input $q"
                        gap = 0
                    }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)) {
                        Text(q.trim(), color = Color(0xFFEF4444), fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                    }
                }
            }
        }
        Text(
            term.prompt + input.substring(0, (input.length - gap).coerceIn(0, input.length)) + "▏" + input.substring((input.length - gap).coerceIn(0, input.length)), color = Color(0xFFEF4444), fontFamily = FontFamily.Monospace, fontSize = 13.sp,
            modifier = Modifier.fillMaxWidth().background(Color(0xFF20090A)).padding(8.dp)
        )
        DevKeyboard(
            onText = { raw ->
                val t = raw.replace("()", "(").replace("[]", "[").replace("{}", "{")
                val p = (input.length - gap).coerceIn(0, input.length)
                val before = input.substring(0, p)
                val after = input.substring(p)
                val closeOf = mapOf("(" to ")", "[" to "]", "{" to "}", "\"" to "\"", "'" to "'")
                val isQuote = t == "\"" || t == "'"
                if (t in setOf(")", "]", "}", "\"", "'") && after.startsWith(t)) {
                    gap = (gap - 1).coerceAtLeast(0) // typed the closing char that is already there: jump over it
                } else if (closeOf.containsKey(t) && !(isQuote && before.lastOrNull()?.isLetterOrDigit() == true)) {
                    input = before + t + closeOf[t] + after // auto-close: cursor stays between the pair
                    gap += 1
                } else {
                    val e = if (term.mode == TermMode.PY && Settings.autoSpace) AutoSpace.apply(before, after, t, Lang.PYTHON) else null
                    input = (if (e != null) before.dropLast(e.del) + e.ins else before + t) + after
                }
            },
            onCmd = { c ->
                when (c) {
                    Cmd.BACK -> {
                        val p = (input.length - gap).coerceIn(0, input.length)
                        if (p > 0) {
                            val l = input[p - 1]
                            val r = input.getOrNull(p)
                            val pairClose = mapOf('(' to ')', '[' to ']', '{' to '}', '"' to '"', '\'' to '\'')[l]
                            if (pairClose != null && r == pairClose) {
                                input = input.removeRange(p - 1, p + 1); gap = (gap - 1).coerceAtLeast(0)
                            } else input = input.removeRange(p - 1, p)
                        }
                    }
                    Cmd.LEFT -> gap = (gap + 1).coerceAtMost(input.length)
                    Cmd.RIGHT -> gap = (gap - 1).coerceAtLeast(0)
                    Cmd.HOME -> gap = input.length
                    Cmd.END -> gap = 0
                    Cmd.ENTER -> {
                        if (input.isNotBlank()) history.add(input)
                        hIdx = history.size
                        term.submit(input); input = ""; gap = 0
                    }
                    Cmd.TAB -> if (term.mode == TermMode.SH) {
                        val r = tabComplete(input, term.cwd)
                        if (r.second.isNotEmpty()) term.print(r.second.joinToString("  "))
                        input = r.first; gap = 0
                    } else { input += "    "; gap = 0 }
                    Cmd.UP -> if (hIdx > 0) { hIdx--; input = history[hIdx]; gap = 0 }
                    Cmd.DOWN -> { if (hIdx < history.size - 1) { hIdx++; input = history[hIdx] } else { hIdx = history.size; input = "" }; gap = 0 }
                    Cmd.PASTE -> clip.getText()?.text?.let { val p = (input.length - gap).coerceIn(0, input.length); input = input.substring(0, p) + it.replace("\n", " ") + input.substring(p) }
                    else -> {}
                }
            }
        )
    }
}

@Composable
fun HtmlPreview(html: String, onClose: () -> Unit) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onClose,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(Modifier.fillMaxSize().background(Color.White)) {
            TextButton(onClick = onClose) { Text("Закрыть") }
            androidx.compose.ui.viewinterop.AndroidView(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                factory = { ctx ->
                    android.webkit.WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        loadDataWithBaseURL("https://localhost/", html, "text/html", "utf-8", null)
                    }
                }
            )
        }
    }
}

/** Tab completion of file names for the terminal input: returns the new line and the list of candidates (if several). Developer: @yfeeeee */
fun tabComplete(input: String, cwd: java.io.File): Pair<String, List<String>> {
    val sp = input.lastIndexOf(' ')
    val head = input.substring(0, sp + 1)
    val word = input.substring(sp + 1)
    val slash = word.lastIndexOf('/')
    val dirPart = if (slash >= 0) word.substring(0, slash + 1) else ""
    val prefix = word.substring(slash + 1)
    val dir = if (dirPart.isEmpty()) cwd else java.io.File(if (dirPart.startsWith("/")) dirPart else cwd.path + "/" + dirPart)
    val names = (dir.list() ?: return input to emptyList<String>())
        .filter { it.startsWith(prefix) && (!it.startsWith(".") || prefix.startsWith(".")) }
        .sorted()
    if (names.isEmpty()) return input to emptyList<String>()
    fun label(n: String) = if (java.io.File(dir, n).isDirectory) "$n/" else n
    if (names.size == 1) return (head + dirPart + label(names[0])) to emptyList<String>()
    var common = names[0]
    for (n in names) common = common.commonPrefixWith(n)
    return (head + dirPart + common) to names.map { label(it) }
}
