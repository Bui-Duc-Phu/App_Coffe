package com.example.giuaky1.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Models.Order
import com.example.giuaky1.R
import com.example.giuaky1.Ultils.CustomString
import com.example.giuaky1.Ultils.MyCategory
import com.example.giuaky1.databinding.OrderNotCustomItemBinding
import kotlin.math.roundToInt

class OrderNotCompletedAdapter(private val context: Context,
                               private var list: List<Order>)
    : RecyclerView.Adapter<OrderNotCompletedAdapter.viewholer>()  {
        lateinit var binding: OrderNotCustomItemBinding
       

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholer {
        binding = OrderNotCustomItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return viewholer(binding.root)
    }

    override fun onBindViewHolder(holder: viewholer, position: Int) {
        holder.apply {
            val model  = list[position]

            if (position == 0) {
                val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
                layoutParams.topMargin = 100.dpToPx(holder.itemView.context)
                holder.itemView.layoutParams = layoutParams
            }
            id.text = "ID : #${model.orderID}"
            pay.text = model.pay
            time.text= context.getString(R.string.time) + " ${model.time}"
            shipper.text= CustomString.shipper(model.shipper.name,model.shipper.sDT)
            sumPrice.text =context.getString(R.string.sum)+" 1000"

            val layoutManager = LinearLayoutManager(context)
            layoutManager.setInitialPrefetchItemCount(model.products.size)
            holder.recylerview.layoutManager = layoutManager
            val adapter =  ProductInOrderItemAdapter(context,model.products)
            holder.recylerview.adapter=adapter
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }
    private fun Int.dpToPx(context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (this * density).roundToInt()
    }

    inner class viewholer(view: View): RecyclerView.ViewHolder(view){
        var id = binding.IDTv
        var pay  = binding.payTv
        var time = binding.timeTv
        var shipper = binding.shiperTv
        var sumPrice = binding.sumPrice
        var recylerview = binding.productRecylerview
    }
}