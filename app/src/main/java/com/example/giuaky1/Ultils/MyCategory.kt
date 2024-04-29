package com.example.giuaky1.Ultils


import android.util.Patterns
import com.example.giuaky1.Models.Order_product

import com.example.giuaky1.Models.ProductModel

import java.text.NumberFormat
import java.util.Locale

class MyCategory {

    companion object{
        fun isSumPriceProduct(quantity:String,price:String) : String{
            val sum  = quantity.toInt() * price.toInt()
            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            return formatter.format(sum)
        }
        fun calculateTotalPriceFormatted(): String{
            var totalPrice = 0.0

            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            return formatter.format(totalPrice)
        }
        fun isNumeric(input: String): Boolean {
            return input.all { it.isDigit() }
        }

        fun mailOrPhone(s:String):Boolean{
            if(Patterns.EMAIL_ADDRESS.matcher(s).matches()){
                return  true

            }else {
                return false
            }
        }


    }





}