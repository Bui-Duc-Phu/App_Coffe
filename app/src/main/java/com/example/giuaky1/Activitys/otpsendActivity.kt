package com.example.giuaky1.Activitys

import android.app.ProgressDialog
import android.content.Intent
import android.database.Observable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.Firebase.OTP_Athen_Phone
import com.example.giuaky1.Interfaces.OTPEven
import com.example.giuaky1.databinding.ActivityOtpsendBinding
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

class otpsendActivity : AppCompatActivity(){

    var progressDialog: ProgressDialog? = null
    lateinit var countDownTimer: CountDownTimer


    private val binding: ActivityOtpsendBinding by lazy {
        ActivityOtpsendBinding.inflate(layoutInflater)
    }
    lateinit var OTP: String
    lateinit var receiver: String
    lateinit var type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        OTP  = intent.getStringExtra("OTP").toString()
        receiver = intent.getStringExtra("receiver").toString()
        type = intent.getStringExtra("type").toString()



        init_()
    }

    private fun init_() {
       binding.xacNhanOTPBtn.setOnClickListener {
           oTPProcessing()
       }
       binding.guiLaiTv.setOnClickListener {  creatOtp(receiver)}
        if(OTP.isNotEmpty()) countDownTime()
    }


    private fun oTPProcessing() {
        val otp = binding.pinview.text.toString()
        if(TextUtils.isEmpty(otp) || otp.length < 6){
           binding.pinview.setError("bạn chưa nhập mã OTP")
        }else{
            if(type.equals("mail")){
                if(otp.equals(OTP)){
                    startActivity(
                        Intent(this@otpsendActivity, ForgotPasswordActivity::class.java)
                            .putExtra("receiver",receiver))
                }else{
                    binding.pinview.setText("")
                    Toast.makeText(applicationContext, "OTP không chính xác", Toast.LENGTH_SHORT).show()
                }
            }else{

                OTP_Athen_Phone.OTPAuthenAndRegister(OTP,otp,this){
                    if(true) {
                        startActivity(
                            Intent(this@otpsendActivity, ForgotPasswordActivity::class.java)
                                .putExtra("receiver",receiver))
                    }
                }

            }
        }
    }





    private fun creatOtp(receiver: String){
        progressDialog = ProgressDialog.show(this@otpsendActivity, "App", "Loading...", true)
        val randomDigits = (1..6).map { Random.nextInt(0, 10) }.joinToString("")
        sendOTP(receiver,randomDigits)
    }



    private fun sendOTP(receiver:String,otp:String) {
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
            mimeMessage.setFrom(InternetAddress(stringSenderEmail, "APP COFFE PTIT"))
        } catch (e: MessagingException) {
            e.printStackTrace()
        }

        val thread = Thread {
            try {
                Transport.send(mimeMessage)
                val intent = Intent(this, otpsendActivity::class.java)
                intent.putExtra("OTP",otp)
                progressDialog!!.dismiss()
                startActivity(intent)
                countDownTime()


            } catch (e: MessagingException) {
                e.printStackTrace()
            }
        }
        thread.start()


    }

    private fun countDownTime() {
        runOnUiThread {
            val countdownMillis: Long = 120000
            countDownTimer = object : CountDownTimer(countdownMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    binding.countdownTextview.text = "Thời gian còn lại: ${millisUntilFinished / 1000} giây"
                }

                override fun onFinish() {
                    binding.countdownTextview.text = "Hoàn thành!"
                }
            }
            countDownTimer.start()
        }
    }



}


