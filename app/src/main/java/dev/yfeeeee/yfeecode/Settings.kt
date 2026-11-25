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

    fun load(ctx: Context) {
        val sp = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE)
        fontSize = sp.getInt("font", 14).coerceIn(10, 24)
        pulse = sp.getBoolean("pulse", true)
    }

    fun save(ctx: Context) {
        ctx.getSharedPreferences("settings", Context.MODE_PRIVATE).edit()
            .putInt("font", fontSize).putBoolean("pulse", pulse).apply()
    }
}
