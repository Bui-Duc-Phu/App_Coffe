package com.example.giuaky1.Firebase


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.giuaky1.Models.CartModel

import com.example.giuaky1.Models.Order
import com.example.giuaky1.Models.OrderModel
import com.example.giuaky1.Models.Order_product
import com.example.giuaky1.Models.Shipper

import com.example.giuaky1.Models.Users

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

        fun getPasswrodWithUid(context: Context,callback: (String) -> Unit){

            val firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser!!
            val ref = FirebaseDatabase
                .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users").child(firebaseUser.uid.toString())

            ref.addListenerForSingleValueEvent(object  : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val password  = snapshot.child("password").value.toString()
                    if(password.isNotEmpty()) callback(password)
                    else callback("isNotEmpty05012002")
                }

                override fun onCancelled(error: DatabaseError) {


                }
            })
        }


        fun phoneAlreadyExists(context: Context,phoneUser:String, callback: (Boolean) -> Unit){
            val databaseReference = FirebaseDatabase.getInstance()
                .getReference("ProfileUser")
            databaseReference.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var cnt = false
                    for (profileSnapshot in snapshot.children){
                        val phone = profileSnapshot.child("phoneNumber").getValue(String::class.java)
                        if(phone != null && phone.isNotEmpty()){
                            if(phoneUser.equals(phone)){
                                cnt=true
                                break // Thêm break để dừng vòng lặp khi đã tìm thấy số điện thoại
                            }
                        }
                    }
                    callback(cnt)
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "bug on fun phoneAlreadyExists ", Toast.LENGTH_SHORT).show()
                }
            })
        }

        fun getUidWithPhone(phone_:String,callback: (String) -> Unit){
            val databaseReference = FirebaseDatabase.getInstance()
                .getReference("ProfileUser")
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(profileSnapshot in snapshot.children){
                        val phone = profileSnapshot.child("phoneNumber").getValue(String::class.java)
                        val uid= profileSnapshot.key.toString()
                        if(phone != null && phone.isNotEmpty()){
                            if(phone_.equals(phone)){
                                callback(uid)

                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

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
                        callback(phone)
                        callback2(location)
                        callback3(date)
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
                        val order = Order(state,checkout, uID, orderID, pay, time, shipper,receiverPhone,receiverLocation, DataHandler.getOMAL(), "1000")

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
                .getInstance()
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
                        val order = Order(state,checkout, uID, orderID, pay, time, shipper,receiverPhone,receiverLocation, DataHandler.getOMAL(), "1000")
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