package com.example.giuaky1.Firebase

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Adapters.CartAdapter
import com.example.giuaky1.Administrator.Adapters.OrderListAdapter
import com.example.giuaky1.Administrator.model.DoanhThu
import com.example.giuaky1.Models.CartModel
import com.example.giuaky1.Models.Order
import com.example.giuaky1.Models.ProductModel
import com.example.giuaky1.Models.Shipper
import com.example.giuaky1.Models.SizeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DataHandler {
    var rule:String=""
    val shipper: Shipper = Shipper("Nguyễn Văn A", "0123456789")
    var orderModelArrayList = ArrayList<CartModel>()
    fun addOrderToFirebase(checkOut: String, orderId: String, paymentMethods1: String, dateTime: String, shipper: Shipper, phoneNumber: String, address: String, orderModelArrayList: ArrayList<CartModel>, totalPrice: String) {
        val ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(getUID())
        val orderModel = Order("Đang chờ xác nhận", checkOut, getUID(), orderId, paymentMethods1, dateTime, shipper, phoneNumber, address, orderModelArrayList, totalPrice)
        ordersRef.child(orderId).setValue(orderModel)
    }
    fun getOrderDetails(orderID: String, uID: String, callback: (Order) -> Unit) {
        val ref = FirebaseDatabase
            .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Orders").child(uID).child(orderID)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(orderSnapshot: DataSnapshot) {
                val state = orderSnapshot.child("state").value.toString()
                val checkout = orderSnapshot.child("checkout").value.toString()
                val uID = orderSnapshot.child("uID").value.toString()
                val orderID = orderSnapshot.child("orderID").value.toString()
                val pay = orderSnapshot.child("pay").value.toString()
                val time = orderSnapshot.child("time").value.toString()
                val shipperName = orderSnapshot.child("shipper").child("name").value.toString()
                val shipperSDT = orderSnapshot.child("shipper").child("sDT").value.toString()
                val receiverPhone = orderSnapshot.child("receiverPhone").value.toString()
                val receiverLocation = orderSnapshot.child("receiverLocation").value.toString()

                val productsList = mutableListOf<CartModel>()
                for (productSnapshot in orderSnapshot.child("products").children) {
                    val product=productSnapshot.getValue(CartModel::class.java)
                    if (product != null) {
                        productsList.add(product)
                    }
                }
                val shipper = Shipper(shipperName, shipperSDT)
                val sumPrice = orderSnapshot.child("sumPrice").value.toString()
                val order = Order(state,checkout, uID, orderID, pay, time, shipper,receiverPhone,receiverLocation, productsList, sumPrice)
                callback(order)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    fun readAllOrdersList(onDataReceived: (List<Order>) -> Unit) {
        val ordersRef = FirebaseDatabase.getInstance().getReference("Orders")
        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = ArrayList<Order>()
                for (orderSnapshot in snapshot.children) {
                    for (singleOrderSnapshot in orderSnapshot.children) {
                        val order = singleOrderSnapshot.getValue(Order::class.java)
                        if (order != null) {
                            orderList.add(order)
                        }
                    }
                }
                onDataReceived(orderList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeDoanhThu", "onCancelled: " + error.message)
            }
        })
    }
    fun getOrderWithState(state: String, callback: (List<Order>) -> Unit) {
        val ordersRef = FirebaseDatabase.getInstance().getReference("Orders")
        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = ArrayList<Order>()
                for (orderSnapshot in snapshot.children) {
                    for (singleOrderSnapshot in orderSnapshot.children) {
                        val order = singleOrderSnapshot.getValue(Order::class.java)
                        if (order != null && order.state == state) {
                            orderList.add(order)
                        }
                    }
                }
                callback(orderList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeDoanhThu", "onCancelled: " + error.message)
            }
        })
    }

    fun updateState(uID: String,orderID:String, state: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Orders").child(uID).child(orderID)
        ref.child("state").setValue(state)

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
    fun getDoanhThuTheoNgay(startDate: String, endDate: String, orderList: List<Order>): Double {
        var doanhThu = 0.0
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val start = sdf.parse("$startDate 00:00:00")
        val end = sdf.parse("$endDate 23:59:59")

        for (order in orderList) {
            val orderDate = sdf.parse(order.time)
            if (orderDate.after(start) && orderDate.before(end)) {
                doanhThu += order.sumPrice.toDouble()
            }
        }
        return doanhThu
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
    fun setTypeAccount(value:String) {
        rule=value
    }

    fun getListDoanhThuTheoNgay(startDate: String, endDate: String, listOrder: List<Order>): List<DoanhThu> {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val start = sdf.parse(startDate)
        val end = sdf.parse(endDate)
        val calendar = Calendar.getInstance()
        calendar.time = start
        val doanhThuList = mutableListOf<DoanhThu>()
        while (!calendar.time.after(end)) {
            val dateStr = sdf.format(calendar.time)
            val doanhThu = getDoanhThuTheoNgay(dateStr, dateStr, listOrder)
            doanhThuList.add(DoanhThu(dateStr, doanhThu))
            calendar.add(Calendar.DATE, 1)
        }

        return doanhThuList
    }
}
