package com.example.giuaky1.Activitys

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.TextUtils
import android.util.Patterns
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast

import com.chaos.view.PinView
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.Firebase.FirebaseUpdate
import com.example.giuaky1.Firebase.OTP_Athen_Phone
import com.example.giuaky1.Interfaces.OTPEven

import com.example.giuaky1.Models.Users
import com.example.giuaky1.databinding.ActivitySignUpBinding
import com.example.giuaky1.databinding.DialogChoseTypeAuthenBinding
import com.example.giuaky1.databinding.DialogCustomBinding
import com.example.giuaky1.databinding.DialogCustomOtpBinding
import com.google.android.gms.auth.api.phone.SmsRetriever

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.random.Random

class SignUp : AppCompatActivity() {
    private val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    lateinit var dialogBinding: DialogCustomBinding
    lateinit var  auth : FirebaseAuth
    private var progressDialog: ProgressDialog? = null
    lateinit var databaseReference : DatabaseReference
    lateinit var otp_Key :String
    lateinit var otp_User:String
    lateinit var countDownTimer: CountDownTimer



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
            val phone =   binding.phoneEdt.text.toString().trim()
            val password = binding.passwordEdt.text.toString().trim()
            checkUserName(userName,Email){
                println("cnt = > " + it)
                when{
                    it == 1 -> {
                        progressDialog!!.dismiss()
                        Toast.makeText(applicationContext, "tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show()
                    }
                    it == 2 ->{
                        progressDialog!!.dismiss()
                        Toast.makeText(applicationContext, "Email đã được đăng ký", Toast.LENGTH_SHORT).show()
                    }

                    it>2-> {
                        progressDialog!!.dismiss()
                        Toast.makeText(applicationContext, "Email & tên đăng nhập đã được đăng ký", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        when {
                            TextUtils.isEmpty(userName) ->{ progressDialog!!.dismiss()
                                Toast.makeText(this, "Bạn chưa nhập tên đăng nhập", Toast.LENGTH_SHORT).show()}
                            TextUtils.isEmpty(Email) -> {progressDialog!!.dismiss()
                                Toast.makeText(this, "Bạn chưa nhập Email", Toast.LENGTH_SHORT).show()
                            }
                            TextUtils.isEmpty(phone) -> {progressDialog!!.dismiss()
                                Toast.makeText(this, "Bạn chưa nhập Số Điện Thoại", Toast.LENGTH_SHORT).show()
                            }
                            TextUtils.isEmpty(password) ->{ progressDialog!!.dismiss()
                                Toast.makeText(this, "Password not null", Toast.LENGTH_SHORT).show()}
                            !Patterns.EMAIL_ADDRESS.matcher(Email).matches() ->{progressDialog!!.dismiss()
                                Toast.makeText(this,"không đúng định dang mail", Toast.LENGTH_SHORT).show() }
                            else -> {
                                FirebaseFunction.phoneAlreadyExists(this,phone){
                                    if(it){
                                        progressDialog!!.dismiss()
                                        Toast.makeText(this,"SĐT đã được đăng ký", Toast.LENGTH_SHORT).show()
                                    }else {
                                        choseTypeAuthen(1){
                                            when(it){
                                                1 -> createOTPPhone(phone,this){
                                                    if(it){
                                                        registerWithPhone(userName,Email,password,phone)
                                                    }else{
                                                        progressDialog!!.dismiss()
                                                    }
                                                }
                                                2 ->  creatOtp(Email){
                                                    if(it){
                                                        register(userName,Email,password,phone)
                                                    }else{
                                                        progressDialog!!.dismiss()
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }

                }




            }

        }
    }


    private fun  createOTPPhone(phone:String,activity:Activity,callback: (Boolean) -> Unit ){
        OTP_Athen_Phone.sendOtp(phone,activity,){otp->
            otp_Key = otp
            dialog_get_OTP(1){
                callback(it)
            }
        }
    }

    private  fun registerWithPhone(userName:String,email:String,password:String,phone:String){
        OTP_Athen_Phone.OTPAuthenAndRegister(otp_Key,otp_User,this){authen->
            if(authen){
                register(userName,email,password,phone)
            }
        }
    }
    private fun dialog_get_OTP(a: Int,callback: (Boolean) -> Unit) {
        println("otp_key :  "+ otp_Key)
        if (!isFinishing) {
            val dialog = Dialog(this)
            val dialogView = DialogCustomOtpBinding.inflate(layoutInflater)
            dialog.setContentView(dialogView.root)
            dialog.setCancelable(false)

            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
            countDownTime(dialogView.countdownTextview)



            dialogView.exit.setOnClickListener {
                dialog.dismiss()
                progressDialog!!.dismiss()
                callback(false)
            }
            dialogView.XacthucBtn.setOnClickListener {
                val otp  = dialogView.pinview.text.toString()
                if(otp.isEmpty() || otp.length > 6){
                    Toast.makeText(applicationContext, "Hãy nhập đầy đủ otp", Toast.LENGTH_SHORT).show()
                }else{
                    otp_User = otp
                    callback(true)
                    dialog.dismiss()
                    progressDialog = ProgressDialog.show(this@SignUp, "App", "Loading...", true)
                }
            }
            if (a == 1) {
                dialog.show()
            } else {
                dialog.dismiss()
            }
        }
    }

    private fun dialog_OTP(a: Int,callback: (Boolean) -> Unit) {
        println("otp_key :  "+ otp_Key)
        if (!isFinishing) {
            val dialog = Dialog(this)
            val dialogView = DialogCustomOtpBinding.inflate(layoutInflater)
            dialog.setContentView(dialogView.root)
            dialog.setCancelable(false)

            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams

            countDownTime(dialogView.countdownTextview)


            dialogView.exit.setOnClickListener {
                dialog.dismiss()
                callback(false)
            }
            dialogView.XacthucBtn.setOnClickListener {
                val otp  = dialogView.pinview.text.toString()
                println("otp :  "+ otp)
                if(otp.isEmpty() || otp.length > 6){
                    Toast.makeText(applicationContext, "Hãy nhập đầy đủ otp", Toast.LENGTH_SHORT).show()
                }else{
                    if(otp_Key.equals(otp)){
                        dialog.dismiss()
                        callback(true)
                    }else{
                        Toast.makeText(applicationContext, "OTP chưu chính xác", Toast.LENGTH_SHORT).show()
                        dialogView.pinview.setText("")
                    }
                }
            }
            if (a == 1) {
                dialog.show()
            } else {
                dialog.dismiss()
            }
        }
    }











    private fun choseTypeAuthen(a:Int, callback: (Int) -> Unit){
        if (!isFinishing) {
            val dialog = Dialog(this)
            val dialogView = DialogChoseTypeAuthenBinding.inflate(layoutInflater)
            dialog.setContentView(dialogView.root)
            dialog.setCancelable(false)

            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
            dialogView.exit.setOnClickListener {
                dialog.dismiss()
                callback(3)
            }
            dialogView.XacThucBtn.setOnClickListener {
                val selectedRadioButtonId = dialogView.radioGroup.checkedRadioButtonId
                val selectedRadioButton = dialogView.root.findViewById<RadioButton>(selectedRadioButtonId)
                if(selectedRadioButton == dialogView.radioPhone){
                  callback(1)
                  dialog.dismiss()
              }
                else{
                    callback(2)
                    dialog.dismiss()
              }
            }
            if (a == 1) {
                dialog.show()
            } else {
                dialog.dismiss()
            }
        }
    }




    private fun register(userName:String,email:String,password:String,phone: String){
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
                FirebaseUpdate.updateDataProfile(this,phone,"","","","")
            }else{
                progressDialog!!.dismiss()
                Toast.makeText(applicationContext, "signUp false,Email này đã được đăng ký", Toast.LENGTH_SHORT).show()
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

    private fun checkUserName(userName_:String,email:String, callback: (Int) -> Unit) {
        val ref = FirebaseDatabase
            .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Users")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var isEmailExists = 0
                    var  isUserNameExists= 0
                    for (snapshot in snapshot.children) {
                        val user = snapshot.getValue(Users::class.java)
                        if(user!!.userName.equals(userName_)) isUserNameExists+=1
                        if(email.equals(user.email)) isEmailExists+=2
                    }
                    callback(isEmailExists + isUserNameExists)
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "checkUsername connect to firebase false : "+error.message , Toast.LENGTH_SHORT).show()
                    callback(0)
                }
            })
    }









    private fun creatOtp(receiver: String,callback: (Boolean) -> Unit){
        val randomDigits = (1..6).map { Random.nextInt(0, 10) }.joinToString("")
        otp_Key = randomDigits.toString()
        sendOTP(receiver,randomDigits.toString()){
           callback(it)
        }
    }



    private fun sendOTP(receiver:String,otp:String,callback: (Boolean) -> Unit) {
        val stringSenderEmail = "firebase683@gmail.com"
        val stringReceiverEmail = receiver
        val stringPasswordSenderEmail = "pmei knlr idbd nkgy"
        val stringHost = "smtp.gmail.com"
        val properties = Properties()
        properties["mail.smtp.host"] = stringHost
        properties["mail.smtp.port"] = "465"
        properties["mail.smtp.ssl.enable"] = "true"
        properties["mail.smtp.auth"] = "true"
        properties["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail)
            }
        })
        val mimeMessage = MimeMessage(session)
        try {
            mimeMessage.setRecipient(Message.RecipientType.TO, InternetAddress(stringReceiverEmail))
            mimeMessage.subject = "send otp:"
            mimeMessage.setText("OTP : "+ otp)
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
        val thread = Thread {
            try {
                Transport.send(mimeMessage)
                runOnUiThread {
                    dialog_OTP(1){
                        callback(it)

                    }
                }

            } catch (e: MessagingException) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    private fun countDownTime(countdownTextview:TextView) {
        runOnUiThread {
            val countdownMillis: Long = 120000
            countDownTimer = object : CountDownTimer(countdownMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    countdownTextview.text = "Thời gian còn lại: ${millisUntilFinished / 1000} giây"
                }
                override fun onFinish() {
                    val randomDigits = (1..6).map { Random.nextInt(0, 10) }.joinToString("")
                    otp_Key = randomDigits
                    countdownTextview.text = "OTP đã hết hạn,hãy tạo tại OTP!"
                }
            }
            countDownTimer.start()
        }
    }








}