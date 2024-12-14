package com.bangkit.storyapplicationgagas.View.UI.Landing

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.bangkit.storyapplicationgagas.View.UI.Login.LoginActivity
import com.bangkit.storyapplicationgagas.View.UI.SignUp.SignUpActivity
import com.bangkit.storyapplicationgagas.databinding.ActivityLandingBinding

class LandingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupActions()
        playAnimation()
    }

    private fun playAnimation() {
        // Animasi untuk imageView
        ObjectAnimator.ofFloat(binding.logo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 2500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        // Animasi untuk tombol dan teks
        val loginButtonAnim = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)
        val signupButtonAnim = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)
        val titleAnim = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val descAnim = ObjectAnimator.ofFloat(binding.descTextView, View.ALPHA, 1f).setDuration(100)

        // Memainkan animasi tombol secara bersamaan
        val buttonAnimations = AnimatorSet().apply {
            playTogether(loginButtonAnim, signupButtonAnim)
        }

        // Memainkan animasi judul dan deskripsi secara berurutan, kemudian tombol
        AnimatorSet().apply {
            playSequentially(titleAnim, descAnim, buttonAnimations)
            start()
        }
    }

    private fun setupView() {
        // Mengatur tampilan fullscreen berdasarkan versi SDK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupActions() {
        binding.loginButton.setOnClickListener {
            navigateToLogin()
        }

        binding.signupButton.setOnClickListener {
            navigateToSignUp()
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun navigateToSignUp() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }
}