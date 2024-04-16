package com.example.giuaky1.Firebase


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Adapters.MyCartAdapter
import com.example.giuaky1.Models.CartModel

import com.example.giuaky1.Models.Order
import com.example.giuaky1.Models.OrderModel
import com.example.giuaky1.Models.Order_product
import com.example.giuaky1.Models.ProductModel
import com.example.giuaky1.Models.Shipper

import com.example.giuaky1.Models.Users

import com.example.giuaky1.Models.SizeModel

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.util.Locale

class FirebaseFunction {

    companion object{
        var orderModelArrayList: ArrayList<CartModel> = ArrayList()
        fun getOMAL(): ArrayList<CartModel> {
            return orderModelArrayList
        }
        fun addToOrder(
            orderId: String?,
            orderTotalPrice: TextView,
            dateTime: String?,
            s: ArrayList<CartModel>,
            method:String
        ) {
            val ordersRef = FirebaseDatabase.getInstance().getReference("Order-confirm")
            val orderKey = ordersRef.push().key
            val orderModel = OrderModel()
            orderModel.orderId=orderId
            orderModel.totalPrice=orderTotalPrice.text.toString()
            orderModel.dateTime=dateTime
            orderModel.orderDetails=s
            Log.d("addToOrder", "orderId: $orderId, totalPrice: ${orderTotalPrice.text}, dateTime: $dateTime, orderDetails: $s")
            ordersRef.child(orderKey!!).setValue(orderModel)
            ordersRef.child(orderKey!!).child("method").setValue(method)
        }



        @SuppressLint("SuspiciousIndentation")
        fun getPhoneProfile(context: Context, callback: (String) -> Unit, callback2: (String) -> Unit, callback3: (String) -> Unit)  {
            val firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser!!
            val databaseReference = FirebaseDatabase.getInstance()
                .getReference("ProfileUser")
                .child(firebaseUser.uid)

                databaseReference
                .addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val phone:String = snapshot.child("phoneNumber").value.toString()
                        val location  = snapshot.child("location").value.toString()
                        val date = snapshot.child("dateOfBirth").value.toString()
                        callback(phone!!)
                        callback2(location!!)
                        callback3(date!!)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "false get data form getPhoneProfile", Toast.LENGTH_SHORT).show()

                    }
                })

        }




        fun getUserDataWithUid(uid : String,callback: (Users)->Unit){
            val ref = FirebaseDatabase
                .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users").child(uid)
            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(Users::class.java)
                    callback(user!!)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(Users())
                }
            })
        }
        fun getOrderDataWithOrderId(orderID:String,callback: (Order) -> Unit) {
            val ref = FirebaseDatabase
                .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Orders").child(orderID)
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(orderSnapshot: DataSnapshot) {
                        val state = orderSnapshot.child("state").value.toString()
                        val checkout = orderSnapshot.child("checkout").value.toString()
                        val uID = orderSnapshot.child("uID").value.toString()
                        val orderID = orderSnapshot.child("orderID").value.toString()
                        val pay = orderSnapshot.child("pay").value.toString()
                        val day = orderSnapshot.child("day").value.toString()
                        val time = orderSnapshot.child("time").value.toString()
                        val shipperName = orderSnapshot.child("shipper").child("name").value.toString()
                        val shipperSDT = orderSnapshot.child("shipper").child("sDT").value.toString()
                        val receiverPhone = orderSnapshot.child("receiverPhone").value.toString()
                        val receiverLocation = orderSnapshot.child("receiverLocation").value.toString()

                        val productsList = mutableListOf<Order_product>()
                        for (productSnapshot in orderSnapshot.child("products").children) {
                            val productName = productSnapshot.child("productName").value.toString()
                            val size = productSnapshot.child("size").value.toString()
                            val quantity = productSnapshot.child("quantity").value.toString()
                            val price = productSnapshot.child("price").value.toString()
                            val orderID = productSnapshot.child("orderID").value.toString()

                            val product = Order_product(productName, size, quantity, price, orderID)
                            productsList.add(product)
                        }
                        val shipper = Shipper(shipperName, shipperSDT)
                        val order = Order(state,checkout, uID, orderID, pay, day, time, shipper,receiverPhone,receiverLocation, productsList)
                        callback(order)
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
        }


        fun readOrdersCompleted(uid:String,callback: (List<Order>) -> Unit) {
            val ref = FirebaseDatabase
                .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Orders")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val orderList = mutableListOf<Order>()
                    for (orderSnapshot in snapshot.children) {
                        val state = orderSnapshot.child("state").value.toString()
                        val checkout = orderSnapshot.child("checkout").value.toString()
                        val uID = orderSnapshot.child("uID").value.toString()
                        val orderID = orderSnapshot.child("orderID").value.toString()
                        val pay = orderSnapshot.child("pay").value.toString()
                        val day = orderSnapshot.child("day").value.toString()
                        val time = orderSnapshot.child("time").value.toString()
                        val shipperName = orderSnapshot.child("shipper").child("name").value.toString()
                        val shipperSDT = orderSnapshot.child("shipper").child("sDT").value.toString()
                        val receiverPhone = orderSnapshot.child("receiverPhone").value.toString()
                        val receiverLocation = orderSnapshot.child("receiverLocation").value.toString()

                        val productsList = mutableListOf<Order_product>()
                        for (productSnapshot in orderSnapshot.child("products").children) {
                            val productName = productSnapshot.child("productName").value.toString()
                            val size = productSnapshot.child("size").value.toString()
                            val quantity = productSnapshot.child("quantity").value.toString()
                            val price = productSnapshot.child("price").value.toString()
                            val orderID = productSnapshot.child("orderID").value.toString()

                            val product = Order_product(productName, size, quantity, price, orderID)
                            productsList.add(product)
                        }
                        val shipper = Shipper(shipperName, shipperSDT)
                        val order = Order(state,checkout, uID, orderID, pay, day, time, shipper,receiverPhone,receiverLocation, productsList)

                        if(checkout.equals("1")){
                            if(uid.equals(uID) && state.equals("1")  ) orderList.add(order)
                        }
                    }
                    callback(orderList)
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
        fun readOrdersNotCompleted(uid:String,callback: (List<Order>) -> Unit) {
            val ref = FirebaseDatabase
                .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Orders")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val orderList = mutableListOf<Order>()
                    for (orderSnapshot in snapshot.children) {
                        val state = orderSnapshot.child("state").value.toString()
                        val checkout = orderSnapshot.child("checkout").value.toString()
                        val uID = orderSnapshot.child("uID").value.toString()
                        val orderID = orderSnapshot.child("orderID").value.toString()
                        val pay = orderSnapshot.child("pay").value.toString()
                        val day = orderSnapshot.child("day").value.toString()
                        val time = orderSnapshot.child("time").value.toString()
                        val shipperName = orderSnapshot.child("shipper").child("name").value.toString()
                        val shipperSDT = orderSnapshot.child("shipper").child("sDT").value.toString()
                        val receiverPhone = orderSnapshot.child("receiverPhone").value.toString()
                        val receiverLocation = orderSnapshot.child("receiverLocation").value.toString()

                        val productsList = mutableListOf<Order_product>()
                        for (productSnapshot in orderSnapshot.child("products").children) {
                            val productName = productSnapshot.child("productName").value.toString()
                            val size = productSnapshot.child("size").value.toString()
                            val quantity = productSnapshot.child("quantity").value.toString()
                            val price = productSnapshot.child("price").value.toString()
                            val orderID = productSnapshot.child("orderID").value.toString()

                            val product = Order_product(productName, size, quantity, price, orderID)
                            productsList.add(product)
                        }
                        val shipper = Shipper(shipperName, shipperSDT)
                        val order = Order(state,checkout, uID, orderID, pay, day, time, shipper,receiverPhone,receiverLocation, productsList)
                        System.out.println("checkout : "+checkout)

                        if(checkout.equals("1")){
                            if(uid.equals(uID) && state.equals("0")) orderList.add(order)
                        }
                    }
                    callback(orderList)
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        fun readAllOrdersList(callback: (List<Order>) -> Unit) {
            val ref = FirebaseDatabase
                .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Orders")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val orderList = mutableListOf<Order>()
                    for (orderSnapshot in snapshot.children) {
                        val state = orderSnapshot.child("state").value.toString()
                        val checkout = orderSnapshot.child("checkout").value.toString()
                        val uID = orderSnapshot.child("uID").value.toString()
                        val orderID = orderSnapshot.child("orderID").value.toString()
                        val pay = orderSnapshot.child("pay").value.toString()
                        val day = orderSnapshot.child("day").value.toString()
                        val time = orderSnapshot.child("time").value.toString()
                        val shipperName = orderSnapshot.child("shipper").child("name").value.toString()
                        val shipperSDT = orderSnapshot.child("shipper").child("sDT").value.toString()
                        val receiverPhone = orderSnapshot.child("receiverPhone").value.toString()
                        val receiverLocation = orderSnapshot.child("receiverLocation").value.toString()

                        val productsList = mutableListOf<Order_product>()
                        for (productSnapshot in orderSnapshot.child("products").children) {
                            val productName = productSnapshot.child("productName").value.toString()
                            val size = productSnapshot.child("size").value.toString()
                            val quantity = productSnapshot.child("quantity").value.toString()
                            val price = productSnapshot.child("price").value.toString()
                            val orderID = productSnapshot.child("orderID").value.toString()

                            val product = Order_product(productName, size, quantity, price, orderID)
                            productsList.add(product)
                        }
                        val shipper = Shipper(shipperName, shipperSDT)
                        val order = Order(state,checkout, uID, orderID, pay, day, time, shipper,receiverPhone,receiverLocation, productsList)
                        orderList.add(order)
                    }

                    callback(orderList)
                }
                override fun onCancelled(error: DatabaseError) {


                }
            })
        }

        fun addProduct(){
            val database = FirebaseDatabase.getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("Products")
            addP(database,"Cappuccino","https://i.imgur.com/HUNKiZu.png","Cappuccino",50000)
            addP(database,"Cappuccino1","https://i.imgur.com/HUNKiZu.png","Cappuccino1",55000)
            addP(database,"Espresso","https://i.imgur.com/tTmdWG0.png","Espresso",30000)
            addP(database,"Espresso1","https://i.imgur.com/tTmdWG0.png","Espresso1",20000)
            addP(database,"Macchiato","https://i.imgur.com/FKWnmUj.png","Macchiato",25000)
            addP(database,"Macchiato1","https://i.imgur.com/FKWnmUj.png","Macchiato1",20000)
            addP(database,"Mocha","https://i.imgur.com/89qkwtp.png","Mocha",45000)
            addP(database,"Mocha1","https://i.imgur.com/89qkwtp.png","Mocha1",45000)
        }

        private fun addP(
            db: DatabaseReference,
            tenSp: String,
            imageUrl: String,
            name: String,
            price: Int
        ) {
            val db1:DatabaseReference=db.child(tenSp)
            val productData = HashMap<String, Any>()
            productData["imageUrl"]=imageUrl
            productData["name"]=name
            productData["price"]=price
            db1.setValue(productData).addOnSuccessListener { Log.d("succesproduct","Upload thành công") }
        }
        fun addToCart(productModel: ProductModel, selectedSize: SizeModel){
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            val firebaseUser = auth.currentUser
            val id=firebaseUser?.uid ?: ""
            val productID = productModel.name + "_" + selectedSize.size // use both name and size to identify a unique product
            val cartReference = FirebaseDatabase.getInstance().getReference("Carts").child(id)
            cartReference.child(productID)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val cartModel: CartModel? = snapshot.getValue(CartModel::class.java)
                            cartModel?.let {
                                it.quantity = it.quantity + 1
                                val updateData: MutableMap<String, Any> = HashMap()
                                updateData["quantity"] = it.quantity
                                updateData["totalPrice"] = it.quantity * (it.price + selectedSize.price) // add the price of the size to the product price
                                updateData["sizePrice"] = selectedSize.price // update size price
                                cartReference.child(productID)
                                    .updateChildren(updateData)
                            }
                        } else {
                            val cartModel = CartModel(
                                productModel.name,
                                productModel.imageUrl,
                                1,
                                productModel.price + selectedSize.price, // add the price of the size to the product price
                                productModel.price + selectedSize.price, // add the price of the size to the product price
                                selectedSize.size, // pass the selected size here
                                selectedSize.price // pass the price of the selected size here
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
            llBuy: View,
            tvGiaTien: TextView,
            tvPhiGiaoHang: TextView,
            tvTotalPrice: TextView
        ) {
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            val firebaseUser = auth.currentUser
            val id = firebaseUser?.uid ?: ""
            val databaseReference = FirebaseDatabase.getInstance().getReference("Carts")
            databaseReference.child(id)
                .addValueEventListener(object : ValueEventListener {
                    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val cartModelArrayList = ArrayList<CartModel>()
                        if (dataSnapshot.exists()) {
                            for (snapshot in dataSnapshot.children) {
                                val cartModel = snapshot.getValue(CartModel::class.java)
                                cartModel?.let {
                                    cartModelArrayList.add(it)
                                }
                                orderModelArrayList = cartModelArrayList
                            }
                            llBuy.visibility = View.VISIBLE
                            recyclerView.visibility = View.VISIBLE
                            txtEmptyCart.visibility = View.GONE
                            val adapter = recyclerView.adapter as? MyCartAdapter
                            adapter?.updateData(cartModelArrayList)
                        } else {
                            recyclerView.visibility = View.GONE
                            txtEmptyCart.visibility = View.VISIBLE
                            llBuy.visibility = View.GONE
                        }
                        var totalPrice = 0.0
                        for (cartModel in cartModelArrayList) {
                            totalPrice += cartModel.totalPrice
                        }
                        val vndFormat = NumberFormat.getNumberInstance(Locale.getDefault())
                        tvGiaTien.text = "${vndFormat.format(totalPrice)}đ"
                        totalPrice+=tvPhiGiaoHang.text.toString().toDouble()
                        txtTotalPrice.text = "${vndFormat.format(totalPrice)}đ"
                        tvTotalPrice.text= "${vndFormat.format(totalPrice)}đ"
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("CartFragment", "onCancelled: ${databaseError.message}")
                    }
                })
        }
        fun getID(): String {
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                return firebaseUser.uid
            }
            return ""
        }

    }



}