package com.example.giuaky1.Adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Models.CategoryModel
import com.example.giuaky1.R

class CategoryAdapter(
    private val categoryList: MutableList<CategoryModel?>,
    private val listener: OnCategoryClickListener
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        if (category != null) {
            holder.bind(category, listener)
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategoryName: TextView

        init {
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName)
        }

        @SuppressLint("NotifyDataSetChanged")
        fun bind(category: CategoryModel, listener: OnCategoryClickListener) {
            tvCategoryName.text = category.name
            if (category == selectedCategory) {
                tvCategoryName.setTextColor(Color.RED) // Highlight color
            } else {
                tvCategoryName.setTextColor(Color.BLACK) // Normal color
            }
            itemView.setOnClickListener {
                selectedCategory = category
                listener.onCategoryClick(category)
                notifyDataSetChanged()
            }
        }
    }

    interface OnCategoryClickListener {
        fun onCategoryClick(category: CategoryModel?)
    }

    companion object {
        private var selectedCategory: CategoryModel? = null
    }
}