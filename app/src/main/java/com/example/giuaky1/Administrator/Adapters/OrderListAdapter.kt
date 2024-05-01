package com.example.giuaky1.Administrator.Adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Administrator.Activitys.DetailOrder
import com.example.giuaky1.Firebase.DataHandler
import com.example.giuaky1.Models.CartModel
import com.example.giuaky1.Models.Order
import com.example.giuaky1.R

class OrderListAdapter(private var orderList: List<Order>) : RecyclerView.Adapter<OrderListAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_list, parent, false)
        return OrderViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.orderIdTextView.text = "ID : ${order.orderID}"
        holder.statusTextView.text = order.state
        holder.dateAndTimeTextView.text = order.time
        if (order.state == "Đang chờ xác nhận") {
            holder.changeStateButton.text = "Xác nhận"
        } else if (order.state == "Đang giao hàng") {
            holder.changeStateButton.text = "Giao hàng"
            holder.cancelButton.visibility = View.GONE
        } else {
            holder.changeStateButton.visibility = View.GONE
            holder.cancelButton.visibility = View.GONE
        }
        holder.changeStateButton.setOnClickListener {
            when (holder.changeStateButton.text) {
                "Xác nhận" -> {
                    DataHandler.updateState(order.uID, order.orderID, "Đang giao hàng")
                }
                "Giao hàng" -> {
                    DataHandler.updateState(order.uID, order.orderID, "Đã giao hàng")
                }
            }
        }
        holder.cancelButton.setOnClickListener {
            DataHandler.updateState(order.uID, order.orderID, "Đã hủy")
        }
        holder.detailsButton.setOnClickListener {
            DataHandler.getOrderDetails(order.orderID, order.uID) { orderDetail ->
                val intent = Intent(it.context, DetailOrder::class.java)
                intent.putExtra("orderDetail", orderDetail)
                it.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newOrderList: List<Order>) {
        orderList = newOrderList
        notifyDataSetChanged()
    }

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderIdTextView: TextView = view.findViewById(R.id.orderID)
        val statusTextView: TextView = view.findViewById(R.id.orderStatus)
        val dateAndTimeTextView: TextView = view.findViewById(R.id.dateAndTime)
        val changeStateButton: Button = view.findViewById(R.id.changeStateButton)
        val detailsButton: Button = view.findViewById(R.id.detailsButton)
        val cancelButton: Button = view.findViewById(R.id.cancelButton)

    }
}