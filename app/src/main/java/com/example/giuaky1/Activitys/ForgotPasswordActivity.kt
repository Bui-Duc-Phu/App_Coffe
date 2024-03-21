package com.example.giuaky1.Activitys

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.widget.Toast
import com.example.giuaky1.Models.Users
import com.example.giuaky1.databinding.ActivityForgotPasswordBinding
import com.example.giuaky1.databinding.DialogCustomForgotPasswordTrueBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ForgotPasswordActivity : AppCompatActivity() {


    private val binding: ActivityForgotPasswordBinding by lazy {
        ActivityForgotPasswordBinding.inflate(layoutInflater)
    }

    lateinit var auth: FirebaseAuth
    lateinit var firebaseUser: FirebaseUser
    private var progressDialog: ProgressDialog? = null
    lateinit var receiver: String
    lateinit var uri:String




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        receiver = intent.getStringExtra("receiver").toString()
        System.out.println(receiver)



        init_()
    }

    private fun init_() {
        binding.guiBtn.setOnClickListener {
            upDatePasswrod()
        }
    }

    private fun upDatePasswrod() {
        val newPassword = binding.passwordEdt.text.toString()
        val retypePassword = binding.retypePassword.text.toString()
        progressDialog = ProgressDialog.show(this, "App", "Loading...", true)
        when{
            TextUtils.isEmpty(newPassword) -> binding.passwordEdt.setError("Chưa nhập mật khẩu")
            TextUtils.isEmpty(retypePassword) -> binding.passwordEdt.setError("Chưa nhập mật khẩu")
            else -> {
            val ref  =  FirebaseDatabase
                    .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("Users")
            ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    System.out.println("11")
                    for (snapshot in snapshot.children) {

                        val user = snapshot.getValue(Users::class.java)
                        if(user!!.email.equals(receiver)) {
                            uri = user.userID
                            updatePassword(receiver, user.password, newPassword)

                        }
                    }
                    
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "", Toast.LENGTH_SHORT).show()

                }
            })

            }
        }

    }


    private fun updatePassword(email: String, password: String, newPassword: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { signInTask ->
                if (signInTask.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        val credential = EmailAuthProvider.getCredential(email, password)
                        currentUser.reauthenticate(credential)
                            .addOnCompleteListener { reauthTask ->
                                if (reauthTask.isSuccessful) {
                                    currentUser.updatePassword(newPassword)
                                        .addOnCompleteListener { updatePasswordTask ->
                                            if (updatePasswordTask.isSuccessful) {
                                                val ref  =  FirebaseDatabase
                                                    .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                                    .getReference("Users")
                                                    .child(currentUser.uid)
                                                    .child("password")
                                                ref.setValue(newPassword)
                                                    .addOnCompleteListener {task->
                                                        if(task.isSuccessful){
                                                            progressDialog!!.dismiss()
                                                            dialog_(1)
                                                            Handler().postDelayed({
                                                                dialog_(0)
                                                                startActivity(Intent(this@ForgotPasswordActivity, Main::class.java))
                                                                finish()
                                                            }, 2000)
                                                        }else{
                                                            Toast.makeText(
                                                                applicationContext,
                                                                "Failed to set pasword on realtime: ${task.exception?.message}",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                            } else {
                                                progressDialog!!.dismiss()
                                                Toast.makeText(
                                                    this@ForgotPasswordActivity,
                                                    "Failed to update password: ${updatePasswordTask.exception?.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                } else {
                                    progressDialog!!.dismiss()
                                    Toast.makeText(
                                        this@ForgotPasswordActivity,
                                        "Failed to reauthenticate: ${reauthTask.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        progressDialog!!.dismiss()
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Current user is null",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    progressDialog!!.dismiss()
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Email or password is incorrect",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }










    private fun dialog_(a: Int) {
        if (!isFinishing) {
            val dialog = Dialog(this)
            val dialogView = DialogCustomForgotPasswordTrueBinding.inflate(layoutInflater)
            dialog.setContentView(dialogView.root)
            if (a == 1) {
                dialog.show()
            } else {
                dialog.dismiss()
            }
        }
    }
}