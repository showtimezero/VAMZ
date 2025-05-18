package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WeightAdapter(private val entries: List<WeightEntry>) :
    RecyclerView.Adapter<WeightAdapter.WeightViewHolder>() {

    class WeightViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
        return WeightViewHolder(textView)
    }

    override fun onBindViewHolder(holder: WeightViewHolder, position: Int) {
        val entry = entries[position]
        holder.textView.text = "${entry.date}: ${entry.weight} kg"
    }

    override fun getItemCount() = entries.size
}
