package com.example.giuaky1.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Models.CartModel
import com.example.giuaky1.Models.ProductModel
import com.example.giuaky1.Ultils.MyCategory
import com.example.giuaky1.databinding.ProductInOrderItemBinding

class ProductInOrderItemAdapter(private val context: Context,
                                private var list:ArrayList<CartModel>)
    : RecyclerView.Adapter<ProductInOrderItemAdapter.viewholer>()  {
        lateinit var binding: ProductInOrderItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholer {
        binding = ProductInOrderItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewholer(binding.root)
    }

    override fun onBindViewHolder(holder: viewholer, position: Int) {

        val model =  list[position]
        holder.apply {
            stt.text = (position+1).toString()
            nameProduct.text = model.name
            size.text = model.size
            price.text = model.price.toString()
            quantity.text = model.quantity.toString()
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class viewholer(view: View): RecyclerView.ViewHolder(view){
        var stt =  binding.stt
        var nameProduct = binding.productName
        var size = binding.size
        var price = binding.price
        var quantity = binding.quantity
    }
}