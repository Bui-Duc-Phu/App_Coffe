package com.example.giuaky1.Fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Adapters.BannerAdapter
import com.example.giuaky1.Adapters.CategoryAdapter
import com.example.giuaky1.Adapters.ProductAdapter
import com.example.giuaky1.Firebase.DataHandler
import com.example.giuaky1.Models.CategoryModel
import com.example.giuaky1.Models.ProductModel
import com.example.giuaky1.R
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {
    private val productModelList = ArrayList<ProductModel>()
    private val searchModelList = ArrayList<ProductModel>()
    private var searchView: SearchView? = null
    private var adapter: ProductAdapter? = null
    private var recyclerViewProducts: RecyclerView? = null
    private var recyclerViewCategory: RecyclerView? = null
    private var currentCategory: CategoryModel? = null
    private var adapterCategory: CategoryAdapter? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var view: View? = null
    private var currentProduct = 0
    private var tvHello: TextView? = null
    private var recyclerViewBanner: RecyclerView? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_home, container, false)
        setControl()
        fetchDataFromFirebase()
        fetchBannerFromFirebase()
        setSearchView()
        loadCategories()
        setHello()
        setRecyclerViewProducts()
        return view
    }

    private fun setRecyclerViewProducts() {
        recyclerViewProducts!!.setLayoutManager(GridLayoutManager(activity, 2))
        adapter = ProductAdapter(productModelList)
        recyclerViewProducts!!.setAdapter(adapter)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun setControl() {
        recyclerViewProducts = view!!.findViewById(R.id.recyclerViewProducts)
        recyclerViewCategory = view!!.findViewById(R.id.recyclerViewCategory)
        searchView = view!!.findViewById(R.id.svCoffees)
        tvHello = view!!.findViewById(R.id.tvHello)
        recyclerViewBanner = view!!.findViewById(R.id.recyclerViewBanner)
    }

    private fun setHello() {
        val calendar = Calendar.getInstance()
        val hour = calendar[Calendar.HOUR_OF_DAY]
        if (hour in 5..9) {
            tvHello!!.setText( R.string.goodmorning)
        } else if (hour in 10..12) {
            tvHello!!.setText( R.string.goodluch)
        } else if (hour in 13..17) {
            tvHello!!.setText( R.string.goodafternoon)
        }  else {
            tvHello!!.setText( R.string.goodnight)
        }
    }

    private fun setSearchView() {
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val filteredList = filter(searchModelList, newText)
                adapter!!.updateList(filteredList)
                recyclerViewProducts!!.setAdapter(adapter)
                return true
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler!!.removeCallbacks(runnable!!)
    }

    private fun fetchBannerFromFirebase() {
        val bannerImages: MutableList<String> = ArrayList()
        FirebaseDatabase.getInstance().getReference("Banner")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.getChildren()) {
                        val bannerUrl = snapshot.getValue(String::class.java)
                        if (bannerUrl != null) {
                            bannerImages.add(bannerUrl)
                        }
                    }
                    setBanner(bannerImages)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("bannerfb", "Fetch data cancelled: " + databaseError.message)
                }
            })
    }

    private fun setBanner(bannerImages: List<String>) {
        val bannerAdapter = BannerAdapter(bannerImages)
        recyclerViewBanner!!.setLayoutManager(
            LinearLayoutManager(
                activity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
        recyclerViewBanner!!.setAdapter(bannerAdapter)
        handler = Handler()
        runnable = object : Runnable {
            override fun run() {
                if (currentProduct >= bannerImages.size) {
                    currentProduct = 0
                    recyclerViewBanner!!.scrollToPosition(currentProduct)
                } else {
                    recyclerViewBanner!!.smoothScrollToPosition(currentProduct)
                }
                currentProduct++
                handler!!.postDelayed(this, 3000)
            }
        }
        handler!!.postDelayed(runnable as Runnable, 3000)
    }

    private fun fetchDataFromFirebase() {
        FirebaseDatabase.getInstance().getReference("Products")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    productModelList.clear()
                    for (snapshot in dataSnapshot.getChildren()) {
                        val productModel = snapshot.getValue(ProductModel::class.java)
                        if (productModel != null) {
                            productModelList.add(productModel)
                            searchModelList.add(productModel)
                        }
                    }
                    adapter!!.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("productsfb", "Fetch data cancelled: " + databaseError.message)
                }
            })
    }

    fun filter(models: List<ProductModel>, query: String): List<ProductModel> {
        val query1 = query.lowercase(Locale.getDefault())
        val filteredList: MutableList<ProductModel> = ArrayList()
        for (model in models) {
            val text = model.name!!.lowercase(Locale.getDefault())
            if (text.contains(query1)) {
                filteredList.add(model)
            }
        }
        return filteredList
    }

    private fun loadCategories() {
        val layoutManager = FlexboxLayoutManager(activity)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.SPACE_EVENLY
        recyclerViewCategory!!.setLayoutManager(layoutManager)
        FirebaseDatabase.getInstance().getReference("Categories")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val categoryList: MutableList<CategoryModel?> = ArrayList()
                    for (categorySnapshot in dataSnapshot.getChildren()) {
                        val category = categorySnapshot.getValue(
                            CategoryModel::class.java
                        )
                        categoryList.add(category)
                    }
                    adapterCategory = CategoryAdapter(
                        categoryList, object : CategoryAdapter.OnCategoryClickListener {
                            override fun onCategoryClick(selectedCategory: CategoryModel?) {
                                currentCategory = selectedCategory
                                if (currentCategory!!.name == "All") {
                                    adapter!!.updateList(productModelList)
                                    adapter!!.notifyDataSetChanged()
                                } else {
                                    filterProducts(currentCategory)
                                }
                                adapterCategory!!.notifyDataSetChanged()
                            }
                        })
                    recyclerViewCategory!!.setAdapter(adapterCategory)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterProducts(selectedCategory: CategoryModel?) {
        val filteredList: MutableList<ProductModel> = ArrayList()
        for (product in productModelList) {
            if (product.category != null && selectedCategory!!.name != null && product.category == selectedCategory.name) {
                filteredList.add(product)
            }
        }
        adapter!!.updateList(filteredList)
        adapter!!.notifyDataSetChanged()
    }
}