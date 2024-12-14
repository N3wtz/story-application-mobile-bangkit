package com.bangkit.storyapplicationgagas.View.UI.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.bangkit.storyapplicationgagas.Data.Preference.Model
import com.bangkit.storyapplicationgagas.Data.Response.Response
import com.bangkit.storyapplicationgagas.R
import com.bangkit.storyapplicationgagas.Utils.isInternetAvailable
import com.bangkit.storyapplicationgagas.View.UI.MainActivity
import com.bangkit.storyapplicationgagas.View.ViewModelFactory
import com.bangkit.storyapplicationgagas.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        viewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        viewModel.loginResult.observe(this) { result ->
            when(result) {
                is Response.Loading -> {
                    binding.overlayLoading.visibility = View.VISIBLE
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                }
                is Response.Success -> {
                    binding.overlayLoading.visibility = View.GONE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    viewModel.saveSession(Model(result.data.loginResult.name, result.data.loginResult.token))
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                is Response.Error -> {
                    binding.overlayLoading.visibility = View.GONE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    if (!isInternetAvailable(this)) {
                        Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this,
                            getString(R.string.username_password_is_incorrect), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        binding.loginButton.setOnClickListener{
            val email = binding.emailEditText.text.toString()
            val password = binding.eTextPassword.editText?.text.toString()
            viewModel.login(email,password)
        }


    }
}