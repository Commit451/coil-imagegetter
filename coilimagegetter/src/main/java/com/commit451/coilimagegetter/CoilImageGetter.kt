package com.commit451.coilimagegetter

import android.graphics.drawable.Drawable
import android.text.Html
import coil.Coil
import coil.ImageLoader

/**
 * An [Html.ImageGetter] implementation that uses Coil to load images.
 * @param imageLoader Allows you to specify your own imageLoader
 * @param sourceModifier Allows you to modify the source (typically a URL) of the image before it
 * is loaded as a drawable. This can be used to take an image that has path references such as
 * "images/cat.png" and fully resolve the path to a URL that can be loaded successfully via Coil.
 */
open class CoilImageGetter(
    private val imageLoader: ImageLoader = Coil.loader(),
    private val sourceModifier: ((source: String) -> String)? = null
) : Html.ImageGetter {

    override fun getDrawable(source: String): Drawable {
        val finalSource = sourceModifier?.invoke(source) ?: source
        val drawable = CoilCompat.getBlocking(imageLoader, finalSource)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        return drawable
    }
}