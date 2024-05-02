package com.example.giuaky1.Activitys

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController

import com.example.giuaky1.Firebase.FirebaseFunction

import androidx.viewpager2.widget.ViewPager2
import com.example.giuaky1.Adapters.AdapterViewPager
import com.example.giuaky1.Chats.ChatMain
import com.example.giuaky1.Firebase.DataHandler
import com.example.giuaky1.Firebase.DataHandler.countItemsInCart
import com.example.giuaky1.Firebase.FirebaseService
import com.example.giuaky1.Firebase.FirebaseUpdate
import com.example.giuaky1.Fragments.CartFragment
import com.example.giuaky1.Fragments.HistoryFragment
import com.example.giuaky1.Fragments.HomeFragment
import com.example.giuaky1.Fragments.ProfileFragment

import com.example.giuaky1.Models.ModeTheme
import com.example.giuaky1.R

import com.example.giuaky1.databinding.ActivityMainBinding
import com.example.sqlite.DBHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import java.util.Locale


class Main : AppCompatActivity(){
    lateinit var auth: FirebaseAuth
    lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var toggle: ActionBarDrawerToggle

    private val PREF_LANG_KEY = "Language"



    private val fragmentArrayList = ArrayList<Fragment>()

    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        DataHandler.getInforPDF {
            DataHandler.userInfo=it
        }
        devices()
//        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
//        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                FirebaseService.token = task.result
//            } else {
//                // Handle token retrieval failure here
//                Toast.makeText(applicationContext, "get token Fail..", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        val fireBase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
//        var userid = fireBase.uid
//        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userid")






        init_()





    }

    private fun init_() {
        navigationDrawer()

        setSupportActionBar(binding.toolbar)
        setupViewPagerAndBottomNav()
        setUpBadge()

        binding.chatBtn.setOnClickListener {
            startActivity(Intent(this@Main,ChatMain::class.java))
        }

    }

    private fun setUpBadge() {
        val badgeDrawable = binding.bottomNavigationView!!.getOrCreateBadge(R.id.cartFragment)
        countItemsInCart(DataHandler.CartItemCountCallback { count: Int ->
            if (count > 0) {
                badgeDrawable.number = count
                badgeDrawable.isVisible = true
            } else {
                badgeDrawable.isVisible = false
            }
        })
    }


    private fun setupViewPagerAndBottomNav() {
        fragmentArrayList.add(HomeFragment())
        fragmentArrayList.add(CartFragment())
        fragmentArrayList.add(HistoryFragment())
        fragmentArrayList.add(ProfileFragment())
        binding.viewPager2!!.setAdapter(AdapterViewPager(this, fragmentArrayList))
        binding.viewPager2!!.setUserInputEnabled(false)
        binding.bottomNavigationView!!.setOnItemSelectedListener { item: MenuItem ->
            if (item.itemId == R.id.homeFragment) {
                binding.viewPager2!!.setCurrentItem(0, true)
            }
            if (item.itemId == R.id.cartFragment) {
                binding.viewPager2!!.setCurrentItem(1, true)
            }
            if (item.itemId == R.id.historyFragment) {
                binding.viewPager2!!.setCurrentItem(2, true)
            }
            if (item.itemId == R.id.profileFragment) {
                binding.viewPager2!!.setCurrentItem(3, true)
            }
            true
        }
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
                  isAccountGoogle {
                      if(!it)startActivity(Intent(this@Main,ChangePassword::class.java))
                  }

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
              R.id.notification -> loadNotification()
              R.id.nav_logout ->{
                  val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                      .requestIdToken(getString(R.string.default_web_client_id))
                      .requestEmail()
                      .build()
                  googleSignInClient = GoogleSignIn.getClient(this, gso)
                  googleSignInClient.revokeAccess().addOnCompleteListener(this) {}
                  googleSignInClient.signOut().addOnCompleteListener(this){}
                  auth.signOut()
                  val firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser!!
                  FirebaseUpdate.deleteDriver(firebaseUser.uid.toString()){
                      if(!it) Toast.makeText(applicationContext, "delete driver failse", Toast.LENGTH_SHORT).show()
                  }
                  startActivity(Intent(this, LoginOrSignUp::class.java))
              }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

    }
    private fun isAccountGoogle(callback : (Boolean)->Unit){
        val firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        FirebaseFunction.getUserDataWithUid(firebaseUser.uid){
            if(it.typeAccount.equals("3")){
                Toast.makeText(applicationContext, "Account google, not changed password", Toast.LENGTH_SHORT).show()
                callback(true)
            }else{
                callback(false)
            }
        }
    }

    private fun loadNotification() {

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



   private fun  devices(){
       val firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        FirebaseFunction.evenLogOut(applicationContext,firebaseUser.uid.toString()){
            if(!it){
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                googleSignInClient = GoogleSignIn.getClient(this, gso)
                googleSignInClient.revokeAccess().addOnCompleteListener(this) {}
                googleSignInClient.signOut().addOnCompleteListener(this){}
                auth.signOut()
                FirebaseUpdate.deleteDriver(firebaseUser.uid.toString()){
                    if(!it) Toast.makeText(applicationContext, "delete driver failse", Toast.LENGTH_SHORT).show()
                }
                startActivity(Intent(this, LoginOrSignUp::class.java))
            }
        }
   }

















}