package com.dicoding.mysharestory.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.dicoding.mysharestory.databinding.ItemStoryBinding
import com.dicoding.mysharestory.model.Story
import com.dicoding.mysharestory.ui.story.ListStoryFragment

class ListStoryAdapter(
    private val fragment: ListStoryFragment
) : PagingDataAdapter<Story, ListStoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var _binding: ItemStoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = ItemStoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: ${getItem(position)}")
        val stories = getItem(position)
        if (stories!=null){
            holder.bind(stories, fragment)
        } else {
            Log.d(TAG, "onBindViewHolder: null")
        }
    }

    class ViewHolder(
        private val binding: ItemStoryBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(stories: Story, fragment: ListStoryFragment){
            with(binding){
                tvNameItem.text = stories.name
                Glide.with(fragment)
                    .asBitmap()
                    .fitCenter()
                    .transform(RoundedCorners(18))
                    .load(stories.photoUrl)
                    .into(ivImageStory)
                rvStory.setOnClickListener {
                    fragment.toDetail(stories)
                }
            }
        }
    }

    companion object{
        private val TAG = ListStoryAdapter::class.java.simpleName
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}