package com.example.giuaky1.Models
import java.io.Serializable

class CartModel: Serializable{
    var name: String? = null
    var imageUrl: String? = null
    var quantity = 0
    var price = 0.0
    var totalPrice = 0.0
    var size: String? = null
    var sizePrice = 0.0

    constructor()
    constructor(
        name: String?,
        imageUrl: String?,
        quantity: Int,
        price: Double,
        totalPrice: Double,
        size: String?,
        sizePrice: Double
    ) {
        this.name = name
        this.imageUrl = imageUrl
        this.quantity = quantity
        this.price = price
        this.totalPrice = totalPrice
        this.size = size
        this.sizePrice = sizePrice
    }
}