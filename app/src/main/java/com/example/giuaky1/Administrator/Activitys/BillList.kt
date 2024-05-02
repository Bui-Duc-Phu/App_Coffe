package com.example.giuaky1.Administrator.Activitys

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Administrator.Adapters.ItemBillAdapter
import com.example.giuaky1.Administrator.model.ItemBill
import com.example.giuaky1.Firebase.DataHandler
import com.example.giuaky1.R
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class BillList : AppCompatActivity() {
    private val STORAGE_PERMISSION_CODE = 1
    private var recyclerViewBill: RecyclerView? = null
    private val dataHandler = DataHandler
    private var btnExportPDF: Button? = null
    private val adapter = ItemBillAdapter(emptyList())
    private val billList1 = mutableListOf<ItemBill>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_list)
        setControl()
        setEvent()
        dataHandler.getAllBill { billList ->
            billList1.addAll(billList)
            adapter.submitList(billList)
        }
    }

    private fun setEvent() {
        btnExportPDF!!.setOnClickListener {
            checkStoragePermission()
        }
    }

    private fun setControl() {
        recyclerViewBill = findViewById(R.id.recyclerViewBill)
        recyclerViewBill!!.adapter = adapter
        recyclerViewBill!!.layoutManager = LinearLayoutManager(this)
        btnExportPDF = findViewById(R.id.btnExportPDF)
    }

    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        } else {
            createPdf()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) { createPdf()
                } else {
                    Toast.makeText(this,
                        getString(R.string.permission_denied_to_read_your_external_storage), Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
                // Ignore all other requests
            }
        }
    }

    private fun createPdf() {
        val pdfDoc = Document()
        try {
            val path = getExternalFilesDir(null)?.absolutePath + "/billList.pdf"
            val writer = PdfWriter.getInstance(pdfDoc, FileOutputStream(path))
            pdfDoc.open()

            val mHeadingFont = Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.BOLD)
            val mValueFont = Font(Font.FontFamily.TIMES_ROMAN, 18.0f, Font.NORMAL)

            val heading = Paragraph(getString(R.string.danh_s_ch_h_a_n), mHeadingFont)
            heading.alignment = Element.ALIGN_CENTER
            pdfDoc.add(heading)

            val billList = billList1
            for (bill in billList) {
                val billParagraph = Paragraph(
                    "Ngày: ${bill.date}, Tên: ${bill.name}, Tổng tiền: ${bill.price}", mValueFont
                )
                pdfDoc.add(billParagraph)
            }

            Toast.makeText(this, "PDF đã được tạo thành công tại $path", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Đã xảy ra lỗi khi tạo PDF", Toast.LENGTH_LONG).show()
        } finally {
            pdfDoc.close()
        }

    }
}