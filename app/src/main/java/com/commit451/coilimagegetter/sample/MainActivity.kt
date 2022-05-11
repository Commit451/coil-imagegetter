package com.commit451.coilimagegetter.sample

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import androidx.appcompat.app.AppCompatActivity
import com.commit451.coilimagegetter.CoilImageGetter
import com.commit451.coilimagegetter.sample.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import ru.gildor.coroutines.okhttp.await
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivityMainBinding

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val client = OkHttpClient.Builder()
        .build()
    private val renderer: HtmlRenderer = HtmlRenderer.builder()
        .build()
    private val parser: Parser = Parser.builder()
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        job = Job()

        launch {
            val deferred = async(Dispatchers.Default) {
                loadReadmeAsSpanned()
            }
            binding.textView.text = deferred.await()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private suspend fun loadReadmeAsHtml(): String {
        val request = Request.Builder()
            .url("https://raw.githubusercontent.com/Jawnnypoo/open-meh/master/README.md")
            .build()
        val result = client.newCall(request).await()
        val markdown = result.body?.string()

        val document = parser.parse(markdown)
        return renderer.render(document)
    }

    private suspend fun loadReadmeAsSpanned(): Spanned {
        val html = loadReadmeAsHtml()
        val getter = CoilImageGetter(
            textView = binding.textView,
            sourceModifier = { source ->
                if (source.startsWith("http")) {
                    source
                } else {
                    "https://raw.githubusercontent.com/Jawnnypoo/open-meh/master/$source"
                }
            }
        )
        return html.formatAsHtml(getter)
    }

    /**
     * Assures HTML is formatted the same way pre and post Android N
     */
    @Suppress("DEPRECATION")
    fun String.formatAsHtml(
        imageGetter: Html.ImageGetter? = null,
        tagHandler: Html.TagHandler? = null
    ): Spanned {
        return if (Build.VERSION.SDK_INT >= 24) {
            Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY, imageGetter, tagHandler)
        } else {
            Html.fromHtml(this, imageGetter, tagHandler)
        }
    }
}
