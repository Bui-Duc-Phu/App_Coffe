package com.example.giuaky1.Firebase

import com.example.giuaky1.Models.Order
import com.example.giuaky1.Models.Order_product
import com.example.giuaky1.Models.Shipper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseFunction {
    companion object{
         fun readOrdersCompleted(uid:String,callback: (List<Order>) -> Unit) {
            val ref = FirebaseDatabase
                .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Orders")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val orderList = mutableListOf<Order>()
                    for (orderSnapshot in snapshot.children) {
                        val state = orderSnapshot.child("state").value.toString()
                        val uID = orderSnapshot.child("uID").value.toString()
                        val orderID = orderSnapshot.child("orderID").value.toString()
                        val pay = orderSnapshot.child("pay").value.toString()
                        val day = orderSnapshot.child("day").value.toString()
                        val time = orderSnapshot.child("time").value.toString()
                        val shipperName = orderSnapshot.child("shipper").child("name").value.toString()
                        val shipperSDT = orderSnapshot.child("shipper").child("sDT").value.toString()

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
                        val order = Order(state, uID, orderID, pay, day, time, shipper, productsList)

                        if(uid.equals(uID) && state.equals("1")) orderList.add(order)
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
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val orderList = mutableListOf<Order>()
                    for (orderSnapshot in snapshot.children) {
                        val state = orderSnapshot.child("state").value.toString()
                        val uID = orderSnapshot.child("uID").value.toString()
                        val orderID = orderSnapshot.child("orderID").value.toString()
                        val pay = orderSnapshot.child("pay").value.toString()
                        val day = orderSnapshot.child("day").value.toString()
                        val time = orderSnapshot.child("time").value.toString()
                        val shipperName = orderSnapshot.child("shipper").child("name").value.toString()
                        val shipperSDT = orderSnapshot.child("shipper").child("sDT").value.toString()

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
                        val order = Order(state, uID, orderID, pay, day, time, shipper, productsList)

                        if(uid.equals(uID) && state.equals("0")) orderList.add(order)
                    }
                    callback(orderList)
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
        }



    }



}