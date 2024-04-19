package com.example.giuaky1.Administrator.Activitys

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.util.size
import com.example.giuaky1.Administrator.Adapters.OrderListAdapter
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.Firebase.FirebaseUpdate
import com.example.giuaky1.databinding.ActivityOrderListBinding

class OrderList : AppCompatActivity() {
    private val binding : ActivityOrderListBinding by lazy {
        ActivityOrderListBinding.inflate(layoutInflater)
    }
    lateinit var  adapter : OrderListAdapter

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
        binding.trash.setOnClickListener {
            var list =  adapter.getList()
            if(adapter.getSelectedItems().size > 0){
                adapter.getSelectedItemsPositions().forEach{position->
                    FirebaseUpdate.DeleteChidl(list[position].orderID){}
                }
            }
            adapter.notifyDataSetChanged()
            showIconToolbarMenu(false)
        }
        binding.selectAll.setOnClickListener {
            adapter.selectAllItems(binding.selectAll.isChecked)

        }
        binding.checkout.setOnClickListener {
            var list =  adapter.getList()
            if(adapter.getSelectedItems().size > 0){
                adapter.getSelectedItemsPositions().forEach{position->
                    FirebaseUpdate.apply {
                        updateOrderCheckout(applicationContext,list[position].orderID)
                        updateOrderState(applicationContext,list[position].orderID,"0")
                    }
                }
            }
            adapter.notifyDataSetChanged()
            showIconToolbarMenu(false)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun recylerviewListOrder() {
      FirebaseFunction.readAllOrdersList(){orderList->
       adapter = OrderListAdapter(this,orderList){show->showIconToolbarMenu(show)}
       binding.recylerview.adapter = adapter
       adapter.notifyDataSetChanged()
        }
    }

    private fun showIconToolbarMenu(show:Boolean){
        System.out.println(show)
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