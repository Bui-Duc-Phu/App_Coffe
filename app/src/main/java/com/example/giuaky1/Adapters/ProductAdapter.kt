package com.example.giuaky1.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Models.ProductModel
import com.example.giuaky1.R
import com.squareup.picasso.Picasso

class ProductAdapter(private val productList: List<ProductModel>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.textProductName)
        val productPrice: TextView = itemView.findViewById(R.id.textProductPrice)
        val productImage: ImageView = itemView.findViewById(R.id.imageProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productName.text = product.name
        holder.productPrice.text = product.price.toString()
        Picasso.get().load(product.imageUrl).into(holder.productImage)
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}
