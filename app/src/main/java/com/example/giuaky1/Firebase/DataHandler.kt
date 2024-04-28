package com.example.giuaky1.Firebase

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Adapters.CartAdapter
import com.example.giuaky1.Models.CartModel
import com.example.giuaky1.Models.Order
import com.example.giuaky1.Models.OrderModel
import com.example.giuaky1.Models.ProductModel
import com.example.giuaky1.Models.Shipper
import com.example.giuaky1.Models.SizeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.util.Locale

object DataHandler {
    val shipper: Shipper = Shipper("Nguyễn Văn A", "0123456789")
    var orderModelArrayList = ArrayList<CartModel>()
    fun addOrderToFirebase(checkOut: String, orderId: String, paymentMethods1: String, dateTime: String, shipper: Shipper, phoneNumber: String, address: String, orderModelArrayList: ArrayList<CartModel>, totalPrice: String) {
        val ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(getUID())
        val orderModel = Order("Đang xử lý", checkOut, getUID(), orderId, paymentMethods1, dateTime, shipper, phoneNumber, address, orderModelArrayList, totalPrice)
        ordersRef.child(orderId).setValue(orderModel)
    }

    fun addToCart(productModel: ProductModel, selectedSize: SizeModel, quantity: Int) {
        val productID = productModel.name + "_" + selectedSize.size
        val cartReference =
            FirebaseDatabase.getInstance().getReference("Carts").child(getUID())
        cartReference.child(productID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val finalPrice =
                        if (productModel.discount > 0) productModel.finalPrice else productModel.price
                            .toDouble()
                    if (snapshot.exists()) {
                        val cartModel = snapshot.getValue(CartModel::class.java)
                        if (cartModel != null) {
                            cartModel.quantity += quantity
                            val updateData: MutableMap<String, Any> = HashMap()
                            updateData["quantity"] = cartModel.quantity
                            updateData["totalPrice"] =
                                cartModel.quantity * (finalPrice + selectedSize.price)
                            updateData["sizePrice"] = selectedSize.price
                            cartReference.child(productID)
                                .updateChildren(updateData)
                        }
                    } else {
                        val cartModel = CartModel(
                            productModel.name,
                            productModel.imageUrl,
                            quantity,
                            finalPrice + selectedSize.price,
                            finalPrice + selectedSize.price,
                            selectedSize.size,
                            selectedSize.price
                        )
                        cartReference.child(productID)
                            .setValue(cartModel)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("CartHandler", "addToCart onCancelled: " + error.message)
                }
            })
    }

    fun fetchDataForCart(
        recyclerView: RecyclerView,
        txtEmptyCart: TextView,
        txtTotalPrice: TextView,
        llBuy: LinearLayout
    ) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Carts")
        databaseReference.child(getUID())
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val cartModelArrayList = ArrayList<CartModel>()
                    if (dataSnapshot.exists()) {
                        for (snapshot in dataSnapshot.getChildren()) {
                            val cartModel = snapshot.getValue(CartModel::class.java)
                            if (cartModel != null) {
                                cartModelArrayList.add(cartModel)
                            }
                            orderModelArrayList = cartModelArrayList
                        }
                        recyclerView.visibility = View.VISIBLE
                        txtEmptyCart.visibility = View.GONE
                        llBuy.visibility = View.VISIBLE
                        val adapter = recyclerView.adapter as CartAdapter?
                        adapter?.updateData(cartModelArrayList)
                    } else {
                        recyclerView.visibility = View.GONE
                        llBuy.visibility = View.GONE
                        txtEmptyCart.visibility = View.VISIBLE
                    }
                    var totalPrice = 0.0
                    for (cartModel in cartModelArrayList) {
                        totalPrice += cartModel.totalPrice
                    }
                    val vndFormat = NumberFormat.getNumberInstance(Locale.getDefault())
                    txtTotalPrice.text = vndFormat.format(totalPrice) + "đ"
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("CartFragment", "onCancelled: " + databaseError.message)
                }
            })
    }

    fun fetchDataForOrder(
        recyclerView: RecyclerView,
        txtTotalPrice: TextView
    ) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Carts")
        databaseReference.child(getUID())
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val cartModelArrayList = ArrayList<CartModel>()
                    for (snapshot in dataSnapshot.getChildren()) {
                        val cartModel = snapshot.getValue(CartModel::class.java)
                        if (cartModel != null) {
                            cartModelArrayList.add(cartModel)
                        }
                        orderModelArrayList = cartModelArrayList
                    }
                    val adapter = recyclerView.adapter as CartAdapter?
                    adapter?.updateData(cartModelArrayList)
                    var totalPrice = 0.0
                    for (cartModel in cartModelArrayList) {
                        totalPrice += cartModel.totalPrice
                    }
                    val vndFormat = NumberFormat.getNumberInstance(Locale.getDefault())
                    totalPrice += 20000.0
                    txtTotalPrice.text = vndFormat.format(totalPrice)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("CartFragment", "onCancelled: " + databaseError.message)
                }
            })
    }
    fun getUID(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }
    fun getName(): String {
        return FirebaseAuth.getInstance().currentUser?.displayName ?: ""
    }

    fun getPhoneNumber(): String {
        return FirebaseAuth.getInstance().currentUser?.phoneNumber ?: ""
    }

    fun getOMAL(): ArrayList<CartModel> {
        return orderModelArrayList
    }
}
