package com.example.giuaky1.Activitys

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Toast
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.Firebase.OTP_Athen_Phone
import com.example.giuaky1.Interfaces.OTPEven
import com.example.giuaky1.R
import com.example.giuaky1.Ultils.MyCategory
import com.example.giuaky1.databinding.ActivityInputEmailBinding
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


class InputEmailActivity : AppCompatActivity() {
    var progressDialog: ProgressDialog ? = null

    private val binding: ActivityInputEmailBinding by lazy {
        ActivityInputEmailBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init_()
    }

    private fun init_() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.sendOTP.setOnClickListener {
            val receiver = binding.emailEdt.text.toString()
            if(receiver.isNotEmpty()){
                if(MyCategory.mailOrPhone(receiver)) {
                    creatOtp(receiver)
                }else{
                    FirebaseFunction.phoneAlreadyExists(this,receiver){
                        if(it){
                            FirebaseFunction.getUidWithPhone(receiver){uid->
                                FirebaseFunction.getUserDataWithUid(uid){user->
                                    if(user.typeAccount.equals("1")){
                                        sendOTPPhone(receiver,user.email)
                                    }else{
                                        Toast.makeText(applicationContext,
                                            getString(R.string.sdt_li_n_k_t_google), Toast.LENGTH_SHORT).show()
                                    }

                                }
                            }
                        }else{
                            Toast.makeText(applicationContext,
                                getString(R.string.s_t_ch_a_c_ng_k), Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            } else binding.emailEdt.setError(getString(R.string.b_n_ch_a_nh_p_th_ng_tin))

        }
    }

    fun sendOTPPhone(receiver: String,mail:String){
        progressDialog = ProgressDialog.show(this@InputEmailActivity, "App", "Loading...", true)
        OTP_Athen_Phone.sendOtp(receiver,this){OTP_key->
            val intent = Intent(this, otpsendActivity::class.java)
            intent.putExtra("OTP",OTP_key)
            intent.putExtra("type","phone")
            intent.putExtra("receiver",mail)
            intent.putExtra("phone",receiver)
            startActivity(intent)
            finish()
            progressDialog!!.dismiss()
        }
    }




    private fun creatOtp(receiver: String){
        val randomDigits = (1..6).map { Random.nextInt(0, 10) }.joinToString("")
       sendOTP(receiver,randomDigits.toString())
    }



    private fun sendOTP(receiver:String,otp:String) {
        progressDialog = ProgressDialog.show(this@InputEmailActivity, "App", "Loading...", true)
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
                intent.putExtra("type","mail")
                intent.putExtra("receiver",receiver)

                progressDialog!!.dismiss()
                startActivity(intent)
                finish()


            } catch (e: MessagingException) {
                e.printStackTrace()
            }
        }
        thread.start()
    }


}