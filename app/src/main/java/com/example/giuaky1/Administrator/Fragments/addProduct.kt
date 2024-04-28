package com.example.giuaky1.Administrator.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Administrator.Activitys.AddCategory
import com.example.giuaky1.Administrator.Activitys.AddProduct
import com.example.giuaky1.Administrator.Adapters.CoffeAdapter
import com.example.giuaky1.Administrator.model.Size
import com.example.giuaky1.Administrator.model.coffeModel
import com.example.giuaky1.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener

class addProduct : Fragment() {
    var btnadd: Button? = null
    var btnAddCategory: Button? = null
    private var recyclerView: RecyclerView? = null
    private var productAdapter: CoffeAdapter? = null
    var productList: MutableList<coffeModel?> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)
        setControl(view)
        setEvent()
        layDuLieuTuFireBase()
        return view
    }

    private fun layDuLieuTuFireBase() {
        val productReference = FirebaseDatabase.getInstance().getReference().child("Products")
        productReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                productList.clear()
                for (snapshot in dataSnapshot.getChildren()) {
                    val product = coffeModel()
                    product.name = snapshot.child("name").getValue(String::class.java)
                    product.imageUrl = snapshot.child("imageUrl").getValue(String::class.java)
                    product.price = snapshot.child("price").getValue(Int::class.java) ?: 0
                    product.discount = snapshot.child("discount").getValue(Int::class.java) ?: 0
                    product.category = snapshot.child("category").getValue(String::class.java)
                    product.sizes = snapshot.child("sizes").getValue(object : GenericTypeIndicator<Map<String, Size>>() {})

                    productList.add(product)
                    Log.d("Firebase", "Value is: $product")
                }
                productAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error: " + databaseError.message)
            }
        })
    }

    private fun setControl(view: View) {
        btnadd = view.findViewById(R.id.btnAdd)
        recyclerView = view.findViewById(R.id.danhsachsanpham)
        productAdapter = CoffeAdapter(requireContext(), productList)
        recyclerView!!.setLayoutManager(GridLayoutManager(requireContext(), 2))
        recyclerView!!.setAdapter(productAdapter)
        btnAddCategory= view.findViewById(R.id.btnAddCategory)
    }

    private fun setEvent() {
        btnadd!!.setOnClickListener { v: View? ->
            val intent = Intent(requireContext(), AddProduct::class.java)
            startActivity(intent)
        }
        btnAddCategory!!.setOnClickListener { v: View? ->
            val intent = Intent(requireContext(), AddCategory::class.java)
            startActivity(intent)
        }
    }
}

