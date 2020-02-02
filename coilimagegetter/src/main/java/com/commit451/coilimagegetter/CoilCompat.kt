package com.commit451.coilimagegetter

import android.graphics.drawable.Drawable
import androidx.annotation.WorkerThread
import coil.ImageLoader
import coil.api.get
import coil.request.GetRequestBuilder
import kotlinx.coroutines.runBlocking

/**
 * For loading images synchronously
 */
internal object CoilCompat {

    @JvmStatic
    @WorkerThread
    fun getBlocking(
        imageLoader: ImageLoader,
        url: String,
        builder: GetRequestBuilder.() -> Unit = {}
    ): Drawable = runBlocking { imageLoader.get(url, builder) }
}