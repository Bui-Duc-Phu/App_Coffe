package com.example.giuaky1.Administrator.model

import java.io.Serializable

class coffeModel: Serializable {
    var imageUrl: String? = null
    var name: String? = null
    var category: String? = null
    var price = 0
    var discount = 0
    var sizes: Map<String, Size>? = null

    constructor()
    constructor(
        imageUrl: String?,
        name: String?,
        category: String?,
        price: Int,
        discount: Int,
        sizes: Map<String, Size>?
    ) {
        this.imageUrl = imageUrl
        this.name = name
        this.category = category
        this.price = price
        this.discount = discount
        this.sizes = sizes
    }

    final val finalPrice: Double
        get() = price * (1 - discount / 100.0)
}