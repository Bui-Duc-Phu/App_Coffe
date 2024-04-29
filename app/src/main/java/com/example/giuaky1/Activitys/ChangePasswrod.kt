package com.example.giuaky1.Activitys

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.R
import com.example.giuaky1.databinding.ActivityChangePasswrodBinding

class ChangePasswrod : AppCompatActivity() {

    private var progressDialog: ProgressDialog? = null

    private val binding:ActivityChangePasswrodBinding by lazy {
        ActivityChangePasswrodBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init_()

    }

    private fun init_() {
        binding.changePasswBtn.setOnClickListener {
            checkInput()
        }
    }

    private fun checkInput() {
        val oldPassword = binding.oldPasswrodEdt.text.toString().trim()
        val newPasswrod = binding.newPassLayoutEdt.text.toString().trim()
        val confirmPasswrod = binding.confirmPasswordEdt.text.toString().trim()
        when{
            TextUtils.isEmpty(oldPassword) ->{ progressDialog!!.dismiss()
                Toast.makeText(this, "Vui lòng nhập lại password", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(newPasswrod) ->{ progressDialog!!.dismiss()
                Toast.makeText(this, " Hãy nhập mật khẩu mới", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(confirmPasswrod) ->{ progressDialog!!.dismiss()
                Toast.makeText(this, "Hãy nhập lại mật khẩu", Toast.LENGTH_SHORT).show()
            }
            !newPasswrod.equals(confirmPasswrod) -> {progressDialog!!.dismiss()
                Toast.makeText(this, "nhập lại mật khẩu không chính xác", Toast.LENGTH_SHORT).show()
            }
            else -> {

            }

        }
    }

    private fun ChangePasswrod(oldPassword:String ,newPasswrod :String , confirmPasswrod :String){
        FirebaseFunction.getPasswrodWithUid(this){
            if(it.equals("")){
                Toast.makeText(applicationContext, "Tài khoản google không, không thể thay đổi password", Toast.LENGTH_SHORT).show()
            }
            else{

            }
        }
    }

    

















}