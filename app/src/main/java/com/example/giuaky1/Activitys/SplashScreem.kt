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

    lateinit var lang : String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screem)


        data = DBHelper(this,null)
        val modeList = data.getModeList()




        Handler().postDelayed({
            if (modeList.isEmpty()) {
                createValue()
            }
            val intent = Intent(this, LoginOrSignUp::class.java)
            startActivity(intent)

        }, SPLASH_DELAY)

    }

    fun createValue(){
        val  data  : DBHelper = DBHelper(this,null)
        data.addName("1","light")
        data.addName("2","vi")
    }







}
