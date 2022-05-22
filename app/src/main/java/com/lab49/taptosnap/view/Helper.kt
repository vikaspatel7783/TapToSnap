package com.lab49.taptosnap.view

import android.graphics.Bitmap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.io.ByteArrayOutputStream

class Helper {

    companion object {
        fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
            val bout = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bout)
            return bout.toByteArray()
        }

        fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
            observe(lifecycleOwner, object : Observer<T> {
                override fun onChanged(t: T?) {
                    observer.onChanged(t)
                    removeObserver(this)
                }
            })
        }
    }
}