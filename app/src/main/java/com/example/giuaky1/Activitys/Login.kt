package com.example.giuaky1.Activitys

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.example.giuaky1.Administrator.Activitys.MainAdmin
import com.example.giuaky1.Administrator.Controller
import com.example.giuaky1.Models.Users
import com.example.giuaky1.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import javax.mail.*



class Login : AppCompatActivity() {


    lateinit var auth: FirebaseAuth
    lateinit var firebaseUser: FirebaseUser
    private var progressDialog: ProgressDialog? = null

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()




        init_()
    }
    private fun init_() {
        binding.apply {
            backBtn.setOnClickListener{
                onBackPressed()
            }
            dangKyTv.setOnClickListener {
                startActivity(Intent(this@Login, SignUp::class.java))
            }

            dangNhapBtn.setOnClickListener {checked()}


            quyenMatKhauTv.setOnClickListener {
                startActivity(Intent(this@Login,InputEmailActivity::class.java))
            }



        }
    }







    private fun checked(){
        var email = binding.emailEdt.text.toString().trim()
        var password =  binding.passwordEdt.text.toString().trim()
        when {
            TextUtils.isEmpty(email) -> Toast.makeText(this, "Email not null", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "Password not null", Toast.LENGTH_SHORT).show()
            else -> {
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) loginWithUsername(email,password)
                else  loginWithEmail(email,password)
            }
        }



    }

    private fun loginWithUsername(email:String,password: String){
        val ref  =  FirebaseDatabase
            .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Users")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                progressDialog = ProgressDialog.show(this@Login, "App", "Loading...", true)
                for (snapshot in snapshot.children) {
                    val user = snapshot.getValue(Users::class.java)
                    if(user!!.userName.equals(email)) {
                        progressDialog!!.dismiss()
                        loginWithEmail(user.email,password)
                        return
                    }
                }
                progressDialog!!.dismiss()
                Toast.makeText(applicationContext, "Tên đăng nhập không tồn tại!", Toast.LENGTH_SHORT).show()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Login with username, connect database faile!", Toast.LENGTH_SHORT).show()
            }
        })
    }


    @SuppressLint("NotConstructor")
    private fun loginWithEmail(email:String, password:String){
        progressDialog = ProgressDialog.show(this, "App", "Loading...", true)
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    binding.emailEdt.setText("")
                    binding.passwordEdt.setText("")
                    progressDialog!!.dismiss()
                    Controller.permission(applicationContext,email){userOrAdmin->
                        System.out.println(userOrAdmin)
                        if(userOrAdmin){
                            startActivity(Intent(this@Login,MainAdmin::class.java))
                            finish()
                        }else{
                            val intent = Intent(this, Main::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }

                }else{
                    progressDialog!!.dismiss()
                    Toast.makeText(this, "email or password is incorrect", Toast.LENGTH_SHORT).show()
                }
            }
    }



}