// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object Settings {
    var fontSize by mutableIntStateOf(14)
    var pulse by mutableStateOf(true)
    var accent by mutableIntStateOf(0)
    var autoUpdate by mutableStateOf(true)
    var autoSpace by mutableStateOf(true)
    /** Terminal color scheme: 0 red-black, 1 green, 2 light. Developer: @yfeeeee */
    var termTheme by mutableIntStateOf(0)
    val termBg get() = androidx.compose.ui.graphics.Color(longArrayOf(0xFF0D0404, 0xFF04100A, 0xFFF6F1F1)[termTheme.coerceIn(0, 2)])
    val termFg get() = androidx.compose.ui.graphics.Color(longArrayOf(0xFFD0D7DE, 0xFFB7F0C8, 0xFF24292F)[termTheme.coerceIn(0, 2)])
    val accents = listOf(0xFFEF4444, 0xFFF472B6, 0xFF60A5FA, 0xFF34D399, 0xFFFBBF24)
    val accentColor: androidx.compose.ui.graphics.Color get() = androidx.compose.ui.graphics.Color(accents[accent.coerceIn(0, accents.lastIndex)])

    fun load(ctx: Context) {
        val sp = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE)
        fontSize = sp.getInt("font", 14).coerceIn(10, 24)
        pulse = sp.getBoolean("pulse", true)
        accent = sp.getInt("accent", 0).coerceIn(0, accents.lastIndex)
        autoUpdate = sp.getBoolean("autoupdate", true)
        autoSpace = sp.getBoolean("autospace", true)
        termTheme = sp.getInt("termtheme", 0).coerceIn(0, 2)
    }

    fun save(ctx: Context) {
        ctx.getSharedPreferences("settings", Context.MODE_PRIVATE).edit()
            .putInt("font", fontSize).putBoolean("pulse", pulse).putInt("accent", accent).putBoolean("autoupdate", autoUpdate).putBoolean("autospace", autoSpace).putInt("termtheme", termTheme).apply()
    }
}
