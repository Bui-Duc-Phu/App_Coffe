package com.example.giuaky1.Adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.Models.CartModel
import com.example.giuaky1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.*

class MyCartAdapter(private val cartModelList: MutableList<CartModel>) :
    RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder>() {
    val id=FirebaseFunction.getID()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return MyCartViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {
        val cartModel = cartModelList[position]
        Picasso.get()
            .load(cartModel.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(holder.imageView)

        holder.txtName.text = cartModel.name
        val vndFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        holder.txtPrice.text = "${vndFormat.format(cartModel.price)}đ"
        holder.txtQuantity.text = cartModel.quantity.toString()
        holder.btnMinus.setOnClickListener {
            minusCartItem(holder, cartModelList[position])
        }
        holder.btnPlus.setOnClickListener {
            plusCartItem(holder, cartModelList[position])
        }
        holder.btnDel.setOnClickListener {
            deleteCartItem(holder.itemView.context, cartModelList, holder.adapterPosition)
        }
    }

    private fun plusCartItem(holder: MyCartViewHolder, cartModel: CartModel) {
        cartModel.quantity++
        cartModel.totalPrice = cartModel.quantity * cartModel.price
        holder.txtQuantity.text = cartModel.quantity.toString()
        updateFirebase(cartModel)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun minusCartItem(holder: MyCartViewHolder, cartModel: CartModel) {
        if (cartModel.quantity > 1) {
            cartModel.quantity--
            cartModel.totalPrice = cartModel.quantity * cartModel.price
            holder.txtQuantity.text = cartModel.quantity.toString()
            updateFirebase(cartModel)
        } else {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Xóa sản phẩm khỏi giỏ hàng")
            builder.setMessage("Bạn có chắc chắn muốn xóa sản phẩm khỏi giỏ hàng?")
            builder.setPositiveButton("Có") { dialog, _ ->
                deleteCartItem(holder.itemView.context, cartModelList, holder.adapterPosition)
                dialog.dismiss()
            }
            builder.setNegativeButton("Không") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
    }

    private fun deleteCartItem(context: Context, cartModelList: List<CartModel>, position: Int) {
        if (position >= 0 && position < cartModelList.size) {
            val cartModel = cartModelList[position]
            cartModel.name?.let {
                FirebaseDatabase.getInstance().getReference("Carts")
                    .child(id)
                    .child(it)
                    .removeValue()
            }
            notifyItemRemoved(position)
            cartModelList.toMutableList().removeAt(position)
        }
    }

    private fun updateFirebase(cartModel: CartModel) {
        cartModel.name?.let {
            FirebaseDatabase.getInstance().getReference("Carts")
                .child(id)
                .child(it)
                .setValue(cartModel)
        }
    }

    override fun getItemCount(): Int {
        return cartModelList.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: MutableList<CartModel>) {
        cartModelList.clear()
        cartModelList.addAll(newList)
        notifyDataSetChanged()
    }
    class MyCartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnMinus: ImageView = itemView.findViewById(R.id.btnMinus)
        val btnPlus: ImageView = itemView.findViewById(R.id.btnPlus)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)
        val txtQuantity: TextView = itemView.findViewById(R.id.txtQuantity)
        val btnDel: ImageView = itemView.findViewById(R.id.btnDelete)
    }
}
