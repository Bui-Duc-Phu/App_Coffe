package com.example.giuaky1.Administrator.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Administrator.model.DoanhThu
import com.example.giuaky1.Administrator.model.DonHang
import com.example.giuaky1.Administrator.model.SanPham
import com.example.giuaky1.R

class ItemSanPhamAdapter(private val sanPhamList: List<SanPham>) : RecyclerView.Adapter<ItemSanPhamAdapter.DoanhThuViewHolder>() {

    class DoanhThuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoanhThuViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_san_pham, parent, false)
        return DoanhThuViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DoanhThuViewHolder, position: Int) {
        val currentItem = sanPhamList[position]
        holder.tvDate.text = currentItem.date
        holder.tvQuantity.text = currentItem.quantity.toString()
        holder.tvName.text = currentItem.name
    }

    override fun getItemCount() = sanPhamList.size
}