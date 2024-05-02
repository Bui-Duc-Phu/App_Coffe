package com.example.giuaky1.Fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.R
import com.example.giuaky1.Adapters.FragmentAdapter
import com.google.android.material.tabs.TabLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.giuaky1.Administrator.Adapters.ItemBillAdapter
import com.example.giuaky1.Administrator.model.ItemBill
import com.example.giuaky1.Firebase.DataHandler
import com.example.giuaky1.Models.Order
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HistoryFragment : Fragment() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var printPdf: ImageView
    private val billList1 = mutableListOf<ItemBill>()
    private val STORAGE_PERMISSION_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        tabLayout = view.findViewById(R.id.tab_layout)
        viewPager = view.findViewById(R.id.view_pager)
        printPdf = view.findViewById(R.id.printf_pdf)
        setPDF()
        setUpTabLayout()

        return view
    }

    @SuppressLint("MissingInflatedId")
    private fun setPDF() {
        printPdf.setOnClickListener {

            val dialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_date_range, null)
            val alertDialog = AlertDialog.Builder(requireActivity()).setView(dialogView).create()

            val startDatePicker: EditText = dialogView.findViewById(R.id.startDateEditText)
            val endDatePicker: EditText = dialogView.findViewById(R.id.endDateEditText)
            setDate(startDatePicker, endDatePicker)
            dialogView.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                createPdf(startDatePicker.text.toString(), endDatePicker.text.toString())
                alertDialog.dismiss()
            }

            alertDialog.show()
        }
    }
    private fun setDate(startDateEditText: EditText, endDateEditText: EditText) {

        startDateEditText.setOnClickListener { view ->
            val now = Calendar.getInstance()
            DatePickerDialog(
                view.context,
                { _, year, monthOfYear, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, monthOfYear)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val dateStr = SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).format(selectedDate.time)
                    if (endDateEditText.text.toString().isNotEmpty()) {
                        val endDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(
                            endDateEditText.text.toString()
                        )
                        if (selectedDate.time.after(endDate)) {
                            Toast.makeText(
                                view.context,
                                getString(R.string.ng_y_b_t_u_kh_ng_th_sau_ng_y_k_t_th_c),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@DatePickerDialog
                        }
                    }
                    startDateEditText.setText(dateStr)
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        endDateEditText.setOnClickListener { view ->
            val now = Calendar.getInstance()
            DatePickerDialog(
                view.context,
                { _, year, monthOfYear, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, monthOfYear)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val dateStr = SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).format(selectedDate.time)
                    if (startDateEditText.text.toString().isNotEmpty()) {
                        val startDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(
                            startDateEditText.text.toString()
                        )
                        if (selectedDate.time.before(startDate)) {
                            Toast.makeText(
                                view.context,
                                getString(R.string.ng_y_k_t_th_c_kh_ng_th_tr_c_ng_y_b_t_u),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@DatePickerDialog
                        }
                    }
                    endDateEditText.setText(dateStr)
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) { createPdf("","")
                } else {
                    Toast.makeText(requireActivity(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
                // Ignore all other requests
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    /*fun createPdf(startDate : String, endDate : String) {
    DataHandler.getInforPDF{userInfo ->
        DataHandler.getBillByDate(startDate,endDate) { billList ->
            if (billList.isEmpty()) {
                requireActivity().runOnUiThread {
                    Snackbar.make(
                        requireView(),
                        "Không có đơn hàng nào trong khoảng thời gian này!",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else {
                Thread {
                    try {
                        val view = LayoutInflater.from(requireActivity())
                            .inflate(R.layout.layout_file_pdf, null)
                            requireActivity().runOnUiThread {
                            val pdfName: TextView = view.findViewById(R.id.pdf_name)
                            val pdfPhone: TextView = view.findViewById(R.id.pdf_phone)
                            val pdfMail: TextView = view.findViewById(R.id.pdf_mail)
                            val pdfTime: TextView = view.findViewById(R.id.pdf_time)
                            val pdfPrice: TextView = view.findViewById(R.id.pdf_price)
                            val recycler_pdf: RecyclerView = view.findViewById(R.id.recycler_pdf)
                            recycler_pdf.layoutManager = LinearLayoutManager(requireActivity())
                            pdfName.text = userInfo.name
                            pdfPhone.text = userInfo.phone
                            pdfMail.text = userInfo.email
                            pdfTime.text = "$startDate - $endDate"
                            pdfPrice.text = "Tổng thanh toán: ${billList.sumOf { it.price }}"
                            recycler_pdf.adapter = ItemBillAdapter(billList)
                            recycler_pdf.adapter?.notifyDataSetChanged()
                        }

                        val displayMetrics = DisplayMetrics()

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            val display = requireActivity().display
                            display?.getRealMetrics(displayMetrics)
                        } else {
                            val windowManager = requireActivity().windowManager
                            windowManager.defaultDisplay.getMetrics(displayMetrics)
                        }

                        view.measure(
                            View.MeasureSpec.makeMeasureSpec(
                                displayMetrics.widthPixels,
                                View.MeasureSpec.EXACTLY
                            ),
                            View.MeasureSpec.makeMeasureSpec(
                                displayMetrics.heightPixels,
                                View.MeasureSpec.EXACTLY
                            )
                        )

                        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)

                        val document = PdfDocument()

                        val viewWidth = 1080
                        val viewHeight = 1920

                        val pageInfo =
                            PdfDocument.PageInfo.Builder(viewWidth, viewHeight, 1).create()

                        val page = document.startPage(pageInfo)

                        val canvas = page.canvas

                        val paint = Paint()
                        paint.color = Color.WHITE

                        view.draw(canvas)

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
                            requireActivity().runOnUiThread {
                                val snackbar = Snackbar.make(
                                    requireView(),
                                    "Xuất file PDF thành công!",
                                    Snackbar.LENGTH_LONG
                                )
                                snackbar.show()
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        requireActivity().runOnUiThread {
                            Toast.makeText(
                                requireActivity(),
                                "Error: ${e.message}",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                }.start()
            }
        }
    }
}*/
fun createPdf(startDate: String, endDate: String) {
    DataHandler.getInforPDF { userInfo ->
        DataHandler.getBillByDate(startDate, endDate) { billList ->
            if (billList.isEmpty()) {
                requireActivity().runOnUiThread {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.kh_ng_c_n_h_ng_n_o_trong_kho_ng_th_i_gian_n_y),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else {
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
                        canvas.drawText("Thời gian: $startDate - $endDate", 50f, y.toFloat(), paint)
                        y += 50

                        for (bill in billList) {
                            canvas.drawText("Ngày: ${bill.date}, Tên: ${bill.name}, Tổng tiền: ${bill.price}", 50f, y.toFloat(), paint)
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
                            requireActivity().runOnUiThread {
                                val snackbar = Snackbar.make(
                                    requireView(),
                                    getString(R.string.xu_t_file_pdf_th_nh_c_ng),
                                    Snackbar.LENGTH_LONG
                                )
                                snackbar.show()
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        requireActivity().runOnUiThread {
                            Toast.makeText(
                                requireActivity(),
                                "Error: ${e.message}",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                }.start()
            }
        }
    }
}
    private fun setUpTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.ch_x_c_nh_n)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.ang_giao_h_ng)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.giao)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.h_y)))
        val adapter = FragmentAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }
}