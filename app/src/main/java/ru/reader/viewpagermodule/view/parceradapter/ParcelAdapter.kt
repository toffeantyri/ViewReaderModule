package ru.reader.viewpagermodule.view.parceradapter

import android.util.Log
import android.widget.Toast
import com.kursx.parser.fb2.FictionBook
import ru.reader.viewpagermodule.APP_CONTEXT
import ru.reader.viewpagermodule.R
import java.io.File

class ParcelAdapter {

    private var fb2File: FictionBook? = null

    fun setFb2File(filePath: String) {
        val fb2 = try {
            fb2File = FictionBook(File(filePath))
            Log.d("MyLog", "${this::class.java.simpleName} - $filePath - $fb2File")
        } catch (e: Exception) {
            showToast(e.message ?: APP_CONTEXT.getString(R.string.toast_error_load))
            Log.d("MyLog", e.stackTraceToString())
        }
    }



    private fun showToast(message: String) {
        Toast.makeText(APP_CONTEXT, message, Toast.LENGTH_LONG).show()
    }

}