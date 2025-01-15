package com.example.farmcontractor.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.farmcontractor.databinding.RecyclerCardWorkViewBinding

class WorkAdapter(private val dataset: MutableList<String>, private val onItemClick: (String) -> Unit, private val onItemLongClick: (String) -> Boolean): RecyclerView.Adapter<WorkAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: RecyclerCardWorkViewBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClick(dataset[adapterPosition])
            }
            binding.root.setOnLongClickListener {
                onItemLongClick(dataset[adapterPosition])
                return@setOnLongClickListener true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerCardWorkViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textViewName.text = dataset[position]
    }
}