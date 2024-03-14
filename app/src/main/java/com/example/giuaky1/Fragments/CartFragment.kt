package com.example.giuaky1.Fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Adapters.MyCartAdapter
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CartFragment : Fragment() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var txtEmptyCart: TextView
    private lateinit var txtTotalPrice: TextView
    private lateinit var llBuy: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnBuy: Button
    private var totalPrice = 0
    private val myCartAdapter = MyCartAdapter(ArrayList())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        databaseReference = FirebaseDatabase.getInstance().getReference("Carts")
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        setControl(view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = myCartAdapter
        FirebaseFunction.fetchDataForCart(recyclerView, txtEmptyCart, txtTotalPrice, llBuy)
        btnBuy.setOnClickListener {
            /*val intent = Intent(activity, OrderActivity::class.java)
            startActivity(intent)*/
        }
        return view
    }

    private fun setControl(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewCart)
        txtEmptyCart = view.findViewById(R.id.txtEmptyCart)
        txtTotalPrice = view.findViewById(R.id.txtTotalPrice)
        btnBuy = view.findViewById(R.id.btnBuy)
        llBuy = view.findViewById(R.id.llBuy)
    }
}
