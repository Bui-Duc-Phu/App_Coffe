package com.example.giuaky1.Administrator.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.giuaky1.databinding.FragmentHomeAdminBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class Home : Fragment() {
    private lateinit var tablayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var homePageViewAdapter: HomePageViewAdapter

    lateinit var binding : FragmentHomeAdminBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeAdminBinding.inflate(inflater,container,false)
        tablayout = binding.TabLayout
        viewPager2 = binding.ViewPage
        homePageViewAdapter = HomePageViewAdapter(this)
        viewPager2.setAdapter(homePageViewAdapter)
        tablayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager2.setCurrentItem(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tablayout.getTabAt(position)!!.select()
            }
        })
        return binding.root
    }
}
//class Home : Fragment() {
//    private lateinit var pieChart: PieChart
//
//    private lateinit var barChart: BarChart
//
//    lateinit var binding : FragmentHomeAdminBinding
//
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        binding = FragmentHomeAdminBinding.inflate(inflater,container,false)
//
//        Log.d("SUPER LONGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG LABEL: ","I AM HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
//        FirebaseFunction.readAllOrdersList { orderList ->
//            test(orderList)
//        }
//
//        pieChart = binding.pieChart
//        setupPieChart()
//        barChart = binding.barChart
//        setupBarChart()
//        return binding.root
//    }
//
//    private fun test(orderList: List<Order>){
//        for (order in orderList) {
//            val firstOrder = orderList[0]
//
//            val checkoutValue = firstOrder.checkout
//
//            Log.d("Checkout", "Checkout Value of the first order: $checkoutValue")
//            Log.d("Order", "Order: $order")
//
//        }
//    }
//
//    private fun setupPieChart() {
//        val entries = listOf(
//            PieEntry(18.5f, "Category A"),
//            PieEntry(26.7f, "Category B"),
//            PieEntry(24.0f, "Category C"),
//            PieEntry(30.8f, "Category D")
//        )
//
//        val dataSet = PieDataSet(entries, null)
//        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
//        dataSet.valueTextSize = 20f
//
//        val data = PieData(dataSet)
//        pieChart.data = data
//
//        pieChart.holeRadius = 15f
//        pieChart.transparentCircleRadius = 30f
//        pieChart.description.isEnabled = false
//
//        pieChart.invalidate()
//
//    }
//
//    private fun setupBarChart() {
//        val entries = listOf(
//            BarEntry(0f, 10000f),
//            BarEntry(1f, 20000f),
//        )
//        val categories = listOf("Tháng trước", "Tháng này")
//
//        val dataSet = BarDataSet(entries, null)
//        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
//        dataSet.valueTextSize = 20f
//        dataSet.setValueTextColor(Color.WHITE)
//
//        val data = BarData(dataSet)
//        barChart.data = data
//
//        val yAxis = barChart.axisLeft
//        yAxis.textColor = Color.WHITE
//
//        val xAxis = barChart.xAxis
//        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.granularity = 1f
//        xAxis.textColor = Color.WHITE
//        xAxis.setDrawGridLines(false)
//        xAxis.valueFormatter = IndexAxisValueFormatter(categories)
//
//        barChart.data = data
//
//        barChart.description.isEnabled = false
//
//        barChart.invalidate()
//
//    }
//
//}