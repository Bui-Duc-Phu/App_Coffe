package com.example.giuaky1.Firebase

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.firebase.database.FirebaseDatabase

class FirebaseUpdate {
    companion object{
        fun DeleteChidl(orderID:String, callback: (Boolean) -> Unit){
            val ref = FirebaseDatabase
                .getInstance()
                .getReference("Orders")
                .child(orderID)
            ref.removeValue()
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener {
                    callback(false)
                    Log.d("FirebaseUpdate", "DeleteChidl: bug from DeleteChild on FirebaseUpdate")
                }
        }

        fun updateOrderState(context:Context,orderID: String, state: String) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Orders").child(orderID).child("state")
            databaseReference.setValue(state)
                .addOnSuccessListener {
                    Toast.makeText(context, "Update state Sucessfull", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Update state False", Toast.LENGTH_SHORT).show()
                }

        }

        fun updateOrderCheckout(context:Context,orderID: String) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Orders").child(orderID).child("checkout")
            databaseReference.setValue("1")
                .addOnSuccessListener {

                }
                .addOnFailureListener {
                    Toast.makeText(context, "Update checkOut False", Toast.LENGTH_SHORT).show()
                }

        }
























    }
}