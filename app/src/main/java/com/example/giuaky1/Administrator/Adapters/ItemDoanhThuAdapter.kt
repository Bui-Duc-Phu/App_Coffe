package com.example.giuaky1.Administrator.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Administrator.model.DoanhThu
import com.example.giuaky1.R

class ItemDoanhThuAdapter(private val doanhThuList: List<DoanhThu>) : RecyclerView.Adapter<ItemDoanhThuAdapter.DoanhThuViewHolder>() {

    class DoanhThuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvRevenue: TextView = itemView.findViewById(R.id.tvRevenue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoanhThuViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_doanh_thu, parent, false)
        return DoanhThuViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DoanhThuViewHolder, position: Int) {
        val currentItem = doanhThuList[position]
        Log.d("DoanhThu123456", currentItem.toString())
        holder.tvDate.text = currentItem.date
        holder.tvRevenue.text = currentItem.revenue.toString()
    }

    override fun getItemCount() = doanhThuList.size
}