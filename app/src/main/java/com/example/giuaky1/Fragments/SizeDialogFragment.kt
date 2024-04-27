package com.example.giuaky1.Fragments


import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Adapters.SizeAdapter
import com.example.giuaky1.Firebase.DataHandler.addToCart
import com.example.giuaky1.Models.ProductModel
import com.example.giuaky1.Models.SizeModel
import com.example.giuaky1.R

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SizeDialogFragment : BottomSheetDialogFragment() {
    private var sizeAdapter: SizeAdapter? = null
    private val sizeList1 = ArrayList<SizeModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_size_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sizeAdapter = SizeAdapter(sizeList1)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.setLayoutManager(LinearLayoutManager(context))
        recyclerView.setAdapter(sizeAdapter)
        assert(arguments != null)
        val product = (requireArguments().getSerializable("product") as ProductModel?)!!
        val productId = product.name
        FirebaseDatabase.getInstance().getReference("Products").child(productId!!).child("sizes")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    sizeList1.clear()
                    for (snapshot in dataSnapshot.getChildren()) {
                        val size = snapshot.child("size").getValue(
                            String::class.java
                        )
                        val price = snapshot.child("price").getValue(
                            Double::class.java
                        )
                        if (size != null && price != null) {
                            sizeList1.add(SizeModel(size, price))
                        }
                    }
                    sizeAdapter!!.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("fetchData", "Fetch data cancelled: " + databaseError.message)
                }
            })
        val btn_add = view.findViewById<Button>(R.id.btn_add_product_to_cart)
        val buttonDecrease = view.findViewById<ImageView>(R.id.buttonDecrease)
        val buttonIncrease = view.findViewById<ImageView>(R.id.buttonIncrease)
        val textQuantity = view.findViewById<TextView>(R.id.textQuantity)
        buttonDecrease.setOnClickListener { v: View? ->
            var quantity = textQuantity.getText().toString().toInt()
            if (quantity > 1) {
                quantity--
                textQuantity.text = quantity.toString()
            }
        }
        buttonIncrease.setOnClickListener { v: View? ->
            var quantity = textQuantity.getText().toString().toInt()
            quantity++
            textQuantity.text = quantity.toString()
        }
        btn_add.setOnClickListener { v: View? ->
            val selectedSize = sizeAdapter!!.selectedSize
            if (selectedSize != null) {
                val quantity = textQuantity.getText().toString().toInt()
                addToCart(product, selectedSize, quantity)
                dismiss()
                thongBaoThanhCong()
            } else {
                Toast.makeText(context, R.string.chon_size_khi_them_vao_cart, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun thongBaoThanhCong() {
        val notificationManager =
            requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANNEL_ID = "my_channel_id_01"
        val notificationId = 1 // Định danh duy nhất cho mỗi thông báo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "My Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = "Channel description"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.setVibrationPattern(longArrayOf(0, 1000, 500, 1000))
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notificationBuilder = NotificationCompat.Builder(requireActivity(), NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.coffee_icon)
            .setContentTitle("Thêm thành công")
            .setContentText("Sản phẩm đã được thêm vào giỏ hàng của bạn")
            .setContentInfo(getString(R.string.thong_tin))
        notificationManager.notify(notificationId, notificationBuilder.build())
        val handler = Handler()
        val delayInMilliseconds: Long = 300
        handler.postDelayed({ notificationManager.cancel(notificationId) }, delayInMilliseconds)
    }

    companion object {
        fun newInstance(product: ProductModel?): SizeDialogFragment {
            val fragment = SizeDialogFragment()
            val args = Bundle()
            args.putSerializable("product", product)
            fragment.setArguments(args)
            return fragment
        }
    }
}