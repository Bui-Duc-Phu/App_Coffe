package com.example.giuaky1.Administrator.Activitys

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Administrator.Adapters.ItemDetailAdapter
import com.example.giuaky1.Firebase.DataHandler
import com.example.giuaky1.Firebase.DataHandler.userInfo
import com.example.giuaky1.Models.Order
import com.example.giuaky1.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailOrder : AppCompatActivity() {

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_order)
        val orderDetail = intent.getSerializableExtra("orderDetail") as Order
        val IDTv = findViewById<TextView>(R.id.IDTv1)
        val payTv = findViewById<TextView>(R.id.payTv1)
        val timeTv = findViewById<TextView>(R.id.timeTv1)
        val shiperTv = findViewById<TextView>(R.id.shiperTv1)
        val sumPrice = findViewById<TextView>(R.id.sumPrice1)
        val receiverPhone = findViewById<TextView>(R.id.receiverPhone)
        val receiverLocation = findViewById<TextView>(R.id.receiverLocation)
        val productRecylerview = findViewById<RecyclerView>(R.id.product_recylerview1)
        val btnBack = findViewById<ImageView>(R.id.backImage)
        val btnPDF = findViewById<Button>(R.id.btnPDF)
        val btnReset = findViewById<Button>(R.id.btnReset)
        IDTv.text = "ID: #${orderDetail.orderID}"
        payTv.text = "Phương thức thanh toán :${orderDetail.pay}"
        timeTv.text = "Thời gian: ${orderDetail.time}"
        shiperTv.text = "Shipper: ${orderDetail.shipper.name}\nSĐT: ${orderDetail.shipper.sDT}"
        sumPrice.text = "Tổng: ${orderDetail.sumPrice}"
        receiverPhone.text = "SĐT người nhận: ${orderDetail.receiverPhone}"
        receiverLocation.text = "Địa chỉ: ${orderDetail.receiverLocation}"

        val adapter = ItemDetailAdapter(this, orderDetail.products)
        productRecylerview.adapter = adapter

        btnBack.setOnClickListener {
            finish()
        }
        btnPDF.setOnClickListener {
            Thread {
                try {
                    val document = PdfDocument()

                    val pageInfo =
                        PdfDocument.PageInfo.Builder(595, 842, 1).create()

                    val page = document.startPage(pageInfo)

                    val canvas = page.canvas
                    val paint = Paint()
                    paint.color = Color.BLACK
                    paint.textSize = 14f

                    var y = 50

                    canvas.drawText("Tên: ${userInfo.name}", 50f, y.toFloat(), paint)
                    y += 50
                    canvas.drawText("Số điện thoại: ${userInfo.phone}", 50f, y.toFloat(), paint)
                    y += 50
                    canvas.drawText("Email: ${userInfo.email}", 50f, y.toFloat(), paint)
                    y += 50
                    canvas.drawText("Thời gian: ${timeTv.text}", 50f, y.toFloat(), paint)
                    y += 50
                    var STT=0
                    for (bill in orderDetail.products) {
                        canvas.drawText("STT: ${++STT}, Tên: ${bill.name},Size:${bill.size},Số lượng: ${bill.quantity}, Giá: ${bill.price}", 50f, y.toFloat(), paint)
                        y += 50
                    }

                    document.finishPage(page)

                    val downloadsDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val sdf1 = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
                    val namePdf = sdf1.format(Date())
                    val fileName = "Đơn_hàng_Coffe_Hub$namePdf.pdf"
                    val filePath = File(downloadsDir, fileName)

                    try {
                        val fos = FileOutputStream(filePath)
                        document.writeTo(fos)
                        document.close()
                        fos.close()
                        this.runOnUiThread {
                            val snackbar = Snackbar.make(
                                findViewById(android.R.id.content),
                                "Đã lưu file PDF tại: $filePath",
                                Snackbar.LENGTH_LONG
                            )
                            snackbar.show()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    this.runOnUiThread {
                        Toast.makeText(
                            this,
                            "Error: ${e.message}",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
            }.start()
        }
        btnReset.setOnClickListener {
            val cartReference = FirebaseDatabase.getInstance().getReference("Carts").child(DataHandler.getUID())
            cartReference.removeValue().addOnSuccessListener {

            }
            for(productModel in orderDetail.products) {
                val productID = productModel.name + "_" + productModel.size
               cartReference.child(productID).setValue(productModel)
            }
            Toast.makeText(this, "Đã thêm lại tất cả sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

}