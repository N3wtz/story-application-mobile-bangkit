package com.bangkit.storyapplicationgagas.View.UI.User

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.storyapplicationgagas.Data.Response.ListStoryItem
import com.bangkit.storyapplicationgagas.databinding.UserStoryItemBinding
import com.bumptech.glide.Glide

class UserAdapter : ListAdapter<ListStoryItem, UserAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private var onItemClickCallBack: OnItemClickCallback? = null

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallBack = onItemClickCallback
    }

    class MyViewHolder(private val binding: UserStoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListStoryItem) {
            Glide.with(itemView.context)
                .load(item.photoUrl)
                .into(binding.ivStory)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = UserStoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)

        holder.itemView.setOnClickListener {
            onItemClickCallBack?.onItemClicked(getItem(holder.adapterPosition))
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id // Membandingkan id sebagai penanda unik
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}