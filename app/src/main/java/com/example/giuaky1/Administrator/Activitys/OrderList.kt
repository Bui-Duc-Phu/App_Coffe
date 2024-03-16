package com.example.giuaky1.Administrator.Activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.example.giuaky1.Administrator.Adapters.OrderListAdapter
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.R
import com.example.giuaky1.databinding.ActivityOrderListBinding
import com.google.firebase.Firebase

class OrderList : AppCompatActivity() {
    private val binding : ActivityOrderListBinding by lazy {
        ActivityOrderListBinding.inflate(layoutInflater)
    }


    private var mainMenu:Menu?=null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        init_()
    }

    private fun init_() {
        recylerviewListOrder()
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

    }

    private fun recylerviewListOrder() {
      FirebaseFunction.readAllOrdersList(){orderList->
          val adapter = OrderListAdapter(this,orderList){show->showIconToolbarMenu(show)}
          binding.recylerview.adapter = adapter
          adapter.notifyDataSetChanged()
        }
    }

    private fun showIconToolbarMenu(show:Boolean){
        if(show){
            binding.checkout.visibility = View.VISIBLE
            binding.trash.visibility = View.VISIBLE
            binding.selectAll.visibility=View.VISIBLE
            binding.backBtn.visibility=View.GONE
            binding.title.visibility=View.GONE
        }else{
            binding.checkout.visibility = View.GONE
            binding.trash.visibility = View.GONE
            binding.selectAll.visibility=View.GONE
            binding.backBtn.visibility=View.VISIBLE
            binding.title.visibility=View.VISIBLE
        }
    }







}