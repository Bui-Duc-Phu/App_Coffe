package com.example.giuaky1.Adapters

import android.content.Context
import android.provider.ContactsContract.Data
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Firebase.DataHandler
import com.example.giuaky1.Models.Order
import com.example.giuaky1.R
import com.example.giuaky1.Ultils.CustomString
import com.example.giuaky1.Ultils.MyCategory
import com.example.giuaky1.databinding.OrderCustomItemBinding
import kotlin.math.roundToInt

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

        if (position == 0) {
            val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
            layoutParams.topMargin = 100.dpToPx(holder.itemView.context)
            holder.itemView.layoutParams = layoutParams
        }


        holder.id.text = model.uID
        holder.pay.text = model.pay
        holder.time.text = model.time
        holder.shipper.text = model.shipper.toString()
        holder.sumPrice.text = "1000"

        val layoutManager = LinearLayoutManager(context)
        layoutManager.setInitialPrefetchItemCount(model.products.size)
        holder.recylerview.layoutManager = layoutManager
        val adapter =  ProductInOrderItemAdapter(context, DataHandler.getOMAL())
        holder.recylerview.adapter=adapter



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
        var date = binding.dateTv
        var time = binding.timeTv
        var shipper = binding.shiperTv
        var sumPrice = binding.sumPrice
        var recylerview = binding.productRecylerview
    }
}