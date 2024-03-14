package com.example.giuaky1.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Adapters.ProductAdapter
import com.example.giuaky1.Models.ProductModel
import com.example.giuaky1.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {
    var productModelList: ArrayList<ProductModel> = ArrayList()
    lateinit var adapter: ProductAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewProducts)
        recyclerView.layoutManager = GridLayoutManager(activity, 2)
        adapter = ProductAdapter(productModelList)
        recyclerView.adapter = adapter
        fetchDataFromFirebase()
        return view
    }
    private fun fetchDataFromFirebase() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Products")
        databaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val productModel: ProductModel? = snapshot.getValue(ProductModel::class.java)
                    if (productModel != null) {
                        productModelList.add(productModel)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("productsfb", "Fetch data cancelled: ${databaseError.message}")
            }
        })
    }

}
