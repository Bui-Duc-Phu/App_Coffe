package com.example.giuaky1.Administrator.Activitys

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Administrator.Adapters.ItemDetailAdapter
import com.example.giuaky1.Models.Order
import com.example.giuaky1.R

class DetailOrder : AppCompatActivity() {

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_order)
        val orderDetail = intent.getSerializableExtra("orderDetail") as Order
        val IDTv = findViewById<TextView>(R.id.IDTv1)
        val payTv = findViewById<TextView>(R.id.payTv1)
        val timeTv = findViewById<TextView>(R.id.timeTv1)
        val shiperTv = findViewById<TextView>(R.id.shiperTv1)
        val sumPrice = findViewById<TextView>(R.id.sumPrice1)
        val receiverPhone = findViewById<TextView>(R.id.receiverPhone)
        val receiverLocation = findViewById<TextView>(R.id.receiverLocation)
        val productRecylerview = findViewById<RecyclerView>(R.id.product_recylerview1)
        val btnBack = findViewById<ImageView>(R.id.backImage)
        IDTv.text = "ID: #${orderDetail.orderID}"
        payTv.text = "Phương thức thanh toán :${orderDetail.pay}"
        timeTv.text = "Thời gian: ${orderDetail.time}"
        shiperTv.text = "Shipper: ${orderDetail.shipper.name}\nSĐT: ${orderDetail.shipper.sDT}"
        sumPrice.text = "Tổng: ${orderDetail.sumPrice}"
        receiverPhone.text = "SĐT người nhận: ${orderDetail.receiverPhone}"
        receiverLocation.text = "Địa chỉ: ${orderDetail.receiverLocation}"

        val adapter = ItemDetailAdapter(this, orderDetail.products)
        productRecylerview.adapter = adapter

        btnBack.setOnClickListener {
            finish()
        }
    }

}