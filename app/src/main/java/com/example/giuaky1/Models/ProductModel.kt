package com.example.giuaky1.Models

import java.io.Serializable

class ProductModel : Serializable {
    var name: String? = null
    var key: String? = null
    var price = 0
    var imageUrl: String? = null
    var discount = 0
    var category: String? = null

    constructor()
    constructor(name: String?, price: Int, imageUrl: String?, discount: Int, category: String?) {
        this.name = name
        this.price = price
        this.imageUrl = imageUrl
        this.discount = discount
        this.category = category
    }

    val finalPrice: Double
        get() = price * (1 - discount / 100.0)
}
