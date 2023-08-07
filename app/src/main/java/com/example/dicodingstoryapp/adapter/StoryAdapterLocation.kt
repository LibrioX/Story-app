package com.example.dicodingstoryapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dicodingstoryapp.data.api.model.ListStoryItem
import com.example.dicodingstoryapp.databinding.ItemRowStoryLocationBinding
import com.example.dicodingstoryapp.file.loadImage

class StoryAdapterLocation(
    private val storyList: List<ListStoryItem>,
    private val onClick: (ListStoryItem) -> Unit
) : ListAdapter<ListStoryItem, StoryAdapterLocation.ListViewHolder>(DIFF_CALLBACK) {

    class ListViewHolder(val binding: ItemRowStoryLocationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {

        val binding =
            ItemRowStoryLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)

    }

    override fun getItemCount() = storyList.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {

        val userStory = storyList[position]
        holder.binding.apply {
            titleStory.text = userStory.name
            descStory.text = userStory.description
            imgStory.loadImage(userStory.photoUrl)
            cardStory.setOnClickListener {
                onClick(userStory)
            }
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