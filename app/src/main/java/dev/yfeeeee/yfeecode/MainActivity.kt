// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        Settings.load(this)
        val store = FileStore(this)
        val term = TerminalState(this, store)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(primary = Settings.accentColor, surface = Color(0xFF120606))) {
                Surface(Modifier.fillMaxSize(), color = Color(0xFF120606)) {
                    var pendingExport by remember { mutableStateOf<File?>(null) }
                    var importDir by remember { mutableStateOf(store.root) }
                    var refresh by remember { mutableIntStateOf(0) }

                    val notif = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
                    LaunchedEffect(Unit) { notif.launch(Manifest.permission.POST_NOTIFICATIONS) }

                    // File work runs off the main thread (no ANR) and never crashes the app: errors become a toast.
                    val scope = rememberCoroutineScope()
                    val ctx = LocalContext.current
                    fun io(okMsg: String, failMsg: String, work: () -> Unit) {
                        scope.launch {
                            val ok = withContext(Dispatchers.IO) {
                                try { work(); true } catch (_: Exception) { false } catch (_: OutOfMemoryError) { false }
                            }
                            if (ok) refresh++
                            Toast.makeText(ctx, if (ok) okMsg else failMsg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    val importer = rememberLauncherForActivityResult(
                        ActivityResultContracts.OpenMultipleDocuments()
                    ) { uris: List<Uri> ->
                        val dir = importDir
                        if (uris.isNotEmpty()) io("Файлы импортированы", "Не удалось импортировать файл") {
                            uris.forEach { store.importUri(it, dir) }
                        }
                    }
                    val exporter = rememberLauncherForActivityResult(
                        ActivityResultContracts.CreateDocument("*/*")
                    ) { uri: Uri? ->
                        val f = pendingExport
                        if (uri != null && f != null) io("Файл сохранён", "Не удалось сохранить файл") { store.exportTo(uri, f) }
                    }

                    val zipOut = rememberLauncherForActivityResult(
                        ActivityResultContracts.CreateDocument("application/zip")
                    ) { uri: Uri? ->
                        if (uri != null) io("ZIP сохранён", "Не удалось создать ZIP") { store.zipTo(uri) }
                    }
                    val zipIn = rememberLauncherForActivityResult(
                        ActivityResultContracts.OpenDocument()
                    ) { uri: Uri? ->
                        val dir = importDir
                        if (uri != null) io("ZIP распакован", "Не удалось распаковать ZIP") { store.unzipFrom(uri, dir) }
                    }

                    App(
                        onZipExport = { zipOut.launch("YfeeCode-project.zip") },
                        onZipImport = { dir -> importDir = dir; zipIn.launch(arrayOf("application/zip", "application/octet-stream")) },
                        store = store,
                        term = term,
                        refreshKey = refresh,
                        onImport = { dir -> importDir = dir; importer.launch(arrayOf("*/*")) },
                        onExport = { f -> pendingExport = f; exporter.launch(f.name) }
                    )
                }
            }
        }
    }
}
