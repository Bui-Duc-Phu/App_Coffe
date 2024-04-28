package com.example.giuaky1.Administrator.Activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.giuaky1.Adapters.ProductInOrderItemAdapter
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.Ultils.CustomString
import com.example.giuaky1.Ultils.MyCategory
import com.example.giuaky1.databinding.ActivityDetailOrderBinding

class DetailOrder : AppCompatActivity(){
    private val binding : ActivityDetailOrderBinding by lazy {
        ActivityDetailOrderBinding.inflate(layoutInflater)
    }
    lateinit var orderID : String
    lateinit var uID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        uID = intent.getStringExtra("uID").toString()
        orderID = intent.getStringExtra("orderID").toString()
        System.out.println("uid  : " + uID  )
        System.out.println("order  : " + orderID )





        init_()

    }

    private fun init_() {
        FirebaseFunction.getOrderDataWithOrderId(orderID){order->
            System.out.println("order  : " + order )
            binding.apply {
                IDTv.text = "ID : #${order.orderID}"
                payTv.text = order.pay
                timeTv.text="Thời gian : ${order.time}"
                shiperTv.text= CustomString.shipper(order.shipper.name,order.shipper.sDT)
                sumPrice.text ="Tổng = 1000"
                receiverPhone.text = "SĐT người nhận : ${order.receiverPhone}"
                receiverLocation.text = "Địa chỉ : ${order.receiverLocation}"
                val adapter =  ProductInOrderItemAdapter(this@DetailOrder,order.products)
                productRecylerview.adapter = adapter
                FirebaseFunction.getUserDataWithUid(order.uID){ User-> receiverEmail.text = "Email : ${User.email}"}
            }
        }
        binding.btnBack.setOnClickListener {
            onBackPressed()
            finish()
        }
    }



}
