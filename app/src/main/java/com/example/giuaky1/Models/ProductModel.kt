package com.example.giuaky1.Models
import java.io.Serializable

class ProductModel : Serializable {
    var name: String = ""
    var price: Double = 0.0
    var imageUrl: String = ""

    constructor()

    constructor(name: String, price: Double, imageUrl: String) {
        this.name = name
        this.price = price
        this.imageUrl = imageUrl
    }
}