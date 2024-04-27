package com.example.giuaky1.Administrator.Adapters

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Firebase.FirebaseFunction


import com.example.giuaky1.Models.Users

import com.example.giuaky1.databinding.ItemUserOrderManagerAdminBinding

class UserOrderListAdapter (
    private val context:Context,
    private var listUser:List<Users>,


    ):RecyclerView.Adapter<UserOrderListAdapter.viewholer>(){
    lateinit var binding: ItemUserOrderManagerAdminBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholer {
       binding= ItemUserOrderManagerAdminBinding.inflate(LayoutInflater.from(parent.context),parent,false )
        return viewholer(binding.root)
    }

    override fun getItemCount(): Int {
        return listUser.size
    }


    override fun onBindViewHolder(holder: viewholer, position: Int) {

        val model =  listUser[position]

        holder.apply {
            userName.text = model.userName
            FirebaseFunction.readAllOrdersList {allOrderList->
                allOrderList.forEach {order->
                    if(order.uID == model.userID){

                    }


                }
            }




        }

    }


    inner class viewholer(view: View) : RecyclerView.ViewHolder(view){
      val userName = binding.userName
      val sumQuantityOrder  = binding.SumQuantityOrder
      val email = binding.email
      val orderNotComplete = binding.orderNotComplete
      val OrderBeDeliever = binding.OrderBeDeliever
      val orderComplete   = binding.orderComplete


    }
}