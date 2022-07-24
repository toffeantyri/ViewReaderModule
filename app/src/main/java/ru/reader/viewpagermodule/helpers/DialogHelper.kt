package ru.reader.viewpagermodule.helpers

import android.app.Activity
import android.view.View
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.dialog_book_loading.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.reader.viewpagermodule.R

class DialogHelper {

    suspend fun createLoadDialog(activity: Activity, onButtonClick: () -> Unit) =
        withContext(Dispatchers.Main) {
            val builder = AlertDialog.Builder(activity)
            val root = activity.layoutInflater.inflate(R.layout.dialog_book_loading, null)
            val view = root.rootView
            val dialog = builder.create()
            view.btn_success.setOnClickListener {
                dialog.dismiss()
                onButtonClick()
            }
            view.btn_dism_dial_book_load.setOnClickListener {
                dialog.dismiss()
            }
            dialog.setView(view)
            dialog.setCancelable(true)
            dialog.show()
        }
}