// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import android.Manifest
import android.net.Uri
import android.os.Bundle
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
import androidx.core.view.WindowCompat
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        Settings.load(this)
        val store = FileStore(this)
        val term = TerminalState(this, store)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(primary = Color(0xFFC084FC), surface = Color(0xFF0A0612))) {
                Surface(Modifier.fillMaxSize(), color = Color(0xFF0A0612)) {
                    var pendingExport by remember { mutableStateOf<File?>(null) }
                    var importDir by remember { mutableStateOf(store.root) }
                    var refresh by remember { mutableIntStateOf(0) }

                    val notif = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
                    LaunchedEffect(Unit) { notif.launch(Manifest.permission.POST_NOTIFICATIONS) }

                    val importer = rememberLauncherForActivityResult(
                        ActivityResultContracts.OpenMultipleDocuments()
                    ) { uris: List<Uri> ->
                        uris.forEach { store.importUri(it, importDir) }
                        refresh++
                    }
                    val exporter = rememberLauncherForActivityResult(
                        ActivityResultContracts.CreateDocument("*/*")
                    ) { uri: Uri? ->
                        val f = pendingExport
                        if (uri != null && f != null) store.exportTo(uri, f)
                    }

                    App(
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
