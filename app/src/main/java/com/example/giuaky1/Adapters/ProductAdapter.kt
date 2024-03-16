package com.example.giuaky1.Adapters

import android.app.Dialog
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.Models.ProductModel
import com.example.giuaky1.R
import com.saadahmedsoft.popupdialog.PopupDialog
import com.saadahmedsoft.popupdialog.Styles
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener
import com.squareup.picasso.Picasso

class ProductAdapter(private val productList: List<ProductModel>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.textProductName)
        val productPrice: TextView = itemView.findViewById(R.id.textProductPrice)
        val productImage: ImageView = itemView.findViewById(R.id.imageProduct)
        val btn_add: Button = itemView.findViewById(R.id.btn_add_product_to_cart)
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
        holder.btn_add.setOnClickListener {
            FirebaseFunction.addToCart(productList[position])
            val dialog: PopupDialog = PopupDialog.getInstance(holder.itemView.context)
            dialog.setStyle(Styles.SUCCESS)
                .setHeading("Thành công")
                .setDescription("Thêm sản phẩm vào giỏ hàng thành công!")
                .showDialog(object : OnDialogButtonClickListener() {
                    override fun onDismissClicked(dialog: Dialog?) {
                        super.onDismissClicked(dialog)
                    }
                })
            Handler().postDelayed({ dialog.dismissDialog() }, 1500)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}
