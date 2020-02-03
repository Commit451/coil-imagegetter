package com.commit451.coilimagegetter

import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Looper
import android.text.Html
import android.widget.TextView
import coil.Coil
import coil.ImageLoader
import coil.api.load

/**
 * An [Html.ImageGetter] implementation that uses Coil to load images. If you use this getter within
 * a background thread, Images will load before any text is shown within the text view. If you use
 * this getter within the main thread, text will show first, then images will "pop in".
 * @param textView the [TextView] which will receive the formatted HTML
 * @param imageLoader Allows you to specify your own imageLoader
 * @param sourceModifier Allows you to modify the source (typically a URL) of the image before it
 * is loaded as a drawable. This can be used to take an image that has path references such as
 * "images/cat.png" and fully resolve the path to a URL that can be loaded successfully via Coil.
 */
open class CoilImageGetter(
    private val textView: TextView,
    private val imageLoader: ImageLoader = Coil.loader(),
    private val sourceModifier: ((source: String) -> String)? = null
) : Html.ImageGetter {

    override fun getDrawable(source: String): Drawable {
        val finalSource = sourceModifier?.invoke(source) ?: source

        return if (isMainThread()) {
            val drawablePlaceholder = DrawablePlaceHolder()
            imageLoader.load(textView.context, finalSource) {
                target { drawable ->
                    drawablePlaceholder.updateDrawable(drawable)
                    // invalidating the drawable doesn't seem to be enough...
                    textView.text = textView.text
                }
            }
            // Since this loads async, we return a "blank" drawable, which we update
            // later
            return drawablePlaceholder
        } else {
            val drawable = CoilCompat.getBlocking(imageLoader, finalSource)
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            drawable
        }
    }

    private class DrawablePlaceHolder : BitmapDrawable() {

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

    private fun isMainThread(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }
}