package com.example.giuaky1.Models

class ProductModel {
    var name: String = ""
    var price: Int = 0
    var imageUrl: String = ""

    constructor() // Constructor không có đối số để Firebase có thể tạo đối tượng từ dữ liệu

    constructor(name: String, price: Int, imageUrl: String) {
        this.name = name
        this.price = price
        this.imageUrl = imageUrl
    }
}
