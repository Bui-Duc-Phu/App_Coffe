package com.example.giuaky1.Activitys

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.giuaky1.Adapters.CartAdapter
import com.example.giuaky1.Firebase.DataHandler
import com.example.giuaky1.Interfaces.OnTaskCompleted
import com.example.giuaky1.Models.CartModel
import com.example.giuaky1.Models.Order
import com.example.giuaky1.Paid.GoogleSheetsTask
import com.example.giuaky1.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

class CreateOrder : AppCompatActivity(), OnTaskCompleted {
    private var orderModelArrayList: ArrayList<CartModel>? = null
    var alertDialog: AlertDialog? = null
    private var dateTime: String? = null
    private var trangThai = 0
    private var googleSheetsTask: GoogleSheetsTask? = null
    private var randomDescription: String? = null
    private var orderId: String? = null
    private var btnSaveQR: Button? = null
    private var backButton: Button? = null
    private var successButton: Button? = null
    private var qrCodeImageView: ImageView? = null
    private var ivPayment: ImageView? = null
    private var paymentMethods1: TextView? = null
    private var edtAddress: EditText? = null
    private var ivUpdateAddress: ImageView? = null
    private var ivEditAddress: ImageView? = null
    private var isQrSaved = false
    private var tvTotalPrice: TextView? = null
    private var btnOrder: Button? = null
    private var tvChangePayment: TextView? = null
    private var recyclerViewOrder: RecyclerView? = null
    private var ivBack: ImageView? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private val myCartAdapter = CartAdapter(DataHandler.orderModelArrayList)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_create_order)
        randomDescription = generateRandomDescription()
        setControl()
        setButtonOrder()
        setDataForOrder()
        setRecyclerViewOrder()
        setBack()
        setEdit()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        ivUpdateAddress!!.setOnClickListener { updateAddressFromLocation() }
        ivEditAddress!!.setOnClickListener { setEdit() }
    }

    private fun updateAddressFromLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )
            return
        }
        fusedLocationClient!!.lastLocation
            .addOnSuccessListener(this) { location ->
                if (location != null) {
                    getAddressFromCoordinates(location.latitude, location.longitude)
                }
            }
    }

    private fun getAddressFromCoordinates(latitude: Double, longitude: Double) {
        val url =
            "https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=$latitude&lon=$longitude"
        val jsonObjectRequest =
            JsonObjectRequest(Request.Method.GET, url, null, { response: JSONObject ->
                try {
                    val addressObject = response.getJSONObject("address")
                    val road = addressObject.getString("road")
                    //       String houseNumber = addressObject.optString("house_number", "");
                    val quarter = addressObject.optString("quarter", "")
                    val suburb = addressObject.optString("suburb", "")
                    val city = addressObject.getString("city")
                    var address = road
                    if (quarter.isNotEmpty()) {
                        address += ", $quarter"
                    }
                    if (suburb.isNotEmpty()) {
                        address += ", $suburb"
                    }
                    address += ", $city"
                    edtAddress!!.setText(address)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this@CreateOrder, "Không thể lấy địa chỉ", Toast.LENGTH_SHORT)
                        .show()
                }
            }) { error: VolleyError ->
                Log.d("Error.Response", error.toString())
                Toast.makeText(this@CreateOrder, "Không thể lấy địa chỉ", Toast.LENGTH_SHORT).show()
            }
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun setBack() {
        ivBack!!.setOnClickListener { finish() }
    }

    private fun setRecyclerViewOrder() {
        recyclerViewOrder!!.setLayoutManager(LinearLayoutManager(this))
        recyclerViewOrder!!.setAdapter(myCartAdapter)
    }

    private fun setDataForOrder() {
        recyclerViewOrder?.let { tvTotalPrice?.let { it1 -> DataHandler.fetchDataForOrder(it, it1) } }
    }

    private fun setButtonOrder() {
        btnOrder!!.setOnClickListener { thanhToanHoaDon() }
        tvChangePayment!!.setOnClickListener { paymentMethod() }
    }

    private fun setControl() {
        edtAddress = findViewById(R.id.edtAddress)
        ivEditAddress = findViewById(R.id.ivEditAddress)
        ivUpdateAddress = findViewById(R.id.ivUpdateAddress)
        paymentMethods1 = findViewById(R.id.payment_methods)
        ivPayment = findViewById(R.id.ivPayment)
        tvTotalPrice = findViewById(R.id.txtTotalPrice)
        btnOrder = findViewById(R.id.btnOrder)
        tvChangePayment = findViewById(R.id.tvChangePayment)
        recyclerViewOrder = findViewById(R.id.recyclerViewOrder)
        ivBack = findViewById(R.id.ivBack)
    }

    private fun setQrSaved() {
        btnSaveQR!!.setOnClickListener { v: View? ->
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }
            if (isQrSaved) {
                Snackbar.make(v!!, R.string.qr_saved, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            qrCodeImageView!!.setDrawingCacheEnabled(true)
            val bitmap = Bitmap.createBitmap(qrCodeImageView!!.drawingCache)
            qrCodeImageView!!.setDrawingCacheEnabled(false)
            try {
                val path =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .toString()
                val fOut: OutputStream
                val file = File(path, "QRCodeThanhToan.jpg")
                fOut = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
                fOut.flush()
                fOut.close()
                MediaStore.Images.Media.insertImage(
                    this.contentResolver,
                    file.absolutePath,
                    file.getName(),
                    file.getName()
                )
                isQrSaved = true
                val snackbar = Snackbar.make(v!!, R.string.qr_save, Snackbar.LENGTH_LONG)
                snackbar.setAction("Xem") {
                    val intent = Intent()
                    intent.setAction(Intent.ACTION_VIEW)
                    intent.setDataAndType(Uri.fromFile(file), "image/*")
                    startActivity(intent)
                }
                snackbar.show()
            } catch (e: Exception) {
                Log.d("QRSave", "QR Code save failed: " + e.message)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun thanhToanHoaDon() {
        if (paymentMethods1!!.getText() == getString(R.string.scan_qr)) {
            val inflater = LayoutInflater.from(this@CreateOrder)
            val overlayView = inflater.inflate(R.layout.qr, null)
            val nameTextView = overlayView.findViewById<TextView>(R.id.nameTextView)
            val amountTextView = overlayView.findViewById<TextView>(R.id.amountTextView)
            val descriptionTextView = overlayView.findViewById<TextView>(R.id.descriptionTextView)
            val timeTextView = overlayView.findViewById<TextView>(R.id.timeTextView)
            nameTextView.setText(R.string.name_host)
            amountTextView.text = getString(R.string.price_bank) + tvTotalPrice!!.getText()
            descriptionTextView.text = getString(R.string.note_bank) + randomDescription
            qrCodeImageView = overlayView.findViewById(R.id.qrImageView)
            val bankId = "VCB"
            val accountNo = "1016010035"
            val amount = tvTotalPrice!!.getText().toString()
            val description = randomDescription
            val imageUrl = ("https://img.vietqr.io/image/"
                    + bankId + "-" + accountNo + "-" + "qr_only.png"
                    + "?amount=" + amount
                    + "&addInfo=" + description)
            Glide.with(this).load(imageUrl).into(qrCodeImageView!!)
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setView(overlayView)
            alertDialog = alertDialogBuilder.create()
            alertDialog!!.show()
            btnSaveQR = overlayView.findViewById(R.id.btnSaveQR)
            setQrSaved()
            backButton = alertDialog!!.findViewById(R.id.backButton)
            if (backButton != null) {
                backButton!!.setOnClickListener { alertDialog!!.dismiss() }
            }
            successButton = alertDialog!!.findViewById(R.id.successButton)
            assert(successButton != null)
            successButton!!.setOnClickListener { view: View? ->
                googleSheetsTask = GoogleSheetsTask(this)
                googleSheetsTask!!.execute()
                if (trangThai == 1) {
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    dateTime = sdf.format(Date())
                    val sdf1 = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
                    orderId = sdf1.format(Date())
                    DataHandler.addOrderToFirebase("Đã thanh toán", orderId!!, paymentMethods1!!.text.toString(), dateTime!!, DataHandler.shipper, "0966638738", edtAddress!!.text.toString(), DataHandler.orderModelArrayList, tvTotalPrice!!.text.toString())
                    thongBaoThanhCong("Thanh toán thành công")
                    alertDialog!!.dismiss()
                    clearCart()
                    finish()
                } else {
                    Snackbar.make(view!!, R.string.cantfind, Snackbar.LENGTH_LONG).show()
                }
            }
            object : CountDownTimer(600000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val secondsRemaining = millisUntilFinished / 1000
                    val minutes = secondsRemaining / 60
                    val seconds = secondsRemaining % 60
                    val timeRemaining =
                        String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                    timeTextView.text = getString(R.string.time_con_lai) + timeRemaining
                }

                override fun onFinish() {
                    alertDialog!!.dismiss()
                }
            }.start()
        } else {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            dateTime = sdf.format(Date())
            val sdf1 = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            orderId = sdf1.format(Date())
            DataHandler.addOrderToFirebase("Chưa thanh toán", orderId!!, paymentMethods1!!.text.toString(), dateTime!!, DataHandler.shipper, "0966638738", edtAddress!!.text.toString(), DataHandler.orderModelArrayList, tvTotalPrice!!.text.toString())
            thongBaoThanhCong("Đơn hàng của bạn đã được tạo")
            clearCart()
            finish()
        }
    }

    private fun generateRandomDescription(): String {
        val descriptionLength = 20
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        val random = Random()
        val description = StringBuilder(descriptionLength)
        for (i in 0 until descriptionLength) {
            val index = random.nextInt(alphabet.length)
            val randomChar = alphabet[index]
            description.append(randomChar)
        }
        return description.toString()
    }

    private fun setEdit() {
        ivEditAddress!!.setOnClickListener(object : View.OnClickListener {
            var isEditing = false
            override fun onClick(v: View) {
                if (!isEditing) {
                    edtAddress!!.setEnabled(true)
                    edtAddress!!.setFocusableInTouchMode(true)
                    edtAddress!!.requestFocus()
                    ivEditAddress!!.setImageResource(R.drawable.edit_success)
                    isEditing = true
                } else {
                    if (edtAddress!!.getText().toString().isEmpty()) {
                        Toast.makeText(this@CreateOrder, R.string.nhap_dia_chi, Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        edtAddress!!.setEnabled(false)
                        edtAddress!!.isFocusable = false
                        ivEditAddress!!.setImageResource(R.drawable.edit)
                        isEditing = false
                    }
                }
            }
        })
    }

    private fun paymentMethod() {
        val paymentMethods = getResources().getStringArray(R.array.payment_methods)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.payment_method)
        builder.setItems(paymentMethods) { dialog: DialogInterface, which: Int ->
            val selectedPaymentMethod = paymentMethods[which]
            paymentMethods1!!.text = selectedPaymentMethod
            when (selectedPaymentMethod) {
                "Thanh toán khi nhận hàng" -> ivPayment!!.setImageResource(R.drawable.cash)
                "Quét mã QR" -> ivPayment!!.setImageResource(R.drawable.qrcode)
                "Thanh toán ZaloPay" -> ivPayment!!.setImageResource(R.drawable.zalo)
                else -> {}
            }
            dialog.dismiss()
        }
        builder.show()
    }

    private fun thongBaoThanhCong(msg:String) {
        val notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANNEL_ID = "my_channel_id_01"
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
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.coffee_icon)
            .setContentTitle(msg)
            .setContentText(getString(R.string.don_hang_dang_xu_ly))
            .setContentInfo(getString(R.string.thong_tin))
        notificationManager.notify(1, notificationBuilder.build())
    }

    override fun onTaskCompleted(result: String, describe: String) {
        val price1 = result.toDouble()
        val price2 = tvTotalPrice!!.getText().toString().replace(".", "").toDouble()
        val des = randomDescription
        if (price1 >= price2 && describe.contains(des!!)) {
            trangThai = 1
        }
    }

    companion object {
        fun clearCart() {
            val userCart = FirebaseDatabase
                .getInstance()
                .getReference("Carts")
                .child(DataHandler.getUID())
            userCart.removeValue()
        }
    }
}