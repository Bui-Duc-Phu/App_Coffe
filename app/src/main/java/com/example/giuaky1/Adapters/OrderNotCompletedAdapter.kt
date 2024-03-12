package com.example.giuaky1.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Models.Order
import com.example.giuaky1.Models.Users
import com.example.giuaky1.Ultils.CustomString
import com.example.giuaky1.databinding.FragmentHistoryBinding
import com.example.giuaky1.databinding.OrderCustomItemBinding
import com.example.giuaky1.databinding.OrderNotCustomItemBinding

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
            id.text = "ID : #${model.orderID}"
            pay.text = model.pay
            date.text = "Ngày : ${model.day}"
            time.text="Thời gian : ${model.time}"
            shipper.text= CustomString.shipper(model.shipper.name,model.shipper.sDT)
            sumPrice.text = "Tổng : 40,00000"

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

    inner class viewholer(view: View): RecyclerView.ViewHolder(view){
        var id = binding.IDTv
        var pay  = binding.payTv
        var date = binding.dateTv
        var time = binding.timeTv
        var shipper = binding.shiperTv
        var sumPrice = binding.sumPrice
        var recylerview = binding.productRecylerview
    }
}