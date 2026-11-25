// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import android.content.Context

enum class Kind { BUILD, TYPE }

data class Exercise(
    val kind: Kind,
    val prompt: String,
    /** BUILD: tokens in the right order. TYPE: accepted variants (compared token by token). */
    val answers: List<String>,
    val blocks: List<String> = emptyList(),
    val explain: String = ""
)

data class Lesson(val title: String, val theory: String, val exercises: List<Exercise>)

data class Course(val id: String, val title: String, val emoji: String, val lessons: List<Lesson>)

/** Collect a command from blocks. [tokens] separated by '|', [extra] = distractor blocks separated by '|'. */
fun build(prompt: String, tokens: String, extra: String = "", explain: String = ""): Exercise {
    val t = tokens.split("|")
    val e = if (extra.isBlank()) emptyList() else extra.split("|")
    return Exercise(Kind.BUILD, prompt, t, (t + e).shuffled(java.util.Random(prompt.hashCode().toLong())), explain)
}

/** Type the command manually. First answer is shown as the reference when wrong. */
fun type(prompt: String, vararg answers: String, explain: String = ""): Exercise =
    Exercise(Kind.TYPE, prompt, answers.toList(), emptyList(), explain)

object Checker {
    private val tok = Regex("[A-Za-z_0-9]+|[^\\sA-Za-z_0-9]")

    private fun tokens(s: String) = tok.findAll(s.trim().replace('\'', '"')).map { it.value }.toList()

    fun typed(input: String, ex: Exercise): Boolean {
        val t = tokens(input)
        return ex.answers.any { tokens(it) == t }
    }

    fun built(selected: List<String>, ex: Exercise) = selected == ex.answers
}

class Progress(ctx: Context) {
    private val sp = ctx.getSharedPreferences("learn", Context.MODE_PRIVATE)

    fun done(course: String, lesson: Int) = sp.getBoolean("$course:$lesson", false)
    fun doneCount(course: String, total: Int) = (0 until total).count { done(course, it) }
    var xp: Int
        get() = sp.getInt("xp", 0)
        set(v) = sp.edit().putInt("xp", v).apply()

    private fun today() = java.time.LocalDate.now().toString()

    /** Marks today as an active learning day and updates the streak. */
    private fun touch() {
        val t = today()
        val last = sp.getString("last", "") ?: ""
        if (last == t) return
        val y = java.time.LocalDate.now().minusDays(1).toString()
        val s = if (last == y) sp.getInt("streak", 0) + 1 else 1
        sp.edit().putString("last", t).putInt("streak", s).apply()
    }

    /** Streak that is still alive (learned today or yesterday). */
    fun streak(): Int {
        val last = sp.getString("last", "") ?: ""
        val y = java.time.LocalDate.now().minusDays(1).toString()
        return if (last == today() || last == y) sp.getInt("streak", 0) else 0
    }

    fun level(): Int = xp / 100 + 1
    fun levelProgress(): Float = (xp % 100) / 100f

    fun practiceDone(id: String) = sp.getBoolean("pr:$id", false)
    fun completePractice(id: String, gained: Int) {
        if (!practiceDone(id)) {
            sp.edit().putBoolean("pr:$id", true).apply()
            xp += gained
            touch()
        }
    }

    fun complete(course: String, lesson: Int, gained: Int) {
        val first = !done(course, lesson)
        sp.edit().putBoolean("$course:$lesson", true).apply()
        xp += if (first) gained else gained / 3
        touch()
    }
}

object Courses {
    val all: List<Course> by lazy {
        listOf(pythonCourse(), jsCourse(), htmlCourse(), cssCourse(), cppCourse(), csCourse())
    }
}

data class Check(val regex: Regex, val message: String)

/** Practical task solved in the built-in editor. For Python [expectOut] is compared with the real output. */
data class Practice(
    val id: String,
    val title: String,
    val task: String,
    val starter: String,
    val lang: Lang,
    val checks: List<Check>,
    val expectOut: String? = null
)

fun must(regex: String, message: String) = Check(Regex(regex), message)
