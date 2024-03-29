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



    lateinit var data  : DBHelper
    override fun onCreate() {
        super.onCreate()

        data = DBHelper(this,null)
        val mode = data.getModeList()[0]
        
        if (mode.equals(ModeTheme.dark.toString())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }





}
