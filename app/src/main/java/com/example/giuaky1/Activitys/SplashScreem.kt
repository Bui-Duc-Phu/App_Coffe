package com.example.giuaky1.Activitys


import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

import com.example.giuaky1.R

import com.example.sqlite.DBHelper


class SplashScreem : AppCompatActivity() {
    private val SPLASH_DELAY: Long = 2000
    lateinit var data  : DBHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screem)



        Handler().postDelayed({
            val intent = Intent(this, LoginOrSignUp::class.java)
            startActivity(intent)

        }, SPLASH_DELAY)

    }







}
