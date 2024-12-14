package com.bangkit.storyapplicationgagas.View.UI.DetailStory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.bangkit.storyapplicationgagas.Data.Response.Response
import com.bangkit.storyapplicationgagas.R
import com.bangkit.storyapplicationgagas.Utils.isInternetAvailable
import com.bangkit.storyapplicationgagas.View.UI.List.StoryViewModel
import com.bangkit.storyapplicationgagas.View.ViewModelFactory
import com.bangkit.storyapplicationgagas.databinding.ActivityDetailStoryBinding
import com.bumptech.glide.Glide


class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private val viewModel by viewModels<StoryViewModel> { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra("idUser").orEmpty()
        val storyName = intent.getStringExtra("name").orEmpty()
        val storyDesc = intent.getStringExtra("desc").orEmpty()
        val storyPhoto = intent.getStringExtra("photo").orEmpty()

        viewModel.detailStory(storyId)

        observeViewModel(storyName, storyDesc, storyPhoto)
        setupActionBar()
    }

    private fun observeViewModel(name: String, desc: String, photo: String) {
        viewModel.detailResult.observe(this) { result ->
            when (result) {
                is Response.Loading -> { /* Optionally handle loading state */ }
                is Response.Success -> {
                    setDetailStory(result.data.name, result.data.photoUrl, result.data.description)
                }
                is Response.Error -> {
                    if (!isInternetAvailable(this)) {
                        setDetailStory(name, photo, desc)
                    }
                    Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_SHORT).show()
                }
                else -> { /* Optionally handle other states */ }
            }
        }
    }

    private fun setDetailStory(name: String, img: String, desc: String) {
        binding.tvNameDetail.text = name
        binding.tvDescDetail.text = desc
        Glide.with(this)
            .load(img)
            .into(binding.ivDetail)
    }

    private fun setupActionBar() {
        binding.fabDetail.setOnClickListener {
            finish()
        }
    }
}