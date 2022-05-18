package com.lumstep.rickandmorty

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lumstep.rickandmorty.databinding.ItemLoadingStateBinding

class LoadStateAdapterForRecycler(private val retry: () -> Unit) :
    LoadStateAdapter<LoadStateAdapterForRecycler.LoadStateViewHolder>() {
    inner class LoadStateViewHolder(
        private val binding: ItemLoadingStateBinding,
        private val retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.loadStateErrorText.text = loadState.error.localizedMessage
            }
            binding.loadStateProgressbar.isVisible = (loadState is LoadState.Loading)
            binding.loadStateRetryButton.isVisible = (loadState is LoadState.Error)
            binding.loadStateErrorText.isVisible = (loadState is LoadState.Error)
            binding.loadStateErrorImage.isVisible = (loadState is LoadState.Error)
            binding.loadStateRetryButton.setOnClickListener {
                retry()
            }
        }
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ) = LoadStateViewHolder(
        ItemLoadingStateBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        retry
    )
}