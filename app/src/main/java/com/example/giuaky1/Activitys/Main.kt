package com.example.giuaky1.Activitys

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.giuaky1.Models.ModeTheme
import com.example.giuaky1.R

import com.example.giuaky1.databinding.ActivityMainBinding
import com.example.sqlite.DBHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale


class Main : AppCompatActivity(){
    lateinit var auth: FirebaseAuth
    lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var toggle: ActionBarDrawerToggle

    private val PREF_LANG_KEY = "Language"





    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()






        init_()





    }

    private fun init_() {
        navigationDrawer()
        buttonNavigation()
        setSupportActionBar(binding.toolbar)


    }

    private fun buttonNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun navigationDrawer(){
        val themeItem = binding.navView.menu.findItem(R.id.lightMode)
        if (isDarkMode()) {
            themeItem.title = getString(R.string.light_mode)
            themeItem.setIcon(R.drawable.sun)
        } else {
            themeItem.title = getString(R.string.dark_mode)
            themeItem.setIcon(R.drawable.moon)
        }

        val languageItem  = binding.navView.menu.findItem(R.id.setLanguage)



        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView.setNavigationItemSelectedListener { menu ->
            when (menu.itemId) {
              R.id.changePassword ->  {
                  Toast.makeText(applicationContext, "changePasswrod", Toast.LENGTH_SHORT).show()

              }
              R.id.setLanguage ->{
                  when{
                      isVietnameseLanguage(this) -> { changeLang(this,"en")
                      recreate()}
                      else ->{ changeLang(this,"vi")
                      recreate()}
                  }
              }
              R.id.lightMode ->{
                 toggleNightMode()
              }
              R.id.privacyPolicy ->{

              }
              R.id.notification ->{

              }
              R.id.nav_logout ->{
                  val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                      .requestIdToken(getString(R.string.default_web_client_id))
                      .requestEmail()
                      .build()
                  googleSignInClient = GoogleSignIn.getClient(this, gso)
                  googleSignInClient.revokeAccess().addOnCompleteListener(this) {}
                  googleSignInClient.signOut().addOnCompleteListener(this){}
                  startActivity(Intent(this, LoginOrSignUp::class.java))
              }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun toggleNightMode() {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        val newNightMode = when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> AppCompatDelegate.MODE_NIGHT_YES
            Configuration.UI_MODE_NIGHT_YES -> AppCompatDelegate.MODE_NIGHT_NO
            else -> return
        }

        val confirmationDialog = AlertDialog.Builder(this)
            .setTitle("Cập nhật theme")
            .setMessage("Bạn có muốn cập nhật theme không?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                AppCompatDelegate.setDefaultNightMode(newNightMode)
                updateDataMode()
                recreate()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setOnCancelListener {
                it.dismiss()
            }
            .create()

        confirmationDialog.show()
    }

    private fun updateDataMode(){
        val dbHelper = DBHelper(this,null)
        if(dbHelper.getModeList()[0].toString().equals(ModeTheme.dark.toString())){
            dbHelper.updateMode("1", ModeTheme.light.toString())
        }else{
            dbHelper.updateMode("1", ModeTheme.dark.toString())
        }
    }

    fun isDarkMode(): Boolean {
        val currentMode = this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentMode == Configuration.UI_MODE_NIGHT_YES
    }




    fun changeLang(context: Context, lang: String) {
        val myLocale = Locale(lang)
        Locale.setDefault(myLocale)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(myLocale)
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)


        val dbHelper = DBHelper(this,null)
        if( lang == "vi") dbHelper.updateMode("2", "vi")
        else dbHelper.updateMode("2", "en")
    }

    fun isVietnameseLanguage(context: Context): Boolean {
        val currentLocale = context.resources.configuration.locale
        return currentLocale == Locale("vi")
    }








}