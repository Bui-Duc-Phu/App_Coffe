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

        fun subStringPhone(phone: String): String {
            if (phone.isEmpty()) {
                return phone
            }
            return phone.substring(1)
        }







    }

}