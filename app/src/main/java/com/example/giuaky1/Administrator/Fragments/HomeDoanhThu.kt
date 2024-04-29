package com.example.giuaky1.Administrator.Fragments


/*
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.giuaky1.Firebase.DataHandler
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.Models.Order
import com.example.giuaky1.R
import com.example.giuaky1.databinding.FragmentHomeAdminDoanhThuBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Locale
import java.util.regex.Pattern

class HomeDoanhThu : Fragment() {

    lateinit var binding : FragmentHomeAdminDoanhThuBinding

    private lateinit var barChart: BarChart

    private lateinit var changeButton: Button

    private var isChart1 = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeAdminDoanhThuBinding.inflate(inflater,container,false)

//        Log.d("SUPER LONGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG LABEL: ","I AM HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
  //      changeButton = binding.MonthWeekDTBtn

        barChart = binding.DoanhThuBarChart
        DataHandler.readAllOrdersList { orderList ->

            setupBarChartMonth(orderList)
            changeButton.setOnClickListener(View.OnClickListener {
                if (isChart1) {
                    changeButton.setText(getString(R.string.frag_TK_week))
                    setupBarChartWeek(orderList)
                } else {
                    changeButton.setText(getString(R.string.frag_TK_month))
                    setupBarChartMonth(orderList)
                }
                isChart1 = !isChart1
            })
        }

        return binding.root
    }

    private fun setupBarChartMonth(orderList: List<Order>) {
        Log.d("chartMonth", orderList.toString())
        val Past5Months: List<String?> = java.util.ArrayList(getMonthLabels())
        Collections.reverse(Past5Months)

        val entries: MutableList<BarEntry?> = mutableListOf()

        for (i in Past5Months.indices) {
            val regexPattern = "^[0-9]{2}/" + Past5Months[i] + "$"
            val pattern = Pattern.compile(regexPattern)
            var SUM: Long = 0
            for (order in orderList) {
                if (pattern.matcher(order.time).matches()) {
                    for(temp in order.products) {
                        SUM += temp.price.toLong()
                    }
                }
            }
            entries.add(BarEntry(i.toFloat(), SUM.toFloat()))
        }

        val dataSet = BarDataSet(entries, null)
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
        dataSet.valueTextSize = 20f
        dataSet.setValueTextColor(Color.WHITE)

        val data = BarData(dataSet)
        barChart.data = data

        val yAxis = barChart.axisLeft
        yAxis.textColor = Color.WHITE

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.textColor = Color.WHITE
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(Past5Months)

        barChart.data = data

        barChart.description.isEnabled = false

        barChart.invalidate()

    }

    private fun setupBarChartWeek(orderList: List<Order>) {
        val Past5Weeks: List<String?> = java.util.ArrayList(getWeekLabels())
        Collections.reverse(Past5Weeks)
        val entries: MutableList<BarEntry> = java.util.ArrayList()
        val calendar = Calendar.getInstance()
        val GiaTriDoanhThu: MutableList<Long?> = java.util.ArrayList()
        for (i in Past5Weeks.indices) {
            var SUM: Long = 0
            for (order in orderList) {
                val entryDate = Calendar.getInstance()
                val dateParts = order.time.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                entryDate[dateParts[2].toInt(), dateParts[1].toInt() - 1] = dateParts[0].toInt()
                if (isWithinLastSevenDays(entryDate,calendar)) {
                    for(temp in order.products) {
                        SUM += temp.price.toLong()
                    }
                }
            }
            GiaTriDoanhThu.add(SUM)
            calendar.add(Calendar.DATE, -6)
        }
        Collections.reverse(GiaTriDoanhThu)
        for (i in GiaTriDoanhThu.indices) {
            entries.add(BarEntry(i.toFloat(), GiaTriDoanhThu[i]!!.toFloat()))
        }

        val dataSet = BarDataSet(entries, null)
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
        dataSet.valueTextSize = 20f
        dataSet.setValueTextColor(Color.WHITE)

        val data = BarData(dataSet)
        barChart.data = data

        val yAxis = barChart.axisLeft
        yAxis.textColor = Color.WHITE

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(Past5Weeks)
        xAxis.textColor = Color.WHITE
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        barChart.data = data
        barChart.description.isEnabled = false
        barChart.invalidate()
    }

    private fun isWithinLastSevenDays(dateToCheck: Calendar, currentDate: Calendar): Boolean {
        val sevenDaysAgo = currentDate.clone() as Calendar
        sevenDaysAgo.add(Calendar.DATE, -7)
        return dateToCheck.after(sevenDaysAgo) && !dateToCheck.after(currentDate)
    }

    private fun getMonthLabels(): List<String>? {
        val labels: MutableList<String> = ArrayList()
        val calendar = Calendar.getInstance()

        for (i in 4 downTo 0) {
            val label = SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(calendar.time)
            labels.add(label)
            calendar.add(Calendar.MONTH, -1)
        }
        return labels
    }

    private fun getWeekLabels(): List<String>? {
        val labels: MutableList<String> = java.util.ArrayList()
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
        for (i in 4 downTo 0) {
            val labelStart = sdf.format(calendar.time)
            calendar.add(Calendar.DATE, -6)
            val labelEnd = sdf.format(calendar.time)
            labels.add("$labelEnd - $labelStart")
            calendar.add(Calendar.DATE, -1)
        }
        return labels
    }

}*/

import android.app.DatePickerDialog
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Administrator.Adapters.ItemDoanhThuAdapter
import com.example.giuaky1.Administrator.model.DoanhThu
import com.example.giuaky1.Firebase.DataHandler
import com.example.giuaky1.Models.Order
import com.example.giuaky1.R
import com.github.mikephil.charting.charts.BarChart
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeDoanhThu : Fragment() {

    private lateinit var barChart: BarChart
    private lateinit var startDateEditText: EditText
    private lateinit var endDateEditText: EditText
    private lateinit var view: View
    private lateinit var DoanhThuButton: Button
    private lateinit var DoanhThuChartButton: Button
    private lateinit var listOrder: List<Order>
    private lateinit var listDoanhThu: List<DoanhThu>
    private lateinit var DoanhThuRecyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_home_admin_doanh_thu, container, false)
        setControl()
        Log.d("DoanhThu123", "onCreateView")
        DataHandler.readAllOrdersList { orderList ->
            listOrder = orderList
            Log.d("DoanhThu123", listOrder.toString())}
        /*
        DataHandler.getOrderWithState("Đã giao hàng") { orderList ->
            listOrder = orderList
            Log.d("DoanhThu123", listOrder.toString())
        }*/
        setDate()
        setDoanhThu()
        return view
    }

    private fun setDoanhThu() {

        DoanhThuButton.setOnClickListener {
            if(startDateEditText.text.toString().isEmpty() || endDateEditText.text.toString().isEmpty()) {
                Toast.makeText(view.context, "Vui lòng chọn ngày bắt đầu và ngày kết thúc", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            DoanhThuRecyclerView.visibility = View.VISIBLE
            listDoanhThu = DataHandler.getListDoanhThuTheoNgay(startDateEditText.text.toString(), endDateEditText.text.toString(), listOrder)
            Log.d("DoanhThu123", listDoanhThu.toString())
            DoanhThuRecyclerView.adapter = ItemDoanhThuAdapter(listDoanhThu)
        }
    }

    private fun setControl() {
        startDateEditText = view.findViewById(R.id.startDateEditText)
        endDateEditText = view.findViewById(R.id.endDateEditText)
        DoanhThuButton = view.findViewById(R.id.DoanhThuButton)
        DoanhThuChartButton = view.findViewById(R.id.DoanhThuChartButton)
        DoanhThuRecyclerView = view.findViewById(R.id.DoanhThuRecyclerView)

    }

    private fun setDate() {

        startDateEditText.setOnClickListener { view ->
            val now = Calendar.getInstance()
            DatePickerDialog(
                view.context,
                { _, year, monthOfYear, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, monthOfYear)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time)
                    if(endDateEditText.text.toString().isNotEmpty()) {
                        val endDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(endDateEditText.text.toString())
                        if(selectedDate.time.after(endDate)) {
                            Toast.makeText(view.context, "Ngày bắt đầu không thể sau ngày kết thúc", Toast.LENGTH_SHORT).show()
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
                    val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time)
                    if(startDateEditText.text.toString().isNotEmpty()) {
                        val startDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(startDateEditText.text.toString())
                        if(selectedDate.time.before(startDate)) {
                            Toast.makeText(view.context, "Ngày kết thúc không thể trước ngày bắt đầu", Toast.LENGTH_SHORT).show()
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

}