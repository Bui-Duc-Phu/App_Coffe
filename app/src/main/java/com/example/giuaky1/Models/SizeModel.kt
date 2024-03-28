package com.example.giuaky1.Models

class SizeModel {
    var size: String = ""
    var price: Double = 0.0 // new price attribute for size

    constructor()

    constructor(size: String, price: Double) {
        this.size = size
        this.price = price
    }
}