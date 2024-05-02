package com.example.giuaky1.Administrator.Chats

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.giuaky1.Chats.Chat
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.R
import com.example.giuaky1.databinding.MessageItemLeftBinding
import com.example.giuaky1.databinding.MessageItemRightBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage


class ChatAdapterAdmin(val context: Context, val list: ArrayList<Chat>) : RecyclerView.Adapter<ChatAdapterAdmin.ViewHolder>() {

    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1
    var firebaseUser: FirebaseUser? = null
    val  adminKey = "ACCOUNT_ADMIN_TYPE_2"


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == MESSAGE_TYPE_RIGHT) {
            val binding = MessageItemRightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewHolder(binding)
        } else {
            val binding = MessageItemLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewHolder(binding)
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (list[position].senderId == adminKey) MESSAGE_TYPE_RIGHT else MESSAGE_TYPE_LEFT

    }

    inner class ViewHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            when (binding) {
                is MessageItemRightBinding ->{
                    binding.messageTV.text = chat.message
                }
                is MessageItemLeftBinding -> {
                    binding.messageTV.text = chat.message
                    println("uid " + chat.senderId)
                    FirebaseFunction.getUserDataWithUid(chat.senderId){
                        binding.senderName.text = it.userName
                        println(it)

                    }

                    val storageRef = FirebaseStorage.getInstance().reference.child("image/")
                    val imageRef = storageRef.child(chat.senderId)
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        if(uri.toString() == null){
                            binding.imageGchatProfileOther.setImageResource(R.drawable.man)
                        }else{
                            Glide.with(context).load(uri.toString()).into(binding.imageGchatProfileOther)
                        }

                    }


                }
                // Handle other bindings if necessary
            }
        }
    }


}