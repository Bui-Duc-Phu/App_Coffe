package com.example.giuaky1.Administrator.Fragments


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Administrator.Adapters.ItemDoanhThuAdapter
import com.example.giuaky1.Administrator.Adapters.ItemDonHangAdapter
import com.example.giuaky1.Administrator.model.DoanhThu
import com.example.giuaky1.Administrator.model.DonHang
import com.example.giuaky1.Firebase.DataHandler
import com.example.giuaky1.Models.Order
import com.example.giuaky1.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeDonHang : Fragment() {

    private lateinit var barChart: BarChart
    private lateinit var startDateEditText: EditText
    private lateinit var endDateEditText: EditText
    private lateinit var view: View
    private lateinit var DonHangButton: Button
    private lateinit var DonHangChartButton: Button
    private lateinit var listOrder: List<Order>
    private lateinit var listDonHang: List<DonHang>
    private lateinit var DonHangRecyclerView: RecyclerView
    private lateinit var DonHangHeader: LinearLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_home_admin_don_hang, container, false)
        setControl()
        DataHandler.getOrderWithState("Đã giao hàng") { orderList ->
            listOrder = orderList
        }
        setDate()
        setDoanhThu()
        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setDoanhThu() {
        DonHangButton.setOnClickListener {
            if(startDateEditText.text.toString().isEmpty() || endDateEditText.text.toString().isEmpty()) {
                Toast.makeText(view.context, "Vui lòng chọn ngày bắt đầu và ngày kết thúc", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            DonHangRecyclerView.visibility = View.VISIBLE
            DonHangHeader.visibility = View.VISIBLE
            barChart.visibility = View.GONE
            DataHandler.getListDonHangTheoNgay(startDateEditText.text.toString(), endDateEditText.text.toString(), listOrder) { listDonHang ->
                this.listDonHang = listDonHang // Initialize listDonHang here
                DonHangRecyclerView.adapter = ItemDonHangAdapter(listDonHang)
                DonHangRecyclerView.adapter?.notifyDataSetChanged()
            }
        }
        DonHangChartButton.setOnClickListener {
            if(startDateEditText.text.toString().isEmpty() || endDateEditText.text.toString().isEmpty()) {
                Toast.makeText(view.context, "Vui lòng chọn ngày bắt đầu và ngày kết thúc", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            DonHangRecyclerView.visibility = View.GONE
            DonHangHeader.visibility = View.GONE
            barChart.visibility = View.VISIBLE
            if (listDonHang.isEmpty()) {
                Toast.makeText(view.context, "Không có dữ liệu để hiển thị", Toast.LENGTH_SHORT).show()
            } else {
                createChart(listDonHang)
            }
        }
    }


    private fun createChart(listDonHang: List<DonHang>) {
        // Tạo dữ liệu cho biểu đồ từ listDonHang
        val entries = listDonHang.mapIndexed { index, donHang ->
            BarEntry(index.toFloat(), donHang.quantity.toFloat())
        }

        // Tạo một BarDataSet với entries
        val barDataSet = BarDataSet(entries, "Đơn hàng")

        // Tạo một danh sách màu
        val colors = ArrayList<Int>()
        for (i in listDonHang.indices) {
            colors.add(ColorTemplate.MATERIAL_COLORS[i % ColorTemplate.MATERIAL_COLORS.size])
        }

        // Thiết lập màu cho BarDataSet
        barDataSet.colors = colors

        // Tạo một BarData với barDataSet
        val barData = BarData(barDataSet)

        // Thiết lập dữ liệu cho biểu đồ và làm mới biểu đồ
        barChart.data = barData

        // Tạo một danh sách LegendEntry
        val legendEntries = listDonHang.mapIndexed { index, donHang ->
            LegendEntry().apply {
                formColor = colors[index]
                label = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).run {
                    val date = parse(donHang.date)
                    SimpleDateFormat("dd/MM", Locale.getDefault()).format(date)
                }
            }
        }

        // Thiết lập danh sách LegendEntry cho chú thích của biểu đồ
        barChart.legend.setCustom(legendEntries)

        barChart.invalidate()
    }
    private fun setControl() {
        startDateEditText = view.findViewById(R.id.startDateEditText)
        endDateEditText = view.findViewById(R.id.endDateEditText)
        DonHangButton = view.findViewById(R.id.DonHangButton)
        DonHangChartButton = view.findViewById(R.id.DonHangChartButton)
        DonHangRecyclerView = view.findViewById(R.id.DonHangRecyclerView)
        DonHangRecyclerView.layoutManager = LinearLayoutManager(view.context)
        DonHangHeader = view.findViewById(R.id.DonHangHeader)
        barChart = view.findViewById(R.id.DonHangBarChart)
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
                                "Ngày bắt đầu không thể sau ngày kết thúc",
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
                                "Ngày kết thúc không thể trước ngày bắt đầu",
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

}