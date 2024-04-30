package com.example.giuaky1.Firebase

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Adapters.CartAdapter
import com.example.giuaky1.Administrator.Adapters.OrderListAdapter
import com.example.giuaky1.Administrator.model.DoanhThu
import com.example.giuaky1.Administrator.model.DonHang
import com.example.giuaky1.Administrator.model.ItemBill
import com.example.giuaky1.Administrator.model.SanPham
import com.example.giuaky1.Models.CartModel
import com.example.giuaky1.Models.Order
import com.example.giuaky1.Models.ProductModel
import com.example.giuaky1.Models.Shipper
import com.example.giuaky1.Models.SizeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.CountDownLatch

object DataHandler {
    var rule: String = ""
    val shipper: Shipper = Shipper("Nguyễn Văn A", "0123456789")
    var orderModelArrayList = ArrayList<CartModel>()
    fun addOrderToFirebase(
        checkOut: String,
        orderId: String,
        paymentMethods1: String,
        dateTime: String,
        shipper: Shipper,
        phoneNumber: String,
        address: String,
        orderModelArrayList: ArrayList<CartModel>,
        totalPrice: String
    ) {
        val ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(getUID())
        val orderModel = Order(
            "Đang chờ xác nhận",
            checkOut,
            getUID(),
            orderId,
            paymentMethods1,
            dateTime,
            shipper,
            phoneNumber,
            address,
            orderModelArrayList,
            totalPrice
        )
        ordersRef.child(orderId).setValue(orderModel)
    }

    fun getOrderDetails(orderID: String, uID: String, callback: (Order) -> Unit) {
        val ref = FirebaseDatabase
            .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Orders").child(uID).child(orderID)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(orderSnapshot: DataSnapshot) {
                val state = orderSnapshot.child("state").value.toString()
                val checkout = orderSnapshot.child("checkout").value.toString()
                val uID = orderSnapshot.child("uID").value.toString()
                val orderID = orderSnapshot.child("orderID").value.toString()
                val pay = orderSnapshot.child("pay").value.toString()
                val time = orderSnapshot.child("time").value.toString()
                val shipperName = orderSnapshot.child("shipper").child("name").value.toString()
                val shipperSDT = orderSnapshot.child("shipper").child("sDT").value.toString()
                val receiverPhone = orderSnapshot.child("receiverPhone").value.toString()
                val receiverLocation = orderSnapshot.child("receiverLocation").value.toString()

                val productsList = mutableListOf<CartModel>()
                for (productSnapshot in orderSnapshot.child("products").children) {
                    val product = productSnapshot.getValue(CartModel::class.java)
                    if (product != null) {
                        productsList.add(product)
                    }
                }
                val shipper = Shipper(shipperName, shipperSDT)
                val sumPrice = orderSnapshot.child("sumPrice").value.toString()
                val order = Order(
                    state,
                    checkout,
                    uID,
                    orderID,
                    pay,
                    time,
                    shipper,
                    receiverPhone,
                    receiverLocation,
                    productsList,
                    sumPrice
                )
                callback(order)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun readAllOrdersList(onDataReceived: (List<Order>) -> Unit) {
        val ordersRef = FirebaseDatabase.getInstance().getReference("Orders")
        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = ArrayList<Order>()
                for (orderSnapshot in snapshot.children) {
                    for (singleOrderSnapshot in orderSnapshot.children) {
                        val order = singleOrderSnapshot.getValue(Order::class.java)
                        if (order != null) {
                            orderList.add(order)
                        }
                    }
                }
                onDataReceived(orderList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeDoanhThu", "onCancelled: " + error.message)
            }
        })
    }

    fun getOrderWithState(state: String, callback: (List<Order>) -> Unit) {
        val ordersRef = FirebaseDatabase.getInstance().getReference("Orders")
        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = ArrayList<Order>()
                for (orderSnapshot in snapshot.children) {
                    for (singleOrderSnapshot in orderSnapshot.children) {
                        val order = singleOrderSnapshot.getValue(Order::class.java)
                        if (order != null && order.state == state) {
                            orderList.add(order)
                        }
                    }
                }
                callback(orderList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeDoanhThu", "onCancelled: " + error.message)
            }
        })
    }

    fun getOrderWithStateClient(state: String, callback: (List<Order>) -> Unit) {
        val ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(getUID())
        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = ArrayList<Order>()
                for (singleOrderSnapshot in snapshot.children) {
                    val order = singleOrderSnapshot.getValue(Order::class.java)
                    if (order != null && order.state == state) {
                        orderList.add(order)
                    }
                }
                callback(orderList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeDoanhThu", "onCancelled: " + error.message)
            }
        })
    }

    fun updateState(uID: String, orderID: String, state: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Orders").child(uID).child(orderID)
        ref.child("state").setValue(state)

    }

    fun addToCart(productModel: ProductModel, selectedSize: SizeModel, quantity: Int) {
        val productID = productModel.name + "_" + selectedSize.size
        val cartReference =
            FirebaseDatabase.getInstance().getReference("Carts").child(getUID())
        cartReference.child(productID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val finalPrice =
                        if (productModel.discount > 0) productModel.finalPrice else productModel.price
                            .toDouble()
                    if (snapshot.exists()) {
                        val cartModel = snapshot.getValue(CartModel::class.java)
                        if (cartModel != null) {
                            cartModel.quantity += quantity
                            val updateData: MutableMap<String, Any> = HashMap()
                            updateData["quantity"] = cartModel.quantity
                            updateData["totalPrice"] =
                                cartModel.quantity * (finalPrice + selectedSize.price)
                            updateData["sizePrice"] = selectedSize.price
                            cartReference.child(productID)
                                .updateChildren(updateData)
                        }
                    } else {
                        val cartModel = CartModel(
                            productModel.name,
                            productModel.imageUrl,
                            quantity,
                            finalPrice + selectedSize.price,
                            finalPrice + selectedSize.price,
                            selectedSize.size,
                            selectedSize.price
                        )
                        cartReference.child(productID)
                            .setValue(cartModel)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("CartHandler", "addToCart onCancelled: " + error.message)
                }
            })
    }

    fun fetchDataForCart(
        recyclerView: RecyclerView,
        txtEmptyCart: TextView,
        txtTotalPrice: TextView,
        llBuy: LinearLayout
    ) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Carts")
        databaseReference.child(getUID())
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val cartModelArrayList = ArrayList<CartModel>()
                    if (dataSnapshot.exists()) {
                        for (snapshot in dataSnapshot.getChildren()) {
                            val cartModel = snapshot.getValue(CartModel::class.java)
                            if (cartModel != null) {
                                cartModelArrayList.add(cartModel)
                            }
                            orderModelArrayList = cartModelArrayList
                        }
                        recyclerView.visibility = View.VISIBLE
                        txtEmptyCart.visibility = View.GONE
                        llBuy.visibility = View.VISIBLE
                        val adapter = recyclerView.adapter as CartAdapter?
                        adapter?.updateData(cartModelArrayList)
                    } else {
                        recyclerView.visibility = View.GONE
                        llBuy.visibility = View.GONE
                        txtEmptyCart.visibility = View.VISIBLE
                    }
                    var totalPrice = 0.0
                    for (cartModel in cartModelArrayList) {
                        totalPrice += cartModel.totalPrice
                    }
                    val vndFormat = NumberFormat.getNumberInstance(Locale.getDefault())
                    txtTotalPrice.text = vndFormat.format(totalPrice) + "đ"
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("CartFragment", "onCancelled: " + databaseError.message)
                }
            })
    }

    fun fetchDataForOrder(
        recyclerView: RecyclerView,
        txtTotalPrice: TextView
    ) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Carts")
        databaseReference.child(getUID())
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val cartModelArrayList = ArrayList<CartModel>()
                    for (snapshot in dataSnapshot.getChildren()) {
                        val cartModel = snapshot.getValue(CartModel::class.java)
                        if (cartModel != null) {
                            cartModelArrayList.add(cartModel)
                        }
                        orderModelArrayList = cartModelArrayList
                    }
                    val adapter = recyclerView.adapter as CartAdapter?
                    adapter?.updateData(cartModelArrayList)
                    var totalPrice = 0.0
                    for (cartModel in cartModelArrayList) {
                        totalPrice += cartModel.totalPrice
                    }
                    val vndFormat = NumberFormat.getNumberInstance(Locale.getDefault())
                    totalPrice += 20000.0
                    txtTotalPrice.text = vndFormat.format(totalPrice)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("CartFragment", "onCancelled: " + databaseError.message)
                }
            })
    }

    fun getDoanhThuTheoNgay(startDate: String, endDate: String, orderList: List<Order>): Double {
        var doanhThu = 0.0
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val start = sdf.parse("$startDate 00:00:00")
        val end = sdf.parse("$endDate 23:59:59")

        for (order in orderList) {
            val orderDate = sdf.parse(order.time)
            if (orderDate.after(start) && orderDate.before(end)) {
                doanhThu += order.sumPrice.toDouble()
            }
        }
        return doanhThu
    }

    private fun getDonHangTheoNgay(
        startDate: String,
        endDate: String,
        orderList: List<Order>
    ): Int {
        var donHang = 0
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val start = sdf.parse("$startDate 00:00:00")
        val end = sdf.parse("$endDate 23:59:59")
        for (order in orderList) {
            val orderDate = sdf.parse(order.time)
            if (orderDate.after(start) && orderDate.before(end)) {
                donHang++
            }
        }
        return donHang
    }

    private fun getSanPhamTheoNgay(
        startDate: String,
        endDate: String,
        orderList: List<Order>
    ): Int {
        var soLuong = 0
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val start = sdf.parse("$startDate 00:00:00")
        val end = sdf.parse("$endDate 23:59:59")
        for (order in orderList) {
            val orderDate = sdf.parse(order.time)

            if (orderDate.after(start) && orderDate.before(end)) {
                for (product in order.products) {
                    soLuong += product.quantity
                }
            }
        }
        return soLuong
    }

    fun getUID(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }

    fun getPhoneNumber(): String {
        return FirebaseAuth.getInstance().currentUser?.phoneNumber ?: ""
    }

    fun getOMAL(): ArrayList<CartModel> {
        return orderModelArrayList
    }

    fun setTypeAccount(value: String) {
        rule = value
    }

    fun getListDoanhThuTheoNgay(
        startDate: String,
        endDate: String,
        listOrder: List<Order>,
        callback: (List<DoanhThu>) -> Unit
    ) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val start = sdf.parse(startDate)
        val end = sdf.parse(endDate)
        val calendar = Calendar.getInstance()
        calendar.time = start
        val doanhThuList = mutableListOf<DoanhThu>()
        while (!calendar.time.after(end)) {
            val dateStr = sdf.format(calendar.time)
            val doanhThu = getDoanhThuTheoNgay(dateStr, dateStr, listOrder)
            if (doanhThu > 0) { // Only add the day to the list if the revenue is greater than 0
                doanhThuList.add(DoanhThu(dateStr, doanhThu))
            }
            calendar.add(Calendar.DATE, 1)
        }
        callback(doanhThuList)
    }


    fun getListDonHangTheoNgay(
        startDate: String,
        endDate: String,
        listOrder: List<Order>,
        callback: (List<DonHang>) -> Unit
    ) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val start = sdf.parse(startDate)
        val end = sdf.parse(endDate)
        val calendar = Calendar.getInstance()
        calendar.time = start
        val donHangList = mutableListOf<DonHang>()
        while (!calendar.time.after(end)) {
            val dateStr = sdf.format(calendar.time)
            val donhang = getDonHangTheoNgay(dateStr, dateStr, listOrder)
            if (donhang > 0) { // Only add the day to the list if the revenue is greater than 0
                donHangList.add(DonHang(dateStr, donhang))
            }
            calendar.add(Calendar.DATE, 1)
        }
        callback(donHangList)
    }

    fun getListSanPhamTheoNgay(
        startDate: String,
        endDate: String,
        listOrder: List<Order>,
        callback: (List<SanPham>) -> Unit
    ) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val start = sdf.parse(startDate)
        val end = sdf.parse(endDate)
        val calendar = Calendar.getInstance()
        calendar.time = start
        val sanPhamList = mutableListOf<SanPham>()
        while (!calendar.time.after(end)) {
            val dateStr = sdf.format(calendar.time)
            val soLuong = getSanPhamTheoNgay(dateStr, dateStr, listOrder)
            if (soLuong > 0) { // Only add the day to the list if the revenue is greater than 0
                sanPhamList.add(SanPham(dateStr, soLuong))
            }
            calendar.add(Calendar.DATE, 1)
        }
        callback(sanPhamList)
    }


    fun getAllBill(callback: (List<ItemBill>) -> Unit) {
        val ordersRef = FirebaseDatabase.getInstance().getReference("Orders")
        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = ArrayList<ItemBill>()
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                for (orderSnapshot in snapshot.children) {
                    for (singleOrderSnapshot in orderSnapshot.children) {
                        val order = singleOrderSnapshot.getValue(Order::class.java)
                        if (order != null && order.state == "Đã giao hàng") {
                            val name1 = getNameFromUID(order.uID)
                            val bill = ItemBill(
                                order.time,
                                "123",
                                order.sumPrice.toDouble(),
                                order.orderID,
                                order.uID
                            )
                            orderList.add(bill)
                        }
                    }
                }
                val sortedOrderList =
                    orderList.sortedWith(compareByDescending { sdf.parse(it.date) })
                callback(sortedOrderList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeDoanhThu", "onCancelled: " + error.message)
            }
        })
    }

    fun getNameFromUID(uID: String): String {
        var name = ""
        val ref = FirebaseDatabase.getInstance().getReference("Users").child(uID)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                name = snapshot.child("name").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeDoanhThu", "onCancelled: " + error.message)
            }
        })
        return name
    }

    fun getAddress(callback: (String) -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("ProfileUser").child(getUID())
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val address = snapshot.child("location").value.toString()
                Log.d("HomeDoanhThu", "onDataChange: $address")
                callback(address)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeDoanhThu", "onCancelled: " + error.message)
            }
        })
    }

    fun getPhoneNumber(callback: (String) -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("ProfileUser").child(getUID())
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val phoneNumber = snapshot.child("phoneNumber").value.toString()
                callback(phoneNumber)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeDoanhThu", "onCancelled: " + error.message)
            }
        })
    }
}
