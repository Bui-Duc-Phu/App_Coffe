package com.example.giuaky1.Administrator.Fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.Models.Order
import com.example.giuaky1.R
import com.example.giuaky1.databinding.FragmentHomeAdminSanPhamBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Locale
import java.util.regex.Pattern

class HomeSanPham : Fragment() {

    lateinit var binding : FragmentHomeAdminSanPhamBinding

    private lateinit var pieChart: PieChart

    private lateinit var changeButton: Button

    private lateinit var timeView: TextView

    private var isChart1 = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeAdminSanPhamBinding.inflate(inflater,container,false)

//        Log.d("SUPER LONGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG LABEL: ","I AM HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        changeButton = binding.MonthWeekSPBtn

        pieChart = binding.SanPhamPieChart

        timeView = binding.TimePieChartLabel

        FirebaseFunction.readAllOrdersList { orderList ->
            setupBarChartMonth(orderList)
            changeButton.setOnClickListener(View.OnClickListener {
                if (isChart1) {
                    changeButton.setText(getString(R.string.frag_TK_week))
                    setupPieChartWeek(orderList)
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
        val entries: MutableList<PieEntry> = ArrayList()
        var TONGSP = 0

        val listSanPham = mutableMapOf<String, Int>()
        val calendar = Calendar.getInstance()
        for (order in orderList){
            val entryDate = Calendar.getInstance()
            val dateParts = order.day.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            entryDate[dateParts[2].toInt(), dateParts[1].toInt() - 1] = dateParts[0].toInt()
            if (isWithinOneMonth(entryDate,calendar)) {
                for(product in order.products){
                    if(!listSanPham.contains(product.productName)){
                        listSanPham[product.productName] = product.quantity.toInt()
                    }
                    else{
                        listSanPham[product.productName] = listSanPham[product.productName]!! +product.quantity.toInt()
                    }
                    TONGSP+=product.quantity.toInt()
                }
            }
        }
        for ((key, value) in listSanPham) {
            entries.add( PieEntry(
                (value.toFloat() / TONGSP * 10000).toInt() / 100.0f,
                key
            ))
        }

        val dataSet = PieDataSet(entries, null)
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
        dataSet.valueTextSize = 20f
        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.holeRadius = 25f
        pieChart.transparentCircleRadius = 30f
        pieChart.description.isEnabled = false
        pieChart.invalidate()
        timeView.setText(getMonth())
    }

    private fun setupPieChartWeek(orderList: List<Order>) {
        val entries: MutableList<PieEntry> = ArrayList()
        var TONGSP = 0

        val listSanPham = mutableMapOf<String, Int>()
        val calendar = Calendar.getInstance()
        for (order in orderList){
            val entryDate = Calendar.getInstance()
            val dateParts = order.day.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            entryDate[dateParts[2].toInt(), dateParts[1].toInt() - 1] = dateParts[0].toInt()
            if (isWithinLastSevenDays(entryDate,calendar)) {
                for(product in order.products){
                    if(!listSanPham.contains(product.productName)){
                        listSanPham[product.productName] = product.quantity.toInt()
                    }
                    else{
                        listSanPham[product.productName] = listSanPham[product.productName]!! +product.quantity.toInt()
                    }
                    TONGSP+=product.quantity.toInt()
                }
            }
        }
        for ((key, value) in listSanPham) {
            entries.add( PieEntry(
                (value.toFloat() / TONGSP * 10000).toInt() / 100.0f,
                key
            ))
        }

        val dataSet = PieDataSet(entries, null)
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
        dataSet.valueTextSize = 20f
        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.holeRadius = 25f
        pieChart.transparentCircleRadius = 30f
        pieChart.description.isEnabled = false
        pieChart.invalidate()
        timeView.setText(getWeek())
    }

    private fun isWithinOneMonth(dateToCheck: Calendar, currentDate: Calendar): Boolean {
        val oneMonthFromNow = currentDate.clone() as Calendar
        oneMonthFromNow.add(Calendar.MONTH, -1)
        return dateToCheck.after(oneMonthFromNow) && !dateToCheck.after(currentDate)
    }

    private fun isWithinLastSevenDays(dateToCheck: Calendar, currentDate: Calendar): Boolean {
        val sevenDaysAgo = currentDate.clone() as Calendar
        sevenDaysAgo.add(Calendar.DATE, -7)
        return dateToCheck.after(sevenDaysAgo) && !dateToCheck.after(currentDate)
    }

    private fun getMonth(): String? {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val endDate = dateFormat.format(calendar.time)
        calendar.add(Calendar.MONTH, -1)
        val startDate = dateFormat.format(calendar.time)
        return "$startDate - $endDate"
    }

    fun getWeek(): String? {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val endDate = dateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        val startDate = dateFormat.format(calendar.time)
        return "$startDate - $endDate"
    }

}