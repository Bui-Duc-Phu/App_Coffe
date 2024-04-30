package com.example.giuaky1.Firebase

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseUpdate {
    companion object{
        final val PICK_IMAGE_REQUEST:Int = 2020
         lateinit var storage:FirebaseStorage

         lateinit var refStorage: StorageReference



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

        fun uploadImage(filePath: Uri,context: Context,callback: (Boolean) -> Unit){
            storage = FirebaseStorage.getInstance()
            refStorage = storage.reference
            val firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser!!
            if(filePath != null){
                var ref : StorageReference = refStorage.child("image/"+ firebaseUser.uid.toString())
                ref.putFile(filePath!!).addOnCompleteListener{
                    if(it.isSuccessful){
                        return@addOnCompleteListener callback(true)
                    }else{
                        return@addOnCompleteListener callback(false)
                    }
                }

            }
        }


        fun updateDataProfile(context:Context,phone:String,location:String,date:String,name:String,mail:String) {
            val firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser!!
            val databaseReference = FirebaseDatabase.getInstance()
                .getReference("ProfileUser")
                .child(firebaseUser.uid)
            
            databaseReference.child("phoneNumber").setValue(phone).addOnFailureListener {
                Toast.makeText(context, "false to update from updateDataProfile", Toast.LENGTH_SHORT).show()
            }
            databaseReference.child("location").setValue(location).addOnFailureListener {
                Toast.makeText(context, "false to update from updateDataProfile", Toast.LENGTH_SHORT).show()
            }
            databaseReference.child("dateOfBirth").setValue(date).addOnFailureListener {
                Toast.makeText(context, "false to update from updateDataProfile", Toast.LENGTH_SHORT).show()
            }
            databaseReference.child("name").setValue(name).addOnFailureListener {
                Toast.makeText(context, "false to update from updateDataProfile", Toast.LENGTH_SHORT).show()
            }
            databaseReference.child("mail").setValue(mail).addOnFailureListener {
                Toast.makeText(context, "false to update from updateDataProfile", Toast.LENGTH_SHORT).show()
            }
        }



























    }
}