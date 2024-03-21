package com.example.giuaky1.Interfaces

import com.example.giuaky1.Models.Order

interface PassData {
    fun onOrderDataReceived(order: Order)
}