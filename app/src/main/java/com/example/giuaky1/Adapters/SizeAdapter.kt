package com.example.giuaky1.Adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Models.SizeModel
import com.example.giuaky1.R

class SizeAdapter(private val sizeList: ArrayList<SizeModel>) : RecyclerView.Adapter<SizeAdapter.ViewHolder>() {

    private var selectedSize: SizeModel? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_size, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sizeModel = sizeList[position]
        holder.size.text = sizeModel.size
        holder.price.text = sizeModel.price.toString()

        holder.itemView.setOnClickListener {
            selectedSize = sizeModel
            notifyDataSetChanged()
        }

        if (selectedSize == sizeModel) {
            holder.itemView.setBackgroundColor(Color.GREEN)
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE)
        }
    }

    override fun getItemCount(): Int {
        return sizeList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val size: TextView = itemView.findViewById(R.id.textSizeName)
        val price: TextView = itemView.findViewById(R.id.textSizePrice)
    }

    fun getSelectedSize(): SizeModel? {
        return selectedSize
    }
}