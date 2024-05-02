package com.example.giuaky1.Chats

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.R
import com.example.giuaky1.Ultils.RetrofitInstance
import com.example.giuaky1.databinding.ActivityChatMainBinding
import com.example.messengerapp.model.NotificationData
import com.example.messengerapp.model.PushNotification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatMain : AppCompatActivity() {
     val  adminKey = "ACCOUNT_ADMIN_TYPE_2"
      var topic = ""



    private val binding: ActivityChatMainBinding by lazy {
        ActivityChatMainBinding.inflate(layoutInflater)
    }
    lateinit var firebaseUser: FirebaseUser
    lateinit var reference: DatabaseReference
    lateinit var listChat: ArrayList<Chat>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firebaseUser =FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase
            .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Users")

        listChat = ArrayList()


        init_()
    }

    private fun init_() {
        MessageEdt(adminKey)
        readMessager(firebaseUser.uid!!,adminKey)
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }





    }

    private fun MessageEdt(receiverId: String) {
        binding.mesageEdt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_NULL) {
                // Xử lý sự kiện ở đây
                val message = binding.mesageEdt.text.toString()
                if (message.isEmpty()) {
                    Toast.makeText(applicationContext, "Text is empty", Toast.LENGTH_SHORT).show()
                } else {
                    sendMessage(firebaseUser.uid!!, receiverId, message)
                }
                val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.mesageEdt.windowToken, 0)
                true // Trả về true để xác nhận rằng sự kiện đã được xử lý
            } else {
                false
            }
        }
    }







    private fun sendMessage(senderId:String,receiverId:String,message:String) {
        val firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val ref = FirebaseDatabase
            .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Chats")
        var handMap : HashMap<String,String> = HashMap()
        handMap.put("senderId",senderId)
        handMap.put("receiverId",receiverId)
        handMap.put("message",message)
        handMap.put("forDay",message)
        handMap.put("realtime",message)
        ref.push()
            .setValue(handMap!!)
            .addOnSuccessListener {
                binding.mesageEdt.setText("")
            }

    }


    private fun readMessager(senderId: String, receiverId: String) {
        val ref = FirebaseDatabase
            .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Chats")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                listChat.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapshot.getValue(Chat::class.java)
                    println(chat)
                    if (chat!!.senderId.equals(senderId) && chat!!.receiverId.equals(receiverId) ||
                        chat!!.senderId.equals(receiverId) && chat!!.receiverId.equals(senderId)
                    ) {
                        listChat.add(chat)
                    }
                }
                val adapter = ChatAdapter(this@ChatMain, listChat)
                binding.recylerview.adapter = adapter
                println(listChat)
                val lastItemPosition = adapter.itemCount - 1
                if (lastItemPosition >= 0) {
                    binding.recylerview.scrollToPosition(lastItemPosition)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }



}