package com.example.giuaky1.Administrator

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Controller {

    companion object{
        fun permission (context : Context,email:String,callback :(Boolean) -> Unit){
            val ref = FirebaseDatabase
                .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Admins")
            var cnt = 0
            ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (adminSnapshot in snapshot.children) {
                        val Email = adminSnapshot.child("email").getValue(String::class.java)
                        if (email.equals(Email)) cnt++
                    }
                    when{
                        cnt > 0 -> callback(true)
                        else -> callback(false)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "file Controller false get data from firebase", Toast.LENGTH_SHORT).show()
                }
            })
        }










    }
}