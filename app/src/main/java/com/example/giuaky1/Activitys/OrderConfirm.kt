package com.example.giuaky1.Activitys
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.Models.CartModel
import com.example.giuaky1.Interfaces.OnTaskCompleted
import com.example.giuaky1.Paid.GoogleSheetsTask
import com.example.giuaky1.R
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*

class OrderConfirm : AppCompatActivity(), OnTaskCompleted {
    private lateinit var orderModelArrayList: ArrayList<CartModel>
    private lateinit var payment_methods: TextView
    private lateinit var orderTotalPrice: TextView
    private lateinit var alertDialog: AlertDialog
    private lateinit var dateTime: String
    private var trangThai = 0
    private lateinit var googleSheetsTask: GoogleSheetsTask
    private lateinit var orderId: String

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirm)
        payment_methods = findViewById(R.id.payment_methods)
        orderModelArrayList = FirebaseFunction.getOMAL()
        val listView: ListView = findViewById(R.id.list_product_order)
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1)
        for (cartModel in orderModelArrayList) {
            val orderDetail = "${cartModel.name} - ${cartModel.quantity} ly - ${cartModel.quantity * cartModel.price}"
            adapter.add(orderDetail)
        }
        listView.adapter = adapter
        val orderIdTextView: TextView = findViewById(R.id.order_id)
        val dateTimeTextView: TextView = findViewById(R.id.order_datetime)
        orderTotalPrice = findViewById(R.id.order_totalPrice)
        orderId = generateOrderId()
        dateTime = getCurrentDateTime()
        orderIdTextView.text = "ID Đơn hàng: $orderId"
        dateTimeTextView.text = "Ngày - thời gian: $dateTime"
        var totalPrice = 0.0
        for (cartModel in orderModelArrayList) {
            totalPrice += cartModel.totalPrice
        }
        orderTotalPrice.text = totalPrice.toString()
    }

    private fun generateOrderId(): String {
        val random = Random()
        val orderIdNumber = 100_000_000 + random.nextInt(900_000_000)
        return orderIdNumber.toString()
    }

    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    @Throws(JSONException::class)
    fun back_order(view: View) {
        onBackPressed()
    }

    override fun onTaskCompleted(price: String, describe: String) {
        val price1 = price.toDouble()
        val price2 = orderTotalPrice.text.toString().toDouble()
        val des = orderId
        if (price1 >= price2 && describe.contains(des)) {
            clearCart()
            trangThai = 1
        }
    }

    companion object {
        fun clearCart() {
            val userCart = FirebaseDatabase.getInstance().getReference("Carts").child(FirebaseFunction.getID())
            userCart.removeValue()
        }
    }

    @SuppressLint("SetTextI18n")
    fun thanhToanHoaDon(view: View) {
        if (payment_methods.text == "Quét mã QR") {
            val inflater = LayoutInflater.from(this)
            val overlayView: View = inflater.inflate(R.layout.qr, null)
            val nameTextView: TextView = overlayView.findViewById(R.id.nameTextView)
            val amountTextView: TextView = overlayView.findViewById(R.id.amountTextView)
            val descriptionTextView: TextView = overlayView.findViewById(R.id.descriptionTextView)
            val timeTextView: TextView = overlayView.findViewById(R.id.timeTextView)
            nameTextView.text = "Tên người nhận : Nguyễn Tiến Nhật"
            amountTextView.text = "Số tiền :${orderTotalPrice.text}"
            descriptionTextView.text = "Mô tả : $orderId"
            val qrCodeImageView: ImageView = overlayView.findViewById(R.id.qrImageView)
            val bankId = "VCB"
            val accountNo = "1016010035"
            val amount = orderTotalPrice.text.toString()
            val description = orderId
            val imageUrl = "https://img.vietqr.io/image/$bankId-$accountNo-qr_only.png?amount=$amount&addInfo=$description"
            Glide.with(this)
                .load(imageUrl)
                .into(qrCodeImageView)
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setView(overlayView)
            alertDialog = alertDialogBuilder.create()
            alertDialog.show()
            val backButton = alertDialog.findViewById<Button>(R.id.backButton)
            backButton?.setOnClickListener { alertDialog.dismiss() }
            val successButton = alertDialog.findViewById<Button>(R.id.successButton)
            successButton?.setOnClickListener {
                Toast.makeText(this@OrderConfirm, "Đang kiểm tra giao dịch", Toast.LENGTH_SHORT).show()
                googleSheetsTask = GoogleSheetsTask(this@OrderConfirm)
                googleSheetsTask.execute()
                if (trangThai == 1) {
                    Toast.makeText(this@OrderConfirm, "Thanh toán thành công!", Toast.LENGTH_SHORT).show()
                    if (alertDialog.isShowing) {
                        alertDialog.dismiss()
                    }
                    FirebaseFunction.addToOrder(orderId, orderTotalPrice, dateTime, orderModelArrayList,"QR")
                    finish()
                } else {
                    Toast.makeText(this@OrderConfirm, "Không tồn tại giao dịch", Toast.LENGTH_SHORT).show()
                }
            }
            object : CountDownTimer(600000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val secondsRemaining = millisUntilFinished / 1000
                    val minutes = secondsRemaining / 60
                    val seconds = secondsRemaining % 60
                    val timeRemaining = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                    timeTextView.text = "Thời gian còn lại: $timeRemaining"
                }

                override fun onFinish() {
                    alertDialog.dismiss()
                }
            }.start()
        } else {
            Toast.makeText(this, "Đơn hàng đã được tạo", Toast.LENGTH_SHORT).show()
            FirebaseFunction.addToOrder(orderId, orderTotalPrice, dateTime, orderModelArrayList,"delivery")
            clearCart()
            finish()
        }
    }

    fun paymsent_methods() {
        val paymentMethods = resources.getStringArray(R.array.payment_methods)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Chọn phương thức thanh toán")
        builder.setItems(paymentMethods) { dialog, which ->
            val selectedPaymentMethod = paymentMethods[which]
            payment_methods.text = selectedPaymentMethod
            dialog.dismiss()
        }
        builder.show()
    }
}
