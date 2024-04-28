package com.example.giuaky1.Administrator.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giuaky1.Administrator.Activitys.EditCoffe
import com.example.giuaky1.Administrator.model.coffeModel
import com.example.giuaky1.R

class CoffeAdapter(private val context: Context, private val productList: MutableList<coffeModel?>) :
    RecyclerView.Adapter<CoffeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.coffe_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val product = productList[position]
    Glide.with(context).load(product!!.imageUrl).into(holder.imageProduct)
        if (product != null) {
            holder.nameProduct.text = "Tên : " + product.name
        }
    holder.category.text = "Category : " + product.category
    holder.priceProduct.text = "Giá : " + product.price
    var s = ""
    product.sizes?.let { sizes ->
        sizes["S"]?.let { size ->
            s += "Size S: ${size.price}\n"
        }
        sizes["M"]?.let { size ->
            s += "Size M: ${size.price}\n"
        }
        sizes["L"]?.let { size ->
            s += "Size L: ${size.price}\n"
        }
        sizes["XL"]?.let { size ->
            s += "Size XL: ${size.price}\n"
        }
    }
    holder.size.text = s
    holder.discount.text = "Discount : " + product.discount
    holder.itemView.setOnClickListener { v: View? ->
        val intent = Intent(context, EditCoffe::class.java)
        intent.putExtra("item", product)
        context.startActivity(intent)
    }
}

    override fun getItemCount(): Int {
        return productList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageProduct: ImageView
        var nameProduct: TextView
        var category: TextView
        var priceProduct: TextView
        var size: TextView
        var discount: TextView

        init {
            imageProduct = itemView.findViewById(R.id.image)
            nameProduct = itemView.findViewById(R.id.Name)
            category = itemView.findViewById(R.id.Category)
            priceProduct = itemView.findViewById(R.id.Price)
            size = itemView.findViewById(R.id.Size)
            discount = itemView.findViewById(R.id.Discount)
        }
    }
}