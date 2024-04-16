package com.example.giuaky1.Fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.Firebase.FirebaseUpdate
import com.example.giuaky1.R
import com.example.giuaky1.databinding.FragmentHistoryBinding
import com.example.giuaky1.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.io.IOException

class ProfileFragment : Fragment() {
    lateinit var binding : FragmentProfileBinding


    private  var filePath : Uri? = null
    final val PICK_IMAGE_REQUEST:Int = 2020







    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater,container,false)



        init_()
        return binding.root
    }

    private fun init_() {
        profile()


        binding.profileImage.setOnClickListener {
            chooseImage()
            binding.saveLayout.visibility =View.VISIBLE

        }
        binding.saveLayout.setOnClickListener {
            filePath?.let { it1 ->
                FirebaseUpdate.uploadImage(it1,requireContext()){

                    binding.saveLayout.visibility = View.GONE
                }
            }

        }

        FirebaseFunction.getPhoneProfile(requireContext(),{phone->
            binding.phoneEdt.isEnabled = false
            binding.phoneEdt.setText(phone)
        },{location->
            binding.locationEdt.isEnabled = false
            binding.locationEdt.setText(location)
        },{dateOfbirth->
            binding.dateEdt.isEnabled = false
            binding.dateEdt.setText(dateOfbirth)
        })

        binding.editPhone.setOnClickListener {
            binding.phoneEdt.isEnabled = true
            viewLifecycleOwner.lifecycleScope.launch {
                openKeyboardAndSetCursorPosition(requireContext(), binding.phoneEdt)
            }
            binding.save.visibility = View.VISIBLE
        }
        binding.editLocation.setOnClickListener {
            binding.locationEdt.isEnabled = true
            openKeyboardAndSetCursorPosition(requireContext(), binding.locationEdt)
            binding.save.visibility = View.VISIBLE
        }
        binding.editdate.setOnClickListener {
            binding.dateEdt.isEnabled = true
            openKeyboardAndSetCursorPosition(requireContext(), binding.dateEdt)
            binding.save.visibility = View.VISIBLE
        }

        binding.save.setOnClickListener {
            checked()
        }
        isNotEnableEditText()






    }

    fun isNotEnableEditText() {
        binding.mainLayout.setOnTouchListener { v, event ->
            if (requireActivity().currentFocus != null) {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireActivity().currentFocus!!.windowToken, 0)
                requireActivity().currentFocus!!.clearFocus()
            }
            true
        }

        binding.mainLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val r = Rect()
                binding.mainLayout.getWindowVisibleDisplayFrame(r)
                val screenHeight = binding.mainLayout.rootView.height
                val keypadHeight = screenHeight - r.bottom
                if (keypadHeight > screenHeight * 0.15) {
                } else {

                    binding.phoneEdt.isEnabled = false
                    binding.locationEdt.isEnabled = false
                    binding.dateEdt.isEnabled = false
                    binding.save.visibility = View.GONE

                    binding.mainLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }

            }
        })
    }


    fun openKeyboardAndSetCursorPosition(context: Context, editText: EditText) {
        editText.requestFocus() // Focus vào EditText
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT) // Hiển thị bàn phím

        // Lưu ý rằng đoạn code sau sẽ không được thực hiện ngay lập tức, vì vậy cần sử dụng postDelayed
        editText.postDelayed({
            editText.setSelection(editText.text.length) // Di chuyển con trỏ đến cuối văn bản trong EditText
        }, 200) // Đợi 200ms trước khi thực hiện
    }


    fun checked(){
        var phone = binding.phoneEdt.text.toString().trim()
        var location = binding.locationEdt.text.toString().trim()
        var date = binding.dateEdt.text.toString().trim()

        if(phone.isEmpty()) phone = ""
        if(location.isEmpty()) location=""
        if (date.isEmpty()) date=""
        FirebaseUpdate.updateDataProfile(requireContext(),phone,location,date)

        if(binding.phoneEdt.isEnabled == true){
            binding.phoneEdt.isEnabled = false
        }
        if(binding.locationEdt.isEnabled == true){
            binding.locationEdt.isEnabled = false
        }
        if(binding.dateEdt.isEnabled == true){
            binding.locationEdt.isEnabled = false
        }
        binding.save.visibility=View.GONE
    }



    private fun chooseImage(){
        val intent : Intent = Intent()
        intent.type =   "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"select Image..."),PICK_IMAGE_REQUEST)
    }


    override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && requestCode != null){
            filePath = data!!.data
            try {
                var bitmap : Bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver,filePath)
                binding.profileImage.setImageBitmap(bitmap)
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private fun profile() {
        val firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val storageRef = FirebaseStorage.getInstance().reference.child("image/")
        val imageRef = storageRef.child(firebaseUser.uid)
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            if(uri.toString() == null){
                binding.profileImage.setImageResource(R.drawable.ic_launcher_foreground)
            }else{
                Glide.with(this@ProfileFragment)
                    .load(uri.toString())
                    .into(binding.profileImage)
            }

        }
        FirebaseFunction
            .getUserDataWithUid(firebaseUser.uid){
            binding.userName.text = it.userName
            binding.emailTv.text = it.email
        }





    }




}