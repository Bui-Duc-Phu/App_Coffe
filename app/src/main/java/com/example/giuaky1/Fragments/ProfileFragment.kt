package com.example.giuaky1.Fragments

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.Firebase.FirebaseUpdate
import com.example.giuaky1.R
import com.example.giuaky1.databinding.FragmentProfileBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfileFragment : Fragment() {
    lateinit var binding : FragmentProfileBinding

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private  var filePath : Uri? = null
    final val PICK_IMAGE_REQUEST:Int = 2020







    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        init_()
        return binding.root
    }

    private fun init_() {
        profile()


        binding.profileImage.setOnClickListener {
            chooseImage()
            binding.saveBtn.visibility = View.VISIBLE
        }
        binding.saveBtn.setOnClickListener {
            filePath?.let { it1 ->
                FirebaseUpdate.uploadImage(it1,requireContext()){
                    binding.saveBtn.visibility = View.GONE
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
        },{name->
            binding.nameEdt.isEnabled = false
            binding.nameEdt.setText(name)
        }
        )

        binding.editPhone.setOnClickListener {
            binding.phoneEdt.isEnabled = true
            viewLifecycleOwner.lifecycleScope.launch {
                openKeyboardAndSetCursorPosition(requireContext(), binding.phoneEdt)
            }
            binding.save.visibility = View.VISIBLE
        }
        binding.editLocation.setOnClickListener {
            binding.locationEdt.isEnabled = true
            updateAddressFromLocation()
            binding.save.visibility = View.VISIBLE
        }
        binding.editdate.setOnClickListener {
            binding.dateEdt.isEnabled = true
                val now = Calendar.getInstance()
                DatePickerDialog(
                    requireContext(),
                    { _, year, monthOfYear, dayOfMonth ->
                        val selectedDate = Calendar.getInstance()
                        selectedDate.set(Calendar.YEAR, year)
                        selectedDate.set(Calendar.MONTH, monthOfYear)
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        val dateStr = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(selectedDate.time)

                        binding.dateEdt.setText(dateStr)
                    },
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
                ).show()

        }

        binding.save.setOnClickListener {
            checked()
        }
        binding.edtName.setOnClickListener {
            binding.nameEdt.isEnabled = true
            viewLifecycleOwner.lifecycleScope.launch {
                openKeyboardAndSetCursorPosition(requireContext(), binding.nameEdt)
            }
            binding.save.visibility = View.VISIBLE
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
        var name = binding.nameEdt.text.toString().trim()
        var mail= binding.emailTv.text.toString().trim()
        if(phone.isEmpty()) phone = ""
        if(location.isEmpty()) location=""
        if (date.isEmpty()) date=""
        FirebaseUpdate.updateDataProfile(requireContext(),phone,location,date,name,mail)

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


    private fun updateAddressFromLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED

        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )
            return
        }
        fusedLocationClient!!.lastLocation
            .addOnSuccessListener(requireActivity()) { location ->
                if (location != null) {
                    getAddressFromCoordinates(location.latitude, location.longitude)
                }
            }

    }

    private fun getAddressFromCoordinates(latitude: Double, longitude: Double) {
        val url =
            "https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=$latitude&lon=$longitude"
        val jsonObjectRequest =
            JsonObjectRequest(Request.Method.GET, url, null, { response: JSONObject ->
                try {
                    val addressObject = response.getJSONObject("address")
                    val road = addressObject.getString("road")
                    //       String houseNumber = addressObject.optString("house_number", "");
                    val quarter = addressObject.optString("quarter", "")
                    val suburb = addressObject.optString("suburb", "")
                    val city = addressObject.getString("city")
                    var address = road
                    if (quarter.isNotEmpty()) {
                        address += ", $quarter"
                    }
                    if (suburb.isNotEmpty()) {
                        address += ", $suburb"
                    }
                    address += ", $city"
                    binding.locationEdt!!.setText(address)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(requireActivity(), "Không thể lấy địa chỉ", Toast.LENGTH_SHORT)
                        .show()
                }
            }) { error: VolleyError ->
                Log.d("Error.Response", error.toString())
                Toast.makeText(requireActivity(), "Không thể lấy địa chỉ", Toast.LENGTH_SHORT).show()
            }
        Volley.newRequestQueue(requireActivity()).add(jsonObjectRequest)
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