package com.example.giuaky1.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
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
import java.util.Locale

class HomeFragment : Fragment() {
    var productModelList: ArrayList<ProductModel> = ArrayList()
    var searchModelList: ArrayList<ProductModel> = ArrayList()
    lateinit var searchView: SearchView
    lateinit var adapter: ProductAdapter
    private lateinit var recyclerView: RecyclerView
    val categories = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewProducts)
        recyclerView.layoutManager = GridLayoutManager(activity, 2)
        adapter = ProductAdapter(productModelList)
        recyclerView.adapter = adapter
        searchView = view.findViewById(R.id.svCoffees)
        fetchDataFromFirebase()

        return view
    }
    private fun fetchDataFromFirebase() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Products")
        databaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                productModelList.clear()
                searchModelList.clear()
                for (snapshot in dataSnapshot.children) {
                    val productModel: ProductModel? = snapshot.getValue(ProductModel::class.java)
                    if (productModel != null) {
                        productModelList.add(productModel)
                        searchModelList.add(productModel)
                    }
                }
                adapter.notifyDataSetChanged()

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        val filteredList = filter(searchModelList, newText)
                        adapter.updateList(filteredList)
                        recyclerView.adapter = adapter
                        return true
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("productsfb", "Fetch data cancelled: ${databaseError.message}")
            }
        })
        val databaseReference1 = FirebaseDatabase.getInstance().getReference("Category")
        databaseReference1.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                categories.clear()
                for (snapshot in dataSnapshot.children) {
                    val category = snapshot.getValue(String::class.java)
                    if (category != null) {
                        categories.add(category)
                    }
                }
                adapter.notifyDataSetChanged()
                Log.d("categories", categories.size.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("categoriesfb", "Fetch data cancelled: ${databaseError.message}")
            }
        })
    }
    fun filter(models: List<ProductModel>, query: String): List<ProductModel> {
        val query1=query.lowercase(Locale.getDefault())
        val filteredList: MutableList<ProductModel> = java.util.ArrayList()
        for (model in models) {
            val text: String = model.name.lowercase(Locale.getDefault())
            if (text.contains(query1)) {
                filteredList.add(model)
            }
        }
        return filteredList
    }
}
