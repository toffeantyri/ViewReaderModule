package ru.reader.viewpagermodule


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log


fun String.convertToBitmap(): Bitmap? {
    try {
        val decodeString: ByteArray = Base64.decode(this, Base64.DEFAULT)
        val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.size)
        return bitmap
    } catch (e: Exception) {
        Log.d("MyLog", e.stackTraceToString())
        return try {
            val decodeString: ByteArray = Base64.decode(this, Base64.URL_SAFE)
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.size)
            bitmap
        } catch (e: Exception) {
            Log.d("MyLog", e.stackTraceToString())
            null
        }
    }
}
