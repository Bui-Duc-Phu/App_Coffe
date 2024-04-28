package com.example.giuaky1.Administrator.model

import java.io.Serializable

class Size: Serializable {
    var price = 0
    var size: String? = null

    constructor()
    constructor(price: Int, size: String?) {
        this.price = price
        this.size = size
    }
}