package com.example.giuaky1.Activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.giuaky1.R

class SplashScreem : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screem)
        Thread.sleep(2000)
        val intent = Intent(this, LoginOrSignUp::class.java)
        startActivity(intent)
        finish()
    }
}