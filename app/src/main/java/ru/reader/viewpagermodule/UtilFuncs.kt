package ru.reader.viewpagermodule


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.*


suspend fun convertToBitmap(stringBase64 : String): Bitmap? = withContext(Dispatchers.Default){
            try {
            val decodeString: ByteArray = Base64.decode(stringBase64, Base64.DEFAULT)
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.size)
                return@withContext bitmap
        } catch (e: Exception) {
            Log.d("MyLog", e.stackTraceToString())
                return@withContext try {
                    val decodeString: ByteArray = Base64.decode(stringBase64, Base64.URL_SAFE)
                    val bitmap: Bitmap =
                        BitmapFactory.decodeByteArray(decodeString, 0, decodeString.size)
                    bitmap
                } catch (e: Exception) {
                    Log.d("MyLog", e.stackTraceToString())
                    null
                }
    }
}
