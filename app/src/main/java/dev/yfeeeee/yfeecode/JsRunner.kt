// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView

/**
 * Runs JS in a hidden WebView (fetch works, so simple Telegram HTTP bots can run).
 * Node APIs (require, fs, npm packages) are NOT available.
 */
class JsRunner(private val ctx: Context, private val onLine: (String) -> Unit) {
    private var web: WebView? = null

    private inner class Bridge {
        @JavascriptInterface
        fun log(s: String) = onLine(s)
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    fun run(code: String) {
        stop()
        val w = WebView(ctx)
        w.settings.javaScriptEnabled = true
        w.settings.domStorageEnabled = true
        w.addJavascriptInterface(Bridge(), "Android")
        val safeCode = code.replace("</script", "<\\/script", ignoreCase = true)
        val wrapper = """
            <html><body><script>
            (function(){
              function fmt(a){return Array.prototype.map.call(a,function(x){
                try{return typeof x==='object'?JSON.stringify(x):String(x)}catch(e){return String(x)}}).join(' ')}
              console.log=function(){Android.log(fmt(arguments))};
              console.error=function(){Android.log('[error] '+fmt(arguments))};
              console.warn=function(){Android.log('[warn] '+fmt(arguments))};
              console.info=function(){Android.log(fmt(arguments))};
              console.debug=function(){Android.log(fmt(arguments))};
              window.onerror=function(m,s,l){Android.log('[error] '+m+' (line '+l+')')};
              window.onunhandledrejection=function(e){Android.log('[error] '+e.reason)};
            })();
            </script><script>
            $safeCode
            </script></body></html>
        """.trimIndent()
        w.loadDataWithBaseURL("https://localhost/", wrapper, "text/html", "utf-8", null)
        web = w
        onLine("[run] JS started")
    }

    fun stop() {
        web?.let { it.loadUrl("about:blank"); it.destroy() }
        web = null
    }
}
