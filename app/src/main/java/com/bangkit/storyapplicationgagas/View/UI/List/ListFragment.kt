package com.bangkit.storyapplicationgagas.View.UI.List

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.storyapplicationgagas.Data.Response.ListStoryItem
import com.bangkit.storyapplicationgagas.View.UI.DetailStory.DetailStoryActivity
import com.bangkit.storyapplicationgagas.View.ViewModelFactory
import com.bangkit.storyapplicationgagas.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private val viewModel by viewModels<StoryViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private val adapter = StoryPagingAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        binding.rvList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvList.adapter = adapter

        // Setup SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadStories()
        }

        // Set item click callback
        adapter.setOnItemClickCallback(object : StoryPagingAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem) {
                // Pindah ke DetailStoryActivity
                val intent = Intent(requireContext(), DetailStoryActivity::class.java).apply {
                    putExtra("idUser", data.id)
                    putExtra("name", data.name)
                    putExtra("desc", data.description)
                    putExtra("photo", data.photoUrl)
                }
                startActivity(intent)
            }
        })

        // Load stories
        loadStories()
    }

    private fun loadStories() {
        binding.swipeRefreshLayout.isRefreshing = true

        viewModel.getStories().observe(viewLifecycleOwner) { pagingData ->
            binding.swipeRefreshLayout.isRefreshing = false
            adapter.submitData(lifecycle, pagingData)
        }
    }
}
