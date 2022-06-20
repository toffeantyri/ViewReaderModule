package ru.reader.viewpagermodule.screens

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.viewmodels.ViewModelMainActivity


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
