// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Violet = Color(0xFFEF4444)
private val Panel = Color(0xFF240B0B)
private val Chip = Color(0xFF471617)
private val Good = Color(0xFF4ADE80)
private val Bad = Color(0xFFF87171)
private val Muted = Color(0xFFA87F81)

@Composable
fun LearnScreen() {
    val ctx = LocalContext.current
    val prog = remember { Progress(ctx) }
    var course by remember { mutableStateOf<Course?>(null) }
    var lesson by remember { mutableStateOf<Int?>(null) }
    var practice by remember { mutableStateOf<Practice?>(null) }
    var tick by remember { mutableIntStateOf(0) }
    var reading by remember { mutableStateOf(false) }

    BackHandler(enabled = course != null) { if (reading) reading = false else if (lesson != null) lesson = null else course = null }
    val pr = practice

    val c = course
    val l = lesson
    when {
        c != null && reading -> TheoryScreen(c) { reading = false }
        c != null && pr != null -> PracticeScreen(pr, prog) { practice = null; tick++ }
        c != null && l != null -> LessonRunner(c, l, prog) { lesson = null; tick++ }
        c != null -> CourseMap(c, prog, tick, onBack = { course = null }, onOpen = { lesson = it }, onPractice = { practice = it }, onTheory = { reading = true })
        else -> LearnHome(prog, tick) { course = it }
    }
}

@Composable
private fun LearnHome(prog: Progress, tick: Int, onOpen: (Course) -> Unit) {
    val xp = remember(tick) { prog.xp }
    val streak = remember(tick) { prog.streak() }
    val level = remember(tick) { prog.level() }
    val lp = remember(tick) { prog.levelProgress() }
    Column(Modifier.fillMaxSize().padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Уроки", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("🔥 $streak", color = if (streak > 0) Color(0xFFFFB86C) else Muted, fontSize = 16.sp)
            Spacer(Modifier.width(12.dp))
            Text("⭐ $xp XP", color = Violet, fontSize = 16.sp)
        }
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Уровень $level", color = Color.White, fontSize = 13.sp)
            Spacer(Modifier.width(10.dp))
            Box(Modifier.weight(1f).height(8.dp).background(Chip, CircleShape)) {
                Box(Modifier.fillMaxWidth(lp.coerceIn(0.02f, 1f)).fillMaxHeight().background(Violet, CircleShape))
            }
        }
        Text(if (streak > 0) "Серия: $streak дн. подряд. Занимайся каждый день!" else "Пройди урок сегодня, чтобы начать серию.",
            color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        Text("Короткие уроки: собери команду из блоков, затем впиши её сам.", color = Muted, fontSize = 13.sp,
            modifier = Modifier.padding(vertical = 8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            itemsIndexed(Courses.all) { _, c ->
                val done = remember(tick) { prog.doneCount(c.id, c.lessons.size) }
                Row(
                    Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0x55B91C1C), RoundedCornerShape(16.dp))
                        .clickable { onOpen(c) }.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(c.emoji, fontSize = 30.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(c.title, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(6.dp))
                        Box(Modifier.fillMaxWidth().height(8.dp).background(Chip, CircleShape)) {
                            Box(Modifier.fillMaxWidth(done / c.lessons.size.toFloat()).fillMaxHeight().background(Violet, CircleShape))
                        }
                        Text("$done / ${c.lessons.size} уроков", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseMap(c: Course, prog: Progress, tick: Int, onBack: () -> Unit, onOpen: (Int) -> Unit, onPractice: (Practice) -> Unit, onTheory: () -> Unit) {
    val doneCount = remember(tick) { prog.doneCount(c.id, c.lessons.size) }
    Column(Modifier.fillMaxSize().padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← назад", color = Violet) }
            Text("${c.emoji} ${c.title}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            TextButton(onClick = onTheory) { Text("📖 Теория", color = Violet) }
        }
        LazyColumn(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            itemsIndexed(c.lessons) { i, les ->
                val done = prog.done(c.id, i)
                val locked = i > doneCount
                val shift = when (i % 4) { 0 -> 0; 1 -> 36; 2 -> 0; else -> (-36) }
                Row(
                    Modifier.fillMaxWidth().padding(start = (40 + shift).dp, end = 12.dp)
                        .clickable(enabled = !locked) { onOpen(i) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(56.dp).background(
                            when { done -> Good; locked -> Chip; else -> Violet }, CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text(if (done) "✓" else if (locked) "🔒" else "${i + 1}", color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.width(12.dp))
                    Text(les.title, color = if (locked) Muted else Color.White, fontSize = 15.sp)
                }
            }
            item {
                Text("Практика в редакторе", color = Violet, fontSize = 17.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp))
            }
            items(Practices.forCourse(c.id)) { pr ->
                val done = prog.practiceDone(pr.id)
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                        .background(Panel, RoundedCornerShape(14.dp))
                        .border(1.dp, if (done) Good else Color(0x55B91C1C), RoundedCornerShape(14.dp))
                        .clickable { onPractice(pr) }.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (done) "✓" else "⌨", color = if (done) Good else Violet, fontSize = 20.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(pr.title, color = Color.White, fontSize = 15.sp, modifier = Modifier.weight(1f))
                    Text("+15 XP", color = Muted, fontSize = 11.sp)
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

/** Reading mode: the whole course theory as a book, no exercises. */
@Composable
private fun TheoryScreen(c: Course, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← к заданиям", color = Violet) }
            Text("📖 ${c.title}: теория", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        LazyColumn(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            itemsIndexed(c.lessons) { i, les ->
                Column(Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(14.dp)).padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${i + 1}. ${les.title}", color = Violet, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    TheoryText(les.theory)
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun TheoryText(text: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        text.split("\n").forEach { line ->
            if (line.startsWith(">")) {
                Text(
                    line.removePrefix(">").trimStart(), color = Violet, fontFamily = FontFamily.Monospace, fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth().background(Color(0xFF0D0404), RoundedCornerShape(8.dp)).padding(10.dp)
                )
            } else if (line.isNotBlank()) Text(line, color = Color(0xFFF7DDDE), fontSize = 15.sp, lineHeight = 22.sp)
        }
    }
}

@Composable
private fun BlockChip(text: String, onClick: () -> Unit) {
    Box(
        Modifier.background(Chip, RoundedCornerShape(12.dp)).border(1.dp, Color(0x88B91C1C), RoundedCornerShape(12.dp))
            .clickable { onClick() }.padding(horizontal = 12.dp, vertical = 9.dp)
    ) { Text(text, color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 15.sp) }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LessonRunner(c: Course, idx: Int, prog: Progress, onExit: () -> Unit) {
    val lesson = c.lessons[idx]
    var phase by remember { mutableIntStateOf(0) } // 0 theory, 1 exercises, 2 done
    val queue = remember { mutableStateListOf<Exercise>().apply { addAll(lesson.exercises) } }
    var solved by remember { mutableIntStateOf(0) }
    var mistakes by remember { mutableIntStateOf(0) }
    var gained by remember { mutableIntStateOf(0) }
    val picked = remember { mutableStateListOf<Int>() }
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Boolean?>(null) }
    val total = lesson.exercises.size

    BackHandler { onExit() }

    fun check() {
        val ex = queue.first()
        val ok = if (ex.kind == Kind.BUILD) Checker.built(picked.map { ex.blocks[it] }, ex) else Checker.typed(input, ex)
        result = ok
        if (ok) solved++ else mistakes++
    }

    fun next() {
        val cur = queue.removeAt(0)
        if (result == false) queue.add(cur)
        picked.clear(); input = ""; result = null
        if (queue.isEmpty()) {
            gained = 10 + (total - mistakes).coerceAtLeast(0) * 2
            prog.complete(c.id, idx, gained)
            phase = 2
        }
    }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onExit) { Text("✕", color = Muted, fontSize = 18.sp) }
            Box(Modifier.weight(1f).height(10.dp).background(Chip, CircleShape)) {
                val f = if (phase == 2) 1f else solved.coerceAtMost(total) / total.toFloat()
                Box(Modifier.fillMaxWidth(f).fillMaxHeight().background(Violet, CircleShape))
            }
            Spacer(Modifier.width(12.dp))
        }

        when (phase) {
            0 -> Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)) {
                Text(lesson.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                TheoryText(lesson.theory)
                Spacer(Modifier.height(20.dp))
                Button(onClick = { phase = 1 }, modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Violet, contentColor = Color.Black)) { Text("Начать практику") }
            }
            1 -> {
                val ex = queue.first()
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)) {
                    Text(if (ex.kind == Kind.BUILD) "Собери команду из блоков" else "Впиши команду сам",
                        color = Muted, fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(ex.prompt, color = Color.White, fontSize = 18.sp, lineHeight = 26.sp)
                    Spacer(Modifier.height(16.dp))
                    if (ex.kind == Kind.BUILD) {
                        FlowRow(
                            Modifier.fillMaxWidth().heightIn(min = 64.dp).background(Panel, RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0x55B91C1C), RoundedCornerShape(12.dp)).padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            picked.toList().forEach { bi ->
                                BlockChip(ex.blocks[bi]) { if (result == null) picked.remove(bi) }
                            }
                        }
                        Spacer(Modifier.height(18.dp))
                        FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            ex.blocks.forEachIndexed { bi, b ->
                                if (bi !in picked) BlockChip(b) { if (result == null) picked.add(bi) }
                            }
                        }
                    } else {
                        Text(
                            "$ " + input + if (result == null) "▏" else "", color = Violet, fontFamily = FontFamily.Monospace, fontSize = 16.sp,
                            modifier = Modifier.fillMaxWidth().background(Color(0xFF0D0404), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0x55B91C1C), RoundedCornerShape(12.dp)).padding(14.dp)
                        )
                    }
                    result?.let { ok ->
                        Spacer(Modifier.height(16.dp))
                        Column(Modifier.fillMaxWidth().background(if (ok) Color(0x224ADE80) else Color(0x22F87171), RoundedCornerShape(12.dp)).padding(12.dp)) {
                            Text(if (ok) "✓ Верно!" else "✗ Неверно", color = if (ok) Good else Bad, fontWeight = FontWeight.Bold)
                            if (!ok) Text(
                                "Правильно: " + if (ex.kind == Kind.BUILD) ex.answers.joinToString(" ") else ex.answers[0],
                                color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            if (ex.explain.isNotBlank()) Text(ex.explain, color = Color(0xFFF7DDDE), fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
                        }
                    }
                }
                val canCheck = if (ex.kind == Kind.BUILD) picked.isNotEmpty() else input.isNotBlank()
                Button(
                    onClick = { if (result == null) check() else next() },
                    enabled = result != null || canCheck,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (result) { true -> Good; false -> Bad; null -> Violet }, contentColor = Color.Black)
                ) { Text(if (result == null) "Проверить" else "Дальше") }
                if (ex.kind == Kind.TYPE && result == null) DevKeyboard(
                    onText = { input += it.replace("()", "(").replace("[]", "[").replace("{}", "{").replace("<>", "<") },
                    onCmd = { cmd ->
                        when (cmd) {
                            Cmd.BACK -> input = input.dropLast(1)
                            Cmd.ENTER -> if (input.isNotBlank()) check()
                            Cmd.TAB -> input += "    "
                            else -> {}
                        }
                    }
                )
            }
            else -> Column(Modifier.weight(1f).fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("🎉", fontSize = 64.sp)
                Text("Урок пройден!", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("+$gained XP · ошибок: $mistakes", color = Violet, fontSize = 16.sp, modifier = Modifier.padding(vertical = 10.dp))
                Button(onClick = onExit, colors = ButtonDefaults.buttonColors(containerColor = Violet, contentColor = Color.Black)) {
                    Text("Продолжить")
                }
            }
        }
    }
}
