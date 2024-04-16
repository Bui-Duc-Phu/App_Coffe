package com.example.giuaky1.Administrator

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import com.example.giuaky1.Models.ModeTheme
import com.example.sqlite.DBHelper
import com.google.firebase.FirebaseApp
import java.util.Locale

// Tạo một class mới kế thừa từ Application


class MyApp : Application() {



    lateinit var mode : String
    lateinit var lang : String
    lateinit var data  : DBHelper
    override fun onCreate() {

        super.onCreate()

        data = DBHelper(this,null)
        val modeList = data.getModeList()

        if (modeList.isEmpty()) {
            createValue()
            mode = ModeTheme.dark.toString() // Thiết lập mode mặc định nếu danh sách rỗng
        } else {
            mode = modeList[0] // Lấy mode từ danh sách nếu có
            lang = modeList[1]
        }

        if (mode == ModeTheme.dark.toString()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        changeLang(applicationContext, lang)


    }


    fun createValue(){
        val  data  : DBHelper = DBHelper(this,null)
        data.addName("1","dark")
        data.addName("2","vi")
    }

    fun changeLang(context: Context, lang: String) {
        val myLocale = Locale(lang)
        Locale.setDefault(myLocale)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(myLocale)
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
    }





}
