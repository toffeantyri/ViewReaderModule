package ru.reader.viewpagermodule

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.kursx.parser.fb2.FictionBook
import org.xml.sax.SAXException
import ru.reader.viewpagermodule.viewmodels.ViewModelMainActivity
import java.io.*
import javax.xml.parsers.ParserConfigurationException




class MainActivity : AppCompatActivity() {

    val viewModel: ViewModelMainActivity by viewModels()
    lateinit var navHostController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navHostController = Navigation.findNavController(this@MainActivity, R.id.nav_host_main)





    }

    override fun onResume() {
        super.onResume()

    }



}
