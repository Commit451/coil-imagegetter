package com.commit451.coilimagegetter

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.WorkerThread
import coil.ImageLoader
import coil.api.get
import coil.request.ErrorResult
import coil.request.GetRequest
import coil.request.GetRequestBuilder
import coil.request.SuccessResult
import kotlinx.coroutines.runBlocking

/**
 * For loading images synchronously
 */
internal object CoilCompat {

    @JvmStatic
    @WorkerThread
    fun getBlocking(
        context: Context,
        imageLoader: ImageLoader,
        url: String,
        builder: GetRequestBuilder.() -> Unit = {}
    ): Drawable = runBlocking {
        when (val result = imageLoader.execute(
            GetRequest.Builder(context).data(url).apply(builder).build()
        )) {
            is SuccessResult -> result.drawable
            is ErrorResult -> throw result.throwable
        }
    }
}