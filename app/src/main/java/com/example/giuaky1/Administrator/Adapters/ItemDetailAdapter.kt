package com.example.giuaky1.Administrator.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Models.CartModel
import com.example.giuaky1.R

class ItemDetailAdapter(private val context: Context, private var list: List<CartModel>)
    : RecyclerView.Adapter<ItemDetailAdapter.ItemDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemDetailViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_detail_order_admin, parent, false)
        return ItemDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemDetailViewHolder, position: Int) {
        val model = list[position]
        holder.apply {
            stt.text = (position + 1).toString()
            productName.text = model.name
            size.text = model.size
            quantity.text = model.quantity.toString()
            price.text = model.price.toString()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ItemDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stt: TextView = view.findViewById(R.id.stt)
        val productName: TextView = view.findViewById(R.id.productName)
        val size: TextView = view.findViewById(R.id.size)
        val quantity: TextView = view.findViewById(R.id.quantity)
        val price: TextView = view.findViewById(R.id.price)
    }
}