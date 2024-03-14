package com.example.giuaky1.Models

import android.os.Parcel
import android.os.Parcelable

class CartModel{
    var name: String? = null
    var imageUrl: String? = null
    var price = 0
    var totalPrice = 0
    var quantity = 0

    constructor(name1:String,imageUrl1: String,quantity1: Int,price1: Int,totalPrice1: Int) {
        name=name1
        imageUrl=imageUrl1
        quantity=quantity1
        price=price1
        totalPrice=totalPrice1
    }

    constructor()

}
