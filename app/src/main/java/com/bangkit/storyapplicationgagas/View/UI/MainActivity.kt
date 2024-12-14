package com.bangkit.storyapplicationgagas.View.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bangkit.storyapplicationgagas.R
import com.bangkit.storyapplicationgagas.View.UI.AddStory.AddActivity
import com.bangkit.storyapplicationgagas.View.UI.Landing.LandingActivity
import com.bangkit.storyapplicationgagas.View.UI.Login.LoginActivity
import com.bangkit.storyapplicationgagas.View.ViewModelFactory
import com.bangkit.storyapplicationgagas.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    // Menggunakan ViewBinding untuk menghubungkan layout XML dengan Activity
    private lateinit var binding: ActivityMainBinding

    // Mendapatkan instance ViewModel menggunakan ViewModelFactory
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    // Variabel untuk menyimpan nama pengguna
    private var nameUser = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayShowTitleEnabled(true)
        }

        // Mengatur BottomNavigationView untuk navigasi antar fragment
        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Menghubungkan BottomNavigationView dengan NavController
        navView.setupWithNavController(navController)

        // Menambahkan listener
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val fragmentTitle = when (destination.id) {
                R.id.userFragment -> nameUser
                R.id.listFragment -> getString(R.string.list_story)
                else -> getString(R.string.app_name)
            }

            supportActionBar?.title = fragmentTitle
        }

        //
        mainViewModel.getSession().observe(this) { user ->
            nameUser = user.username

            // Belum login ke LoginActivity
            if (user?.isLogin == false) {
                startActivity(Intent(this, LandingActivity::class.java))
                finish()
            }
        }
    }

    // Mengatur item menu toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.postActivity -> {
                startActivity(Intent(this, AddActivity::class.java))
                true
            }

            // Panggil implementasi bawaan
            else -> super.onOptionsItemSelected(item)
        }
    }
}