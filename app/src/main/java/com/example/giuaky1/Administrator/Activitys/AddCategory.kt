package com.example.giuaky1.Administrator.Activitys

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.giuaky1.Administrator.model.CategoryModel
import com.example.giuaky1.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddCategory() : AppCompatActivity() {
    private var edtCategory: EditText? = null
    private var btnAddCategory: Button? = null
    private var spinnerCategory: Spinner? = null
    private var btnEditCategory: Button? = null
    private var btnDeleteCategory: Button? = null
    private var adapter: ArrayAdapter<String?>? = null
    private var categories: MutableList<String?>? = null
    var btnBack: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)
        edtCategory = findViewById(R.id.edtCategory)
        btnAddCategory = findViewById(R.id.btnAddCategory)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnEditCategory = findViewById(R.id.btnEditCategory)
        btnDeleteCategory = findViewById(R.id.btnDeleteCategory)
        btnBack = findViewById(R.id.btnBack)
        categories = ArrayList()
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            categories as ArrayList<String?>
        )
        adapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory!!.setAdapter(adapter)
        loadCategories()
        btnAddCategory!!.setOnClickListener { addCategoryToDatabase() }
        btnEditCategory!!.setOnClickListener {
            editCategoryInDatabase(
                spinnerCategory!!.getSelectedItem().toString()
            )
        }
        btnDeleteCategory!!.setOnClickListener { v: View? -> deleteCategoryFromDatabase() }
        btnBack!!.setOnClickListener { v: View? -> onBackPressed() }
        spinnerCategory!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedCategory = parent.getItemAtPosition(position).toString()
                edtCategory!!.setText(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        })
    }

    private fun loadCategories() {
    val categoryReference = FirebaseDatabase.getInstance().getReference().child("Categories")
    categoryReference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val categories: MutableList<String?> = ArrayList()
            for (snapshot in dataSnapshot.getChildren()) {
                val category = snapshot.child("name").getValue(String::class.java)
                categories.add(category)
            }
            val adapter =
                ArrayAdapter(this@AddCategory, android.R.layout.simple_spinner_item, categories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory!!.setAdapter(adapter)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e("Firebase", "Error: " + databaseError.message)
        }
    })
}

    private fun addCategoryToDatabase() {
        val categoryName = edtCategory!!.text.toString().trim { it <= ' ' }
        if (categoryName.isEmpty()) {
            Toast.makeText(this, getString(R.string.please_enter_a_category), Toast.LENGTH_SHORT).show()
            return
        }
        val categoryReference = FirebaseDatabase.getInstance().getReference().child("Categories")
        val category = CategoryModel()
        category.name = categoryName
        // Tạo một ID duy nhất cho mỗi danh mục
        val categoryId = categoryReference.push().key
        category.id = categoryId
        categoryReference.child(categoryId!!).setValue(
            category,
            DatabaseReference.CompletionListener { databaseError, databaseReference ->
                if (databaseError != null) {
                    Toast.makeText(this@AddCategory,
                        getString(R.string.failed_to_add_category), Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        this@AddCategory,
                        getString(R.string.category_added_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                    edtCategory!!.setText("")
                }
            })
    }
    private fun editCategoryInDatabase(oldCategory: String) {
        val newCategory = edtCategory!!.getText().toString().trim { it <= ' ' }
        if (newCategory.isEmpty()) {
            Toast.makeText(this, getString(R.string.please_enter_a_category), Toast.LENGTH_SHORT).show()
            return
        }
        val categoryReference = FirebaseDatabase.getInstance().getReference().child("Categories")
        categoryReference.orderByChild("name").equalTo(oldCategory)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (snapshot: DataSnapshot in dataSnapshot.getChildren()) {
                            val category = CategoryModel()
                            category.name = newCategory
                            category.id = snapshot.key
                            snapshot.ref.setValue(
                                category,
                                object : DatabaseReference.CompletionListener {
                                    override fun onComplete(
                                        databaseError: DatabaseError?,
                                        databaseReference: DatabaseReference
                                    ) {
                                        if (databaseError != null) {
                                            Log.e("Firebase", "Error: " + databaseError.message)
                                            Toast.makeText(
                                                this@AddCategory,
                                                getString(R.string.failed_to_edit_category),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                this@AddCategory,
                                                getString(R.string.category_edited_successfully),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            edtCategory!!.setText("")
                                        }
                                    }
                                })
                        }
                    } else {
                        Toast.makeText(this@AddCategory,
                            getString(R.string.category_not_found), Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Firebase", "Error: " + databaseError.message)
                }
            })
    }



    private fun deleteCategoryFromDatabase() {
        val category = spinnerCategory!!.getSelectedItem().toString()
        val categoryReference = FirebaseDatabase.getInstance().getReference().child("Categories")
        categoryReference.orderByChild("name").equalTo(category)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (snapshot: DataSnapshot in dataSnapshot.getChildren()) {
                            snapshot.ref.removeValue(object : DatabaseReference.CompletionListener {
                                override fun onComplete(
                                    databaseError: DatabaseError?,
                                    databaseReference: DatabaseReference
                                ) {
                                    if (databaseError != null) {
                                        Log.e("Firebase", "Error: " + databaseError.message)
                                        Toast.makeText(
                                            this@AddCategory,
                                            getString(R.string.failed_to_delete_category),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            this@AddCategory,
                                            getString(R.string.category_deleted_successfully),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            })
                        }
                    } else {
                        Toast.makeText(this@AddCategory,  getString(R.string.category_not_found), Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Firebase", "Error: " + databaseError.message)
                }
            })
    }
}