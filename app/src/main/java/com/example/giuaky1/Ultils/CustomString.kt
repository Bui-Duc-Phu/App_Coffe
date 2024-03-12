package com.example.giuaky1.Ultils

import com.example.giuaky1.Models.Order
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar
import kotlin.random.Random

class CustomString {

    companion object{
         fun shipper(name:String,SDT:String) :String{
            return   "Shipper : $name\nSĐT : $SDT"
        }
         fun idOrder():String{
            val random = Random(System.currentTimeMillis())
            val min = 10000000000
            val max = 99999999999
            val randomNumber = random.nextLong(min, max)
            return "$randomNumber"
        }

         fun dateRealdtime() :String{
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            return "$day/$month/$year"
        }

        fun timeRealdTime()  : String{
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val second = calendar.get(Calendar.SECOND)
            return "$hour:$minute"
        }
        fun readOrdersFromFirebase(callback: (List<Order>) -> Unit) {
            val ordersRef = FirebaseDatabase.getInstance().getReference("orders")
            ordersRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val ordersList = mutableListOf<Order>()
                    for (snapshot in dataSnapshot.children) {
                        // Chuyển đổi dữ liệu từ DataSnapshot thành đối tượng Order
                        val order = snapshot.getValue(Order::class.java)
                        order?.let {
                            ordersList.add(order)
                        }
                    }
                    callback(ordersList)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    println("readOrdersFromFirebase: onCancelled ${databaseError.toException()}")
                }
            })
        }


    }

}