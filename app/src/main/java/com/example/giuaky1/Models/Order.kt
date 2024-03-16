package com.example.giuaky1.Models

    data  class Order(
        var state: String ="",
        var checkout: String ="",
        var uID:String ="",
        var orderID:String="",
        var pay:String = "",
        var day:String = "",
        var time:String = "",
        var shipper: Shipper,
        var products:List<Order_product>
    ) {
    }