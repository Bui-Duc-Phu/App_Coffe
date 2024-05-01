package com.example.giuaky1.Activitys

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

import com.example.giuaky1.Administrator.Activitys.MainAdmin
import com.example.giuaky1.Administrator.Controller
import com.example.giuaky1.Data.PustData
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.Models.Users
import com.example.giuaky1.R
import com.example.giuaky1.Ultils.CommonUtils
import com.example.giuaky1.databinding.ActivityLoginOrSignUpBinding
import com.example.sqlite.DBHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginOrSignUp : AppCompatActivity() {


    lateinit var googleSignInClient : GoogleSignInClient
    lateinit var auth: FirebaseAuth
    lateinit var firebaseUser: FirebaseUser
    lateinit var databaseReference:DatabaseReference
    private var progressDialog: ProgressDialog? = null
    lateinit var gso: GoogleSignInOptions
    lateinit var data  : DBHelper


    private var progressDialog2 : Dialog? = null



    private val binding: ActivityLoginOrSignUpBinding by lazy {
        ActivityLoginOrSignUpBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        showProgress()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        data = DBHelper(this,null)

        Handler().postDelayed({
         hideProgress()
        }, 1000)



        auth = FirebaseAuth.getInstance()
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this , gso)





        init_()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideProgress()
    }

    private fun init_() {
        binding.apply {
            signBtn.setOnClickListener {
                startActivity(Intent(this@LoginOrSignUp, SignUp::class.java))

            }
            loginBtn.setOnClickListener {
                autoLogin()
            }
            googleBtn.setOnClickListener {
               signInGoogle()
//                startActivity(Intent(this@LoginOrSignUp, PustData::class.java))

            }

        }




    }
    private fun showProgress(){
        hideProgress()
        progressDialog2 = CommonUtils.showLoadingDialog(this);
    }
    private fun hideProgress(){
        progressDialog2?.let { if(it.isShowing)it.cancel() }
    }



    private fun signInGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                val email = account.email.toString()
                if (!email.isNullOrEmpty()) {
                    checkMail(email!!) {
                        if(it){
                            Toast.makeText(applicationContext, "Email này đã được đăng ký 1", Toast.LENGTH_SHORT).show()
                            googleSignInClient = GoogleSignIn.getClient(this, gso)
                            googleSignInClient.revokeAccess().addOnCompleteListener(this) {}
                            googleSignInClient.signOut().addOnCompleteListener(this){}
                        }else{
                            updateUI(account)
                        }
                    }

                } else {
                    Toast.makeText(this, "Không thể truy cập thông tin email của tài khoản Google này.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }






    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { signInTask ->
            if (signInTask.isSuccessful) {
                val user: FirebaseUser? = auth.currentUser
                val userid: String = user!!.uid
                databaseReference = FirebaseDatabase
                    .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("Users")
                    .child(userid)
                // Kiểm tra xem người dùng đã được tạo trong database chưa
                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) { // Nếu không có dữ liệu
                            // Ghi thông tin người dùng vào database
                            val hashmap: HashMap<String, Any> = HashMap()
                            hashmap["userID"] = userid
                            hashmap["userName"] = account.displayName.toString()
                            hashmap["email"] = account.email.toString()
                            hashmap["password"] = ""
                            hashmap.put("typeAccount","3")
                            databaseReference.setValue(hashmap).addOnCompleteListener { databaseTask ->
                                if (databaseTask.isSuccessful) {
                                    FirebaseFunction.WriteDeviceId(applicationContext,  userid )
                                    startActivity(Intent(this@LoginOrSignUp, Main::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(this@LoginOrSignUp, databaseTask.exception.toString(), Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Controller.permission(applicationContext,account.email.toString()){
                                if(it){
                                    startActivity(Intent(this@LoginOrSignUp,MainAdmin::class.java))
                                    finish()
                                }else {
                                    FirebaseFunction.WriteDeviceId(applicationContext,  userid )
                                    startActivity(Intent(this@LoginOrSignUp, Main::class.java))
                                    finish()
                                }
                            }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@LoginOrSignUp, error.message, Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this@LoginOrSignUp, signInTask.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun checkMail(email: String, callback: (Boolean) -> Unit) {
        val ref = FirebaseDatabase
            .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var isEmailExists = false
                for (snapshot in snapshot.children) {
                    val user = snapshot.getValue(Users::class.java)
                    if (user!!.email.equals(email) && user.typeAccount.equals("1")) {
                        isEmailExists = true
                        break
                    }
                }
                callback(isEmailExists)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Lỗi khi truy xuất dữ liệu từ Firebase", Toast.LENGTH_SHORT).show()
                callback(true)
            }
        })
    }

    fun autoLogin(){
        if(auth.currentUser != null){
            progressDialog = ProgressDialog.show(this, "App", "Login...", true)
            FirebaseFunction.getUserDataWithUid(auth!!.currentUser!!.uid.toString()){ user->
                println("user " + user)
                if(user.typeAccount.equals("2")){
                    progressDialog!!.dismiss()
                    FirebaseFunction.WriteDeviceId(applicationContext,  auth.currentUser!!.uid.toString() )
                    startActivity(Intent(this, MainAdmin::class.java))
                    finish()
                }else if(user.typeAccount.equals("1")) {
                    progressDialog!!.dismiss()
                    FirebaseFunction.WriteDeviceId(applicationContext,  auth.currentUser!!.uid.toString() )
                    startActivity(Intent(this, Main::class.java))
                    finish()

                }else{

                    progressDialog!!.dismiss()
                    startActivity(Intent(this@LoginOrSignUp, Login::class.java))
                }
            }
        }else{
            startActivity(Intent(this@LoginOrSignUp, Login::class.java))
        }
    }










}