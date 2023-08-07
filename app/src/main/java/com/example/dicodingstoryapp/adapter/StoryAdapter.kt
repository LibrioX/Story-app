package com.example.dicodingstoryapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dicodingstoryapp.data.api.model.ListStoryItem
import com.example.dicodingstoryapp.databinding.ItemRowStoryBinding
import com.example.dicodingstoryapp.file.loadImage
import com.example.dicodingstoryapp.fragment.home.HomeFragmentDirections


class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.ListViewHolder>(DIFF_CALLBACK) {

    class ListViewHolder(val binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {

        val binding =
            ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)

    }


    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {

        val userStory = getItem(position) as ListStoryItem
        holder.binding.apply {
            tvItemName.text = userStory.name
            tvItemDescription.text = userStory.description
            tvItemPhoto.loadImage(userStory.photoUrl)
        }


        holder.itemView.setOnClickListener {
            val toDetailStory =
                HomeFragmentDirections.actionHomeFragmentToAddShowStoryFragment(userStory.id)
            it.findNavController().navigate(toDetailStory)
        }

    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ListStoryItem> =
            object : DiffUtil.ItemCallback<ListStoryItem>() {
                override fun areItemsTheSame(
                    oldUser: ListStoryItem,
                    newUser: ListStoryItem
                ): Boolean {
                    return oldUser.id == newUser.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldUser: ListStoryItem,
                    newUser: ListStoryItem
                ): Boolean {
                    return oldUser == newUser
                }
            }
    }
}