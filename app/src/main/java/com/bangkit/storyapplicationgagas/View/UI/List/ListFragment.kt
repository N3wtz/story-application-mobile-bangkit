package com.bangkit.storyapplicationgagas.View.UI.List

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.storyapplicationgagas.Data.Response.ListStoryItem
import com.bangkit.storyapplicationgagas.R
import com.bangkit.storyapplicationgagas.Utils.isInternetAvailable
import com.bangkit.storyapplicationgagas.View.UI.DetailStory.DetailStoryActivity
import com.bangkit.storyapplicationgagas.View.ViewModelFactory
import com.bangkit.storyapplicationgagas.databinding.FragmentListBinding
import java.net.SocketTimeoutException

class ListFragment : Fragment(){

    private lateinit var binding: FragmentListBinding
    private val viewModel by viewModels<StoryViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private var adapter = StoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View {
        // Menginflate layout FragmentListBinding
        binding = FragmentListBinding.inflate(inflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        // Atut layout manager RecyclerView
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvList.layoutManager = layoutManager
        binding.rvList.adapter = adapter

        // Tampilkan daftar cerita di RecyclerView
        showRecyclerView()

        // Tambah listener  refresh layout
        binding.swipeRefreshLayout.setOnRefreshListener {
            showRecyclerView()
        }
    }

    private fun showRecyclerView() {
        // Menghentikan animasi refresh
        binding.swipeRefreshLayout.isRefreshing = false

        // Cek koneksi internet
        if (!isInternetAvailable(requireContext())) {
            Toast.makeText(requireContext(), "No internet connection.", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            viewModel.storyList().observe(viewLifecycleOwner) { story ->
                setListStory(story)
            }
        } catch (e: SocketTimeoutException) {
            // Menampilkan pesan jika terjadi timeout saat mengambil data
            Toast.makeText(requireContext(), getString(R.string.no_connection), Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            // Menampilkan pesan jika terjadi kesalahan lainnya
            Toast.makeText(requireContext(),
                getString(R.string.error), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setListStory(listStory: List<ListStoryItem>) {
        // Menyediakan daftar cerita ke adapter RecyclerView
        adapter.submitList(listStory)

        // Menambahkan callback saat item dalam daftar diklik
        adapter.setOnItemClickCallback(object :StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem) {
                // Mengirimkan data cerita yang dipilih ke DetailStoryActivity melalui Intent
                val intent = Intent(requireContext(), DetailStoryActivity::class.java)
                intent.putExtra("idUser", data.id)
                intent.putExtra("name", data.name)
                intent.putExtra("desc", data.description)
                intent.putExtra("photo", data.photoUrl)
                startActivity(intent)
            }
        })
    }
}
