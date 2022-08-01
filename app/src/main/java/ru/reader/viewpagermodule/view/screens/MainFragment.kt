package ru.reader.viewpagermodule.view.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.reader.viewpagermodule.APP_CONTEXT
import ru.reader.viewpagermodule.App
import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.data.busines.storage.BookListHelper
import ru.reader.viewpagermodule.util.UtilZip
import ru.reader.viewpagermodule.view.adapters.LoadBookData
import ru.reader.viewpagermodule.viewmodels.ViewModelMainActivity


const val REQUEST_CODE_PERMISSION = 1007

class MainFragment : Fragment() {

    private val viewModel: ViewModelMainActivity by activityViewModels()

    private lateinit var parentActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parentActivity = activity as MainActivity
        checkAndTakePermission()
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onResume() {
        super.onResume()
        btn_open_list.setOnClickListener {
            parentActivity.navHostController.navigate(R.id.action_mainFragment_to_listFragment)
            CoroutineScope(Dispatchers.IO).launch {
                //getFileAndUnzip()
                Log.d("MyLog", " MY PATH" + App.getMyPublicPath)
            }
        }

        button_test_load.setOnClickListener {
            Log.d("MyLog", "(View) test click btn load")
            val arrayList: List<String> = resources.getStringArray(R.array.array_url_bhagavad_gita).toList()
            val loadBook = LoadBookData("БхагавадГита1", "", arrayList.drop(1))
            viewModel.loadBookByUrl(
                loadBookData = loadBook,
                itemPosition = 0,
                onSuccess = {
                    Log.d("MyLog", "(View) test onSuccess")
                },
                onFail = {
                    Log.d("MyLog", "(View) test onFail")
                },
                onLoading = {
                    Log.d("MyLog", "(View) test onLoading")
                }
            )
        }
    }

    private fun getFileAndUnzip() {
        val bh = BookListHelper()
        val file = bh.getFileFromAssetsAndCache("bgzip.fb2.zip")

        file?.exists().let {
            if (file != null) {
                UtilZip.unzipFile(file, App.getDirDownloads.path)
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        @Suppress("DEPRECATION")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MyLog", "onRequest permission Granted")
                } else Log.d("MyLog", "onRequest permission Denied")
            }
        }
    }

    private fun checkAndTakePermission() {
        val permStatRead = ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.READ_EXTERNAL_STORAGE)
        val permStatWrite =
            ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permStatRead != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                parentActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION
            )
        } else {
            Log.d("MyLog", "MainFrag permission read Granted")
        }
        if (permStatWrite != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                parentActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION
            )
        } else {
            Log.d("MyLog", "MainFrag permission write Granted")
        }


    }
}
