package com.example.giuaky1.Administrator.Fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.Models.Order
import com.example.giuaky1.R
import com.example.giuaky1.databinding.FragmentHomeAdminDonHangBinding
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

class HomeDonHang : Fragment() {
    lateinit var binding : FragmentHomeAdminDonHangBinding

    private lateinit var barChart: BarChart

    private lateinit var changeButton: Button

    private var isChart1 = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeAdminDonHangBinding.inflate(inflater,container,false)

//        Log.d("SUPER LONGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG LABEL: ","I AM HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        changeButton = binding.MonthWeekDHBtn

        barChart = binding.DonHangBarChar
        FirebaseFunction.readAllOrdersList { orderList ->
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
        val Past5Months: List<String?> = java.util.ArrayList(getMonthLabels())
        Collections.reverse(Past5Months)

        val entries: MutableList<BarEntry?> = mutableListOf()

        for (i in Past5Months.indices) {
            val regexPattern = "^[0-9]{2}/" + Past5Months[i] + "$"
            val pattern = Pattern.compile(regexPattern)
            var SUM: Int = 0
            for (order in orderList) {
                if (pattern.matcher(order.day).matches()) {
                    SUM += 1
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
        val GiaTriDoanhThu: MutableList<Int?> = java.util.ArrayList()
        for (i in Past5Weeks.indices) {
            var SUM: Int = 0
            for (order in orderList) {
                val entryDate = Calendar.getInstance()
                val dateParts = order.day.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                entryDate[dateParts[2].toInt(), dateParts[1].toInt() - 1] = dateParts[0].toInt()
                if (isWithinLastSevenDays(entryDate,calendar)) {
                    SUM += 1
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
}