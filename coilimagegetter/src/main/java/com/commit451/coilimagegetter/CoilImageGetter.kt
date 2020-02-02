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

    override fun getDrawable(source: String): Drawable {
        val drawablePlaceholder = BitmapDrawablePlaceHolder()
        val finalSource = sourceModifier?.invoke(source) ?: source

        imageLoader.load(textView.context, finalSource) {
            target { drawable ->
                drawablePlaceholder.updateDrawable(drawable)
                textView.text = textView.text // invalidate() doesn't work correctly...
            }
        }
        // Since this loads async, we return a "blank" drawable, which we update
        // later
        return drawablePlaceholder
    }

    private class BitmapDrawablePlaceHolder : BitmapDrawable() {

        private var drawable: Drawable? = null

        override fun draw(canvas: Canvas) {
            drawable?.draw(canvas)
        }

        fun updateDrawable(drawable: Drawable) {
            this.drawable = drawable
            val width = drawable.intrinsicWidth
            val height = drawable.intrinsicHeight
            drawable.setBounds(0, 0, width, height)
            setBounds(0, 0, width, height)
        }
    }
}