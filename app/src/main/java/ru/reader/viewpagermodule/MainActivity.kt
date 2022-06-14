package ru.reader.viewpagermodule

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import io.hamed.htepubreadr.component.EpubReaderComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.reader.viewpagermodule.viewmodels.ViewModelMainActivity
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


const val REQUEST_CODE_PERMISSION = 1007

class MainActivity : AppCompatActivity() {

    val viewModel: ViewModelMainActivity by viewModels()
    lateinit var navHostController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navHostController = Navigation.findNavController(this@MainActivity, R.id.nav_host_main)
        checkAndTakePermision()



            val file =  getFileFromAssets("bhagavad_gita.epub").absoluteFile
            Log.d("MyLog", " name ${file.name} + dir : ${file.path}")

//            try {
//                Log.d("MyLog", "try block ${file.exists()}")
//                val epubReader = EpubReaderComponent(file.absolutePath)
//                val bookEntity = epubReader.make(this@MainActivity)
//                Log.d("MyLog", "${bookEntity.author} \n ${bookEntity.name}")
//            } catch (ex: Exception) {
//                throw ex
//            }



    }

    override fun onResume() {
        super.onResume()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MyLog", "MainActivity permission Granted")
                } else Log.d("MyLog", "MainActivity permission Denied")
            }
        }
    }

    private fun checkAndTakePermision() {
        val permStatRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val permStatWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permStatRead != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION
            )
        }
        if (permStatWrite != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION
            )
        }


    }


    private fun getFileFromAssets(fileName: String): File {
        val file = File(this.cacheDir.toString() + "/" + fileName)
        //Log.d("MyLog", ": filePath $file")
        if (!file.exists()) try {
            val inputStream: InputStream = this.assets.open(fileName)
            val size = inputStream.available()
            Log.d("MyLog", ": size $size")
            val buffer = byteArrayOf()
            inputStream.read(buffer)
            inputStream.close()
            val fileOutput: FileOutputStream = FileOutputStream(file)
            fileOutput.write(buffer)
            fileOutput.close()

        } catch (e: java.lang.Exception) {
            throw RuntimeException(e)
        }
        Log.d("MyLog", ": name  ${file.name} \n ${file.path}")
        return file
    }
}
