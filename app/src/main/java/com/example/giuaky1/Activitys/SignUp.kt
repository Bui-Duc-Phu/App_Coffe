package com.example.giuaky1.Activitys

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.example.giuaky1.Models.Users
import com.example.giuaky1.databinding.ActivitySignUpBinding
import com.example.giuaky1.databinding.DialogCustomBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.properties.Delegates

class SignUp : AppCompatActivity() {
    private val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    lateinit var dialogBinding: DialogCustomBinding
    lateinit var  auth : FirebaseAuth
    private var progressDialog: ProgressDialog? = null
    lateinit var databaseReference : DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        init_()
    }
    private fun init_() {
        binding.apply {
            backBtn.setOnClickListener{
                onBackPressed()

            }
            dangNhapTv.setOnClickListener {
                startActivity(Intent(this@SignUp, Login::class.java))
            }

        }
        checked()


    }

    private fun checked() {
        auth = FirebaseAuth.getInstance()
        binding.dangKyBtn.setOnClickListener {
            progressDialog = ProgressDialog.show(this@SignUp, "App", "Loading...", true)

            val userName = binding.nameEdt.text.toString().trim()
            val Email = binding.emailEdt.text.toString().trim()
            val password = binding.passwordEdt.text.toString().trim()
            checkUserName(userName){
                if(it){
                    progressDialog!!.dismiss()
                    Toast.makeText(applicationContext, "tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show()

                }
                else{
                    when {
                        TextUtils.isEmpty(userName) ->{ progressDialog!!.dismiss()
                            Toast.makeText(this, "Bạn chưa nhập tên đăng nhập", Toast.LENGTH_SHORT).show()}
                        TextUtils.isEmpty(Email) -> {progressDialog!!.dismiss()
                            Toast.makeText(this, "Bạn chưa nhập Email", Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(password) ->{ progressDialog!!.dismiss()
                            Toast.makeText(this, "Password not null", Toast.LENGTH_SHORT).show()}
                        !Patterns.EMAIL_ADDRESS.matcher(Email).matches() ->{progressDialog!!.dismiss()
                            Toast.makeText(this,"không đúng định dang mail", Toast.LENGTH_SHORT).show() }
                        else -> Register(userName, Email, password)
                    }
                }

            }

        }
    }


    private fun Register(userName:String,email:String,password:String){

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {

            if(it.isSuccessful){
                val user: FirebaseUser?  = auth.currentUser
                val userid: String = user!!.uid
                databaseReference = FirebaseDatabase
                    .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("Users")
                    .child(userid)
                val hashmap : HashMap<Any,Any> = HashMap()
                hashmap.put("userID",userid)
                hashmap.put("userName",userName)
                hashmap.put("email",email)
                hashmap.put("password",password)
                hashmap.put("typeAccount","1")
                databaseReference.setValue(hashmap).addOnCompleteListener {
                    if(it.isSuccessful){
                        progressDialog!!.dismiss()
                        dialog_(1)
                        Handler().postDelayed({
                            dialog_(0)
                            startActivity(Intent(this@SignUp, Main::class.java))
                            finish()
                        }, 2000)
                    }
                }
            }else{
                progressDialog!!.dismiss()
                Toast.makeText(applicationContext, "signUp false,Email này đã được liên kết bằng google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dialog_(a: Int) {
        if (!isFinishing) {
            val dialog = Dialog(this)
            dialog.setContentView(DialogCustomBinding.inflate(layoutInflater).root)
            if (a == 1) {
                dialog.show()
            } else {
                dialog.dismiss()
            }
        }
    }

    private fun checkUserName(userName_:String, callback: (Boolean) -> Unit) {
        val ref = FirebaseDatabase
            .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Users")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var isEmailExists = false
                    for (snapshot in snapshot.children) {
                        val user = snapshot.getValue(Users::class.java)
                        if(user!!.userName.equals(userName_)) {
                           isEmailExists=true
                        }
                    }
                    callback(isEmailExists)
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "checkUsername connect to firebase false : "+error.message , Toast.LENGTH_SHORT).show()
                    callback(true)
                }
            })

    }



}