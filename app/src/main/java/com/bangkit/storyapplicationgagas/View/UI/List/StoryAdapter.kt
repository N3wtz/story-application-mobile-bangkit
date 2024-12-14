package com.bangkit.storyapplicationgagas.View.UI.List

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.storyapplicationgagas.Data.Response.ListStoryItem
import com.bangkit.storyapplicationgagas.Utils.dateFormat
import com.bangkit.storyapplicationgagas.databinding.ListStoryBinding
import com.bumptech.glide.Glide

class StoryAdapter : ListAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    // Interface untuk menangani event klik pada item
    private lateinit var onItemClickCallBack: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem)
    }

    // Fungsi untuk mengatur callback klik item
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallBack = onItemClickCallback
    }

    // ViewHolder untuk mengelola tampilan item RecyclerView
    class MyViewHolder(private val binding: ListStoryBinding): RecyclerView.ViewHolder(binding.root) {
        // Fungsi untuk mengikat data ke tampilan
        fun bind(item: ListStoryItem) {
            // Memuat gambar menggunakan Glide
            Glide.with(itemView.context)
                .load(item.photoUrl)
                .into(binding.ivStory)

            // Menampilkan nama, deskripsi, dan waktu pembuatan cerita
            binding.tvNameStory.text = item.name
            binding.tvDescStory.text = item.description
            binding.tvCratedatStory.text = item.createdAt.dateFormat()
        }
    }

    // Membuat ViewHolder untuk RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflate layout XML menjadi objek ViewBinding
        val binding = ListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    // Menghubungkan data ke ViewHolder
    override fun onBindViewHolder(holder: MyViewHolder , position: Int) {
        val item = getItem(position) // Mengambil item berdasarkan posisi
        holder.bind(item) // Mengikat data ke ViewHolder

        // Menangani klik pada item
        holder.itemView.setOnClickListener {
            onItemClickCallBack.onItemClicked(getItem(holder.adapterPosition))
        }
    }

    // Callback untuk membandingkan item dalam daftar
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            // Memastikan apakah item yang dibandingkan sama
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem // Membandingkan objek
            }

            // Memastikan apakah konten dari item yang dibandingkan sama
            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem // Membandingkan data
            }
        }
    }
}