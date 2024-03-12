package com.example.giuaky1.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Models.Order
import com.example.giuaky1.Ultils.CustomString
import com.example.giuaky1.databinding.OrderCustomItemBinding
class OrderCompletedAdapter(private val context: Context,
                            private var list: List<Order>,


)
    : RecyclerView.Adapter<OrderCompletedAdapter.viewholer>(){
        lateinit var binding: OrderCustomItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholer {
        binding = OrderCustomItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewholer(binding.root)
    }

    override fun onBindViewHolder(holder: viewholer, position: Int) {

        val model  = list[position]


        holder.id.text = "ID : #${model.orderID}"
        holder.pay.text = model.pay
        holder.date.text = "Ngày : ${model.day}"
        holder.time.text="Thời gian : ${model.time}"
        holder.shipper.text= CustomString.shipper(model.shipper.name,model.shipper.sDT)
        holder.sumPrice.text = "Tổng : 40,00000"
        val layoutManager = LinearLayoutManager(context)
        layoutManager.setInitialPrefetchItemCount(model.products.size)
        holder.recylerview.layoutManager = layoutManager
        val adapter =  ProductInOrderItemAdapter(context,model.products)
        holder.recylerview.adapter=adapter



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