package com.example.giuaky1.Models

class SizeModel {
    var size = ""
    var price = 0.0
        set(value) {
            field = value
        }

    constructor()
    constructor(size: String, price: Double) {
        this.size = size
        this.price = price
    }

    fun getPrice(): Int {
        return price.toInt()
    }
}