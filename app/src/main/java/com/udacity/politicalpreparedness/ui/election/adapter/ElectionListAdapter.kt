package com.udacity.politicalpreparedness.ui.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.politicalpreparedness.databinding.ElectionRecyItemBinding

class ElectionListAdapter(private val clickListener: ElectionListener) : ListAdapter<ElectionModel, ElectionViewHolder>(ElectionDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder.from(parent)
    }

    // Bind ViewHolder
    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        val election = getItem(position)
        holder.bind(election, clickListener)
    }

    object ElectionDiffCallback : DiffUtil.ItemCallback<ElectionModel>() {
        override fun areItemsTheSame(oldItem: ElectionModel, newItem: ElectionModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ElectionModel, newItem: ElectionModel): Boolean {
            return oldItem == newItem
        }
    }
}

// Create ElectionViewHolder
class ElectionViewHolder(private val binding: ElectionRecyItemBinding) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): ElectionViewHolder {
            return ElectionViewHolder(ElectionRecyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    fun bind(election: ElectionModel, clickListener: ElectionListener) {
        binding.election = election
        binding.clickListener = clickListener
        binding.executePendingBindings()
    }
}

// Create ElectionListener
class ElectionListener(val clickListener: (election: ElectionModel) -> Unit) {
    fun onClick(election: ElectionModel) = clickListener(election)
}
