package com.example.giuaky1.Firebase

import android.app.Activity
import android.content.Context
import android.util.Patterns
import android.widget.Toast
import com.example.giuaky1.Ultils.CustomString
import com.google.android.play.integrity.internal.w
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

class OTP_Athen_Phone {
    companion object{
        val firebaseAuth = FirebaseAuth.getInstance()



        fun sendOtp(phone:String,activity:Activity,  callback: (String) -> Unit ){

           val phonevn = "+84" + CustomString.subStringPhone(phone)
           val e164PhoneNumber = toE164PhoneNumber(phonevn, "VN").toString()

            println("phone : $e164PhoneNumber")
            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(e164PhoneNumber)
                .setTimeout(120L,TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                        Toast.makeText(activity, "otp send successfull", Toast.LENGTH_SHORT).show()
                    }
                    override fun onVerificationFailed(e: FirebaseException) {
                        Toast.makeText(activity, "otp send false", Toast.LENGTH_SHORT).show()
                        println("bug : ${e.message}")
                    }
                    override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                        super.onCodeSent(s, forceResendingToken)
                        callback(s)
                        println("id : $s")
                    }
                }).build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }


        fun OTPAuthenAndRegister(credentialId:String,otpByUser:String,context: Context,callback: (Boolean) -> Unit){
            val credential = PhoneAuthProvider.getCredential(credentialId, otpByUser)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(true)
                    } else {
                        Toast.makeText(context, "OTP Authen False", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        private fun toE164PhoneNumber(phoneNumber: String, vn: String): String? {
            val phoneNumberUtil = PhoneNumberUtil.getInstance()
            try {
                val parsedPhoneNumber: Phonenumber.PhoneNumber = phoneNumberUtil.parse(phoneNumber, null)
                return phoneNumberUtil.format(parsedPhoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164)
            } catch (e: NumberParseException) {
                e.printStackTrace()
            }
            return null
        }





















    }
}