package com.example.giuaky1.Administrator.Activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.giuaky1.Activitys.LoginOrSignUp
import com.example.giuaky1.R
import com.example.giuaky1.databinding.ActivityMainAdminBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainAdmin : AppCompatActivity() {
    private val binding : ActivityMainAdminBinding by lazy {
        ActivityMainAdminBinding.inflate(layoutInflater)
    }

    lateinit var auth: FirebaseAuth
    lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init_()
    }

    private fun init_() {
        navigationDrawer()
        buttonNavigation()

    }





    private fun buttonNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun navigationDrawer(){





        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)




        binding.navView.setNavigationItemSelectedListener { menu ->
            when (menu.itemId) {
                R.id.orderManager ->  {
                    startActivity(Intent(this@MainAdmin,OrderList::class.java))
                }
                R.id.orderManager_Users ->  {
                    startActivity(Intent(this@MainAdmin,OrderList::class.java))
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


}