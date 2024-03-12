package com.example.giuaky1.Ultils

import java.text.NumberFormat
import java.util.Locale

class MyCategory {

    companion object{
        fun isSumPriceProduct(quantity:String,price:String) : String{
            val sum  = quantity.toInt() * price.toInt()
            val formattedNumber = NumberFormat.getNumberInstance(Locale.US).format(sum)
            return formattedNumber
        }


    }
}