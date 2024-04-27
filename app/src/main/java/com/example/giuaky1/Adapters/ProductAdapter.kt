package com.example.giuaky1.Adapters


import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giuaky1.Fragments.SizeDialogFragment
import com.example.giuaky1.Models.ProductModel
import com.example.giuaky1.R

import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(private var productList: List<ProductModel>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productName: TextView
        var productPrice: TextView
        var productImage: ImageView
        var productDiscountPrice: TextView

        init {
            productName = itemView.findViewById(R.id.textProductName)
            productPrice = itemView.findViewById(R.id.textProductPrice)
            productImage = itemView.findViewById(R.id.imageProduct)
            productDiscountPrice = itemView.findViewById(R.id.textProductDiscountPrice)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: ProductViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val product = productList[position]
        holder.productName.text = product.name
        val vndFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        if (product.discount > 0) {
            holder.productPrice.paintFlags =
                holder.productPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.productPrice.text = vndFormat.format(product.price.toLong()) + "đ"
            holder.productDiscountPrice.text =
                vndFormat.format(product.price * (1 - product.discount / 100.0)) + "đ"
            holder.productDiscountPrice.visibility = View.VISIBLE
        } else {
            holder.productPrice.paintFlags =
                holder.productPrice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.productPrice.text = vndFormat.format(product.price.toLong()) + "đ"
            holder.productDiscountPrice.visibility = View.GONE
        }
        Glide.with(holder.itemView.context)
            .load(product.imageUrl)
            .into(holder.productImage)
        holder.itemView.setOnClickListener { v: View ->
            val dialog = SizeDialogFragment.newInstance(
                productList[position]
            )
            dialog.show(
                (v.context as FragmentActivity).supportFragmentManager,
                "SizeDialogFragment"
            )
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun updateList(filteredList: List<ProductModel>) {
        productList = filteredList
    }
}