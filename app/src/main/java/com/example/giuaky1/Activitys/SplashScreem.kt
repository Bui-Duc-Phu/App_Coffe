package com.example.giuaky1.Activitys

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.giuaky1.Administrator.MyApp
import com.example.giuaky1.R
import com.example.giuaky1.Ultils.CommonUtils
import com.example.sqlite.DBHelper
import java.util.Locale

class SplashScreem : AppCompatActivity() {
    private val SPLASH_DELAY: Long = 2000
    lateinit var data  : DBHelper

    private var progressDialog2 : Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screem)



        data = DBHelper(this,null)
        changeLang(this,data.getModeList()[1])


        Handler().postDelayed({
            val intent = Intent(this, LoginOrSignUp::class.java)
            startActivity(intent)

        }, SPLASH_DELAY)

    }

    private fun showProgress(){
        hideProgress()
        progressDialog2 = CommonUtils.showLoadingDialog(this)
    }

    private fun hideProgress(){
        progressDialog2?.let { if(it.isShowing) it.dismiss() }
    }


    fun changeLang(context: Context, lang: String) {

        val myLocale = Locale(lang)
        Locale.setDefault(myLocale)
        val config = Configuration()
        config.locale = myLocale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}
