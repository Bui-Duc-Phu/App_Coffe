package com.example.giuaky1.Administrator.model

data class ItemBill(
    val date: String,
    val name: String,
    val price: Double,
    val orderID: String, // Add this
    val uID: String // Add this
)