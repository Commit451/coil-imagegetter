package com.commit451.coilimagegetter

import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.widget.TextView
import coil.Coil
import coil.ImageLoader
import coil.api.load

/**
 * An [Html.ImageGetter] implementation that uses Coil to load images.
 */
open class CoilImageGetter(
    /**
     * The [TextView] that the HTML will be rendered to.
     */
    private val textView: TextView,
    /**
     * Allows you to specify your own imageLoader
     */
    private val imageLoader: ImageLoader = Coil.loader(),
    /**
     * Allows you to modify the source (typically a URL) of the image before it is loaded as a drawable.
     * This can be used to take an image that has path references such as "images/cat.png" and fully
     * resolve the path to a URL that can be loaded successfully via Coil.
     */
    private val sourceModifier: ((source: String) -> String)? = null
) : Html.ImageGetter {

    private var maxWidth: Int = -1

    override fun getDrawable(source: String): Drawable {
        val drawablePlaceholder = BitmapDrawablePlaceHolder()
        val finalSource = sourceModifier?.invoke(source) ?: source

        imageLoader.load(textView.context, finalSource) {
            target { drawable ->
                if (maxWidth == -1) {
                    val horizontalPadding = textView.paddingLeft + textView.paddingRight
                    maxWidth = textView.measuredWidth - horizontalPadding
                    if (maxWidth == 0) {
                        maxWidth = Int.MAX_VALUE
                    }
                }

                val aspectRatio: Double = 1.0 * drawable.intrinsicWidth / drawable.intrinsicHeight
                val width = maxWidth.coerceAtMost(drawable.intrinsicWidth)
                val height = (width / aspectRatio).toInt()

                drawable.setBounds(0, 0, width, height)

                drawablePlaceholder.drawable = drawable
                drawablePlaceholder.setBounds(0, 0, width, height)

                textView.text = textView.text // invalidate() doesn't work correctly...
            }
        }
        return drawablePlaceholder
    }

    private class BitmapDrawablePlaceHolder : BitmapDrawable() {

        var drawable: Drawable? = null

        override fun draw(canvas: Canvas) {
            drawable?.draw(canvas)
        }
    }
}