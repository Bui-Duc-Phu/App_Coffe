package com.example.giuaky1.Models
class CartModel {
    var name: String = ""
    var imageUrl: String = ""
    var quantity: Int = 0
    var price: Double = 0.0
    var totalPrice: Double = 0.0
    var size: String = ""
    var sizePrice: Double = 0.0 // new attribute for size price

    constructor()

    constructor(name: String, imageUrl: String, quantity: Int, price: Double, totalPrice: Double, size: String, sizePrice: Double) {
        this.name = name
        this.imageUrl = imageUrl
        this.quantity = quantity
        this.price = price
        this.totalPrice = totalPrice
        this.size = size
        this.sizePrice = sizePrice
    }
}