package com.bangkit.storyapplicationgagas.View.UI.SignUp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.bangkit.storyapplicationgagas.Data.Response.Response
import com.bangkit.storyapplicationgagas.R
import com.bangkit.storyapplicationgagas.View.UI.Login.LoginActivity
import com.bangkit.storyapplicationgagas.View.ViewModelFactory
import com.bangkit.storyapplicationgagas.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private val signUpViewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        signUpViewModel.UserResult.observe(this) { result ->
            when (result) {
                is Response.Loading -> {
                    showLoading(true)
                }
                is Response.Success -> {
                    showLoading(false)
                    navigateToLoginScreen()
                    showToast(getString(R.string.registration_successful))
                }
                is Response.Error -> {
                    showLoading(false)
                    showToast(getString(R.string.email_is_already_taken))
                }
            }
        }

        binding.signupButton.setOnClickListener {
            val username = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.eTextPassword.editText?.text.toString()
            signUpViewModel.register(username, email, password)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.overlayLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        val flags = if (isLoading) WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE else 0
        window.setFlags(flags, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
