package com.example.giuaky1.Administrator.Activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.giuaky1.databinding.ActivityUserOrderListBinding

class UserOrderList : AppCompatActivity() {
    private val  binding : ActivityUserOrderListBinding by lazy {
        ActivityUserOrderListBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)



    }
}