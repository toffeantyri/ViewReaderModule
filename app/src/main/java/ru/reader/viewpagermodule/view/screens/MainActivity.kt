package ru.reader.viewpagermodule.view.screens

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.appbar.MaterialToolbar
import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.viewmodels.ViewModelMainActivity


class MainActivity : AppCompatActivity() {

    val viewModel: ViewModelMainActivity by viewModels()
    lateinit var navHostController: NavController
    @BindView(R.id.my_toolbar)
    lateinit var mToolbar : MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        navHostController = Navigation.findNavController(this@MainActivity, R.id.nav_host_main)


        setUpToolBar()




    }

    override fun onResume() {
        super.onResume()

    }

    protected fun setUpToolBar() {
        setSupportActionBar(mToolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }


}
