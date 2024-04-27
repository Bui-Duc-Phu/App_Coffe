package com.example.giuaky1.Adapters


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giuaky1.Models.CartModel
import com.example.giuaky1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(private val cartModelList: MutableList<CartModel>) :
    RecyclerView.Adapter<CartAdapter.MyCartViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder {
        return MyCartViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {
        val cartModel = cartModelList[position]
        updateUI(holder, cartModel)
        setOnClickListeners(holder, position)
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(holder: MyCartViewHolder, cartModel: CartModel) {
        Glide.with(holder.itemView.context)
            .load(cartModel.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(holder.imageView)
        holder.txtName.text = cartModel.name
        holder.txtPrice.text =
            holder.itemView.context.getString(R.string.giatxtprice) + NumberFormat.getNumberInstance(
                Locale.getDefault()
            ).format(cartModel.price - cartModel.sizePrice) + "đ"
        holder.txtQuantity.text = cartModel.quantity.toString()
        holder.txtSize.text =
            "Size : " + cartModel.size + " (+" + NumberFormat.getNumberInstance(Locale.getDefault())
                .format(cartModel.sizePrice) + "đ)"
    }

    private fun setOnClickListeners(holder: MyCartViewHolder, position: Int) {
        holder.btnMinus.setOnClickListener { v: View? ->
            minusCartItem(
                holder,
                cartModelList[position]
            )
        }
        holder.btnPlus.setOnClickListener { v: View? ->
            plusCartItem(
                holder,
                cartModelList[position]
            )
        }
    }

    private fun plusCartItem(holder: MyCartViewHolder, cartModel: CartModel) {
        val position = cartModelList.indexOf(cartModel)
        if (position != -1) {
            cartModel.quantity = cartModel.quantity + 1
            cartModel.totalPrice = cartModel.quantity * cartModel.price
            holder.txtQuantity.text = StringBuffer().append(cartModel.quantity)
            updateFirebase(cartModel)
            notifyItemChanged(position)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun minusCartItem(holder: MyCartViewHolder, cartModel: CartModel) {
        if (cartModel.quantity > 1) {
            cartModel.quantity = cartModel.quantity - 1
            cartModel.totalPrice =
                cartModel.quantity * (cartModel.price + cartModel.sizePrice) // update total price
            holder.txtQuantity.text = cartModel.quantity.toString()
            updateFirebase(cartModel)
        } else {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle(R.string.del_product_cart)
            builder.setMessage(R.string.confirm_del_product_cart)
            builder.setPositiveButton(R.string.co) { dialog: DialogInterface, which: Int ->
                deleteCartItem(cartModelList, holder.getAdapterPosition())
                dialog.dismiss()
            }
            builder.setNegativeButton(R.string.no) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            builder.show()
        }
    }

    fun deleteCartItem(cartModelList: MutableList<CartModel>, position: Int) {
        if (position >= 0 && position < cartModelList.size) {
            val cartModel = cartModelList[position]
            val productID = cartModel.name + "_" + cartModel.size
            FirebaseDatabase.getInstance().getReference("Carts")
                .child(getID())
                .child(productID)
                .removeValue()
            cartModelList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun updateFirebase(cartModel: CartModel) {
        val productID = cartModel.name + "_" + cartModel.size
        FirebaseDatabase.getInstance().getReference("Carts")
            .child(getID())
            .child(productID)
            .setValue(cartModel)
    }

    override fun getItemCount(): Int {
        return cartModelList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<CartModel>?) {
        cartModelList.clear()
        cartModelList.addAll(newList!!)
        notifyDataSetChanged()
    }

    class MyCartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var btnMinus: ImageView
        var btnPlus: ImageView
        var imageView: ImageView
        var txtName: TextView
        var txtPrice: TextView
        var txtQuantity: TextView
        var txtSize: TextView

        init {
            btnMinus = itemView.findViewById(R.id.btnMinus)
            btnPlus = itemView.findViewById(R.id.btnPlus)
            imageView = itemView.findViewById(R.id.imageView)
            txtName = itemView.findViewById(R.id.txtName)
            txtPrice = itemView.findViewById(R.id.txtPrice)
            txtQuantity = itemView.findViewById(R.id.txtQuantity)
            txtSize = itemView.findViewById(R.id.txtSize)
        }
    }
    private fun getID(): String {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            return firebaseUser.uid
        }
        return ""
    }
}
