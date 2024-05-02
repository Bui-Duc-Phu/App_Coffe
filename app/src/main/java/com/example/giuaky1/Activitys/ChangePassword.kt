package com.example.giuaky1.Activitys

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.R
import com.example.giuaky1.databinding.ActivityChangePasswrodBinding
import com.example.giuaky1.databinding.DialogCustomForgotPasswordTrueBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class ChangePassword : AppCompatActivity() {

    private var progressDialog: ProgressDialog? = null

    lateinit var auth: FirebaseAuth

    private val binding:ActivityChangePasswrodBinding by lazy {
        ActivityChangePasswrodBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        init_()

    }

    private fun init_() {
        binding.changePasswBtn.setOnClickListener {
            checkInput()
        }
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun checkInput() {
        val oldPassword = binding.oldPasswrodEdt.text.toString().trim()
        val newPasswrod = binding.newPassLayoutEdt.text.toString().trim()
        val confirmPasswrod = binding.confirmPasswordEdt.text.toString().trim()
        when{
            TextUtils.isEmpty(oldPassword) ->{ progressDialog?.dismiss()
                Toast.makeText(this, getString(R.string.nhap_lai_pass), Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(newPasswrod) ->{ progressDialog?.dismiss()
                Toast.makeText(this, getString(R.string.nhap_pass_new), Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(confirmPasswrod) ->{ progressDialog?.dismiss()
                Toast.makeText(this, getString(R.string.pls_write_pass), Toast.LENGTH_SHORT).show()
            }
            !newPasswrod.equals(confirmPasswrod) -> {progressDialog?.dismiss()
                Toast.makeText(this, getString(R.string.pass_incorrect), Toast.LENGTH_SHORT).show()
            }
            else -> {
                changePasswrod(oldPassword,newPasswrod,confirmPasswrod)

            }

        }
    }

    private fun changePasswrod(oldPassword:String ,newPasswrod :String , confirmPasswrod :String){
        FirebaseFunction.getPasswrodWithUid(this){
                if(oldPassword.equals(it)){
                    val email = auth.currentUser!!.email.toString()
                    updatePassword(email,oldPassword,newPasswrod)
                }else{
                    Toast.makeText(applicationContext,
                        getString(R.string.pass_not_correct), Toast.LENGTH_SHORT).show()
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
                                                            progressDialog?.dismiss()
                                                            dialog_(1)
                                                            Handler().postDelayed({
                                                                dialog_(0)
                                                                FirebaseFunction.WriteDeviceId(applicationContext,  auth.currentUser!!.uid.toString() )
                                                                startActivity(Intent(this@ChangePassword, Main::class.java))
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
                                                progressDialog?.dismiss()
                                                Toast.makeText(
                                                    this@ChangePassword,
                                                    "Failed to update password: ${updatePasswordTask.exception?.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                } else {
                                    progressDialog?.dismiss()
                                    Toast.makeText(
                                        this@ChangePassword,
                                        "Failed to reauthenticate: ${reauthTask.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        progressDialog?.dismiss()
                        Toast.makeText(
                            this@ChangePassword,
                            "Current user is null",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    progressDialog?.dismiss()
                    Toast.makeText(
                        this@ChangePassword,
                        "Email or password is incorrect",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun dialog_(a: Int) {
        if (!isFinishing && !isDestroyed) { // Thêm kiểm tra isDestroyed
            val dialog = Dialog(this)
            val dialogView = DialogCustomForgotPasswordTrueBinding.inflate(layoutInflater)
            dialog.setContentView(dialogView.root)
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
            if (a == 1) {
                try {
                    dialog.show()
                } catch (e: WindowManager.BadTokenException) {
                    // Xử lý ngoại lệ nếu có
                    e.printStackTrace()
                }
            } else {
                dialog.dismiss()
            }
        }
    }





















}