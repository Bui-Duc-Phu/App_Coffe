package com.example.giuaky1.Models

import java.io.Serializable

data  class Order(
        var state: String ="",
        var checkout: String ="",
        var uID:String ="",
        var orderID:String="",
        var pay:String = "",
        var time:String = "",
        var shipper: Shipper,
        var receiverPhone: String,
        var receiverLocation: String,
        var products: ArrayList<CartModel>,
        var sumPrice: String
    ): Serializable {

}