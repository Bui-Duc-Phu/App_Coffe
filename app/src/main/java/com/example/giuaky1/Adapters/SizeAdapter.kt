package com.example.giuaky1.Adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Models.SizeModel
import com.example.giuaky1.R

class SizeAdapter(private val sizeList: ArrayList<SizeModel>) :
    RecyclerView.Adapter<SizeAdapter.SizeViewHolder>() {

    private var selectedPosition = -1 // -1 indicates no selection

    inner class SizeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sizeName: TextView = itemView.findViewById(R.id.textSizeName)
        val sizePrice: TextView = itemView.findViewById(R.id.textSizePrice)
        val radioSize: RadioButton = itemView.findViewById(R.id.radioSize)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_size, parent, false)
        return SizeViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: SizeViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val size = sizeList[position]
        holder.sizeName.text = size.size
        holder.sizePrice.text = size.price.toString()

        // Uncheck all radio buttons first
        holder.radioSize.setOnCheckedChangeListener(null)
        holder.radioSize.isChecked = position == selectedPosition

        // Set checked change listener
        holder.radioSize.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedPosition = position
                notifyDataSetChanged()
            }
        }
    }
    override fun getItemCount(): Int {
        return sizeList.size
    }
}