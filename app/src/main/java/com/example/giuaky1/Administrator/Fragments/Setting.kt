package com.example.giuaky1.Administrator.Fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.giuaky1.Administrator.Activitys.MapsActivity
import com.example.giuaky1.R
import com.google.firebase.database.FirebaseDatabase

class Setting : Fragment() {

    private var etAddress: EditText? = null
    private var save: Button? = null
    private var database = FirebaseDatabase.getInstance().getReference("Addresses")

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_setting, container, false)
        loadDiaChiTuFireBase()
        etAddress = view.findViewById(R.id.etAddress)
        save = view.findViewById(R.id.Save)
        etAddress!!.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (etAddress!!.right - etAddress!!.compoundDrawables[2].bounds.width())) {
                    val intent = Intent(context, MapsActivity::class.java)
                    startActivityForResult(intent, 1)
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }

        save!!.setOnClickListener {
            val address = etAddress?.text.toString()
            database.setValue(address)
            Toast.makeText(context, "Đã lưu địa chỉ", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun loadDiaChiTuFireBase() {
        database.get().addOnSuccessListener {
            if (it.exists()) {
                val address = it.value.toString()
                etAddress?.setText(address)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val selectedAddress = data?.getStringExtra("selectedAddress")
            etAddress?.setText(selectedAddress)
        }
    }
}