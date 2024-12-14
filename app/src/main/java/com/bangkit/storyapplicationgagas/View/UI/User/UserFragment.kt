package com.bangkit.storyapplicationgagas.View.UI.User

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bangkit.storyapplicationgagas.Data.Response.ListStoryItem
import com.bangkit.storyapplicationgagas.Data.Response.Response
import com.bangkit.storyapplicationgagas.R
import com.bangkit.storyapplicationgagas.View.UI.DetailStory.DetailStoryActivity
import com.bangkit.storyapplicationgagas.View.UI.Landing.LandingActivity
import com.bangkit.storyapplicationgagas.View.UI.Login.LoginViewModel
import com.bangkit.storyapplicationgagas.View.ViewModelFactory
import com.bangkit.storyapplicationgagas.databinding.FragmentUserBinding


class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private val viewModel by viewModels<UserViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val userViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private var adapter = UserAdapter()

    private var nameUser = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setMessage(getString(R.string.logout_confirm))
            builder.setPositiveButton("Yes") { dialog, _ ->
                // Logout user
                userViewModel.logout()

                // Redirect to LandingActivity
                val intent = Intent(requireContext(), LandingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                dialog.dismiss()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }

        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvMyList.layoutManager = layoutManager
        binding.rvMyList.adapter = adapter

        userViewModel.getSession().observe(requireActivity()) {
            nameUser = it.username
        }

        viewModel.getMyStory(nameUser).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Response.Loading -> {
                    binding.pbMyList.visibility = View.VISIBLE
                }
                is Response.Success -> {
                    binding.pbMyList.visibility = View.GONE

                    if (result.data.isNotEmpty()) {
                        binding.tvNoData.visibility = View.GONE
                        setListMyStory(result.data)
                    } else {
                        binding.tvNoData.visibility = View.VISIBLE
                    }
                }
                is Response.Error -> {
                    binding.pbMyList.visibility = View.GONE
                }
                else -> {}
            }
        }
    }

    private fun setListMyStory(listStory: List<ListStoryItem>) {
        adapter.submitList(listStory)

        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem) {
                val intent = Intent(requireContext(), DetailStoryActivity::class.java)
                intent.putExtra("idUser", data.id)
                startActivity(intent)
            }
        })
    }
}