package com.example.farmcontractor.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.farmcontractor.Structs.Contract
import com.example.farmcontractor.databinding.RecyclerCardContractsViewBinding

class ContractsAdapter(private val dataset: MutableList<Contract>, private val onItemClick: (String) -> Unit, private val onItemLongClick: (String) -> Boolean): RecyclerView.Adapter<ContractsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: RecyclerCardContractsViewBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClick(dataset[adapterPosition].id.oid)
            }
            binding.root.setOnLongClickListener {
                onItemLongClick(dataset[adapterPosition].id.oid)
                return@setOnLongClickListener true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerCardContractsViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textViewFarmer.text = "${dataset[position].farmer} - ${dataset[position].work}"
        holder.binding.textViewCooridinates.text = "Coordinates: %.6f %.6f".format(dataset[position].lat, dataset[position].lng)
        holder.binding.textViewActive.text = if (dataset[position].active) "Active" else "Finished"
    }
}