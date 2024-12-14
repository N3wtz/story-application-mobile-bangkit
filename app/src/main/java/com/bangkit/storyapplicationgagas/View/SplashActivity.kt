package com.bangkit.storyapplicationgagas.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.lifecycleScope
import com.bangkit.storyapplicationgagas.Data.Preference.UserPreference
import com.bangkit.storyapplicationgagas.Data.Preference.dataStore
import com.bangkit.storyapplicationgagas.R
import com.bangkit.storyapplicationgagas.View.UI.Landing.LandingActivity
import com.bangkit.storyapplicationgagas.View.UI.Login.LoginActivity
import com.bangkit.storyapplicationgagas.View.UI.MainActivity
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DURATION: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Cek apakah pengguna sudah login
        val userPreference = UserPreference.getInstance(applicationContext.dataStore)

        // Menggunakan lifecycleScope untuk mengumpulkan data dari Flow
        lifecycleScope.launch {
            userPreference.getSession().collect { user ->
                val intent = if (user.isLogin) {
                    // Jika sudah login, langsung ke MainActivity (ListFragment)
                    Intent(this@SplashActivity, MainActivity::class.java)
                } else {
                    // Jika belum login, ke LoginActivity
                    Intent(this@SplashActivity, LandingActivity::class.java)
                }
                startActivity(intent)
                finish()
            }
        }
    }
}