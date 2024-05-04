package com.example.giuaky1.Administrator.Activitys

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.giuaky1.Administrator.model.CategoryModel
import com.example.giuaky1.Administrator.model.Size
import com.example.giuaky1.Administrator.model.coffeModel
import com.example.giuaky1.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditCoffe : AppCompatActivity() {
    var imageProductEdit: ImageView? = null
    var btnAddImageEdit: Button? = null
    var nameProductEdit: EditText? = null
    var priceProductEdit: EditText? = null
    var sizeSEdit: EditText? = null
    var sizeLEdit: EditText? = null
    var sizeMEdit: EditText? = null
    var sizeXLEdit: EditText? = null
    var priceSizeS: EditText? = null
    var priceSizeM: EditText? = null
    var priceSizeL: EditText? = null
    var priceSizeXL: EditText? = null
    var discountEdit: EditText? = null
    var btnSaveEdit: Button? = null
    var btnDelEdit: Button? = null
    var btnBackEdit: Button? = null
    var tenSp: String? = null
    var imageUri: Uri? = null
    var spinnerCategoryEdit: Spinner? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_coffe)
        setControl()
        loadCategory()
        btnBackEdit!!.setOnClickListener { v: View? -> onBackPressed() }
        btnSaveEdit!!.setOnClickListener { v: View? ->
            val mDatabase = FirebaseDatabase.getInstance().getReference().child("Products")
            val product = coffeModel()
            product.name = nameProductEdit!!.getText().toString()
            product.price = Integer.valueOf(priceProductEdit!!.getText().toString())
            product.category = spinnerCategoryEdit!!.getSelectedItem().toString()

            // Update sizes
            val sizes = mutableMapOf<String, Size>()
            priceSizeS!!.text.toString().takeIf { it.isNotEmpty() }?.let {
                sizes["S"] = Size(it.toInt(), "S")
            }
            priceSizeM!!.text.toString().takeIf { it.isNotEmpty() }?.let {
                sizes["M"] = Size(it.toInt(), "M")
            }
            priceSizeL!!.text.toString().takeIf { it.isNotEmpty() }?.let {
                sizes["L"] = Size(it.toInt(), "L")
            }
            priceSizeXL!!.text.toString().takeIf { it.isNotEmpty() }?.let {
                sizes["XL"] = Size(it.toInt(), "XL")
            }
            product.sizes = sizes

            product.discount = Integer.valueOf(discountEdit!!.getText().toString())
            product.imageUrl = imageUri.toString()
            mDatabase.child(tenSp!!).removeValue()
            mDatabase.child(product.name!!).setValue(product)
            Toast.makeText(this@EditCoffe,
                getString(R.string.s_a_s_n_ph_m_th_nh_c_ng), Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
        btnDelEdit!!.setOnClickListener { v: View? ->
            xoaDataFirebase()
            onBackPressed()
        }
        btnAddImageEdit!!.setOnClickListener { v: View? -> openGallery() }
    }

    private fun loadCategory() {
        val categoryReference = FirebaseDatabase.getInstance().getReference().child("Categories")
        categoryReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val categories: MutableList<String?> = ArrayList()
                for (snapshot in dataSnapshot.getChildren()) {
                    val category = snapshot.getValue(CategoryModel::class.java)
                    categories.add(category?.name)
                }
                val adapter =
                    ArrayAdapter(this@EditCoffe, android.R.layout.simple_spinner_item, categories)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCategoryEdit!!.setAdapter(adapter)
                setEvent()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error: " + databaseError.message)
            }
        })
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imageProductEdit!!.setImageURI(imageUri)
        }
    }

    private fun xoaDataFirebase() {
        val mDatabase = FirebaseDatabase.getInstance().getReference().child("Products")
        mDatabase.child(tenSp!!).removeValue()
    }

    private fun setEvent() {
    val sp = (intent.getSerializableExtra("item") as coffeModel?)!!
    tenSp = sp.name
    Glide.with(this).load(sp.imageUrl).into(imageProductEdit!!)
    nameProductEdit!!.setText(sp.name)
    priceProductEdit!!.setText(sp.price.toString())
    spinnerCategoryEdit!!.setSelection(
        (spinnerCategoryEdit!!.adapter as ArrayAdapter<String?>).getPosition(
            sp.category
        )
    )

    sp.sizes?.let { sizes ->
        sizes["S"]?.let { size ->
            sizeSEdit!!.setText(size.size)
            priceSizeS!!.setText(size.price.toString())
        }
        sizes["M"]?.let { size ->
            sizeMEdit!!.setText(size.size)
            priceSizeM!!.setText(size.price.toString())
        }
        sizes["L"]?.let { size ->
            sizeLEdit!!.setText(size.size)
            priceSizeL!!.setText(size.price.toString())
        }
        sizes["XL"]?.let { size ->
            sizeXLEdit!!.setText(size.size)
            priceSizeXL!!.setText(size.price.toString())
        }
    }

    discountEdit!!.setText(sp.discount.toString())
    imageUri = Uri.parse(sp.imageUrl)
}

    private fun setControl() {
        imageProductEdit = findViewById(R.id.imageProductEdit)
        btnAddImageEdit = findViewById(R.id.btnAddImageEdit)
        nameProductEdit = findViewById(R.id.nameProductEdit)
        priceProductEdit = findViewById(R.id.priceProductEdit)
        sizeSEdit = findViewById(R.id.sizeSEdit)
        sizeMEdit = findViewById(R.id.sizeMEdit)
        sizeLEdit = findViewById(R.id.sizeLEdit)
        sizeXLEdit = findViewById(R.id.sizeXLEdit)
        priceSizeS = findViewById(R.id.priceSizeS)
        priceSizeM = findViewById(R.id.priceSizeM)
        priceSizeL = findViewById(R.id.priceSizeL)
        priceSizeXL = findViewById(R.id.priceSizeXL)
        discountEdit = findViewById(R.id.DiscountEdit)
        btnSaveEdit = findViewById(R.id.btnSaveEdit)
        btnDelEdit = findViewById(R.id.btnDelEdit)
        btnBackEdit = findViewById(R.id.btnBackEdit)
        spinnerCategoryEdit = findViewById(R.id.spinnerCategoryEdit)
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}