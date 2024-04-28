package com.example.giuaky1.Administrator.Activitys
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TabWidget
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTabHost
import com.example.giuaky1.Administrator.Fragments.*
import com.example.giuaky1.Firebase.DataHandler
import com.example.giuaky1.R
import com.google.android.material.badge.BadgeDrawable

class OrderList : AppCompatActivity() {

    private var mTabHost: FragmentTabHost? = null
    private var tabWidget: TabWidget? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_list)

        mTabHost = findViewById(R.id.tabHost)
        tabWidget = findViewById(R.id.tabWidget)
        mTabHost?.setup(this, supportFragmentManager, R.id.tabContent)

        mTabHost?.newTabSpec("tab1")?.setIndicator("Chờ xác nhận")?.let {
            mTabHost?.addTab(
                it,
                ChoXacNhanFragment::class.java, null
            )
        }
        mTabHost?.newTabSpec("tab2")?.setIndicator("Chờ giao hàng")?.let {
            mTabHost?.addTab(
                it,
                ChoGiaoHangFragment::class.java, null
            )
        }
        mTabHost?.newTabSpec("tab3")?.setIndicator("Đã giao")?.let {
            mTabHost?.addTab(
                it,
                DaGiaoFragment::class.java, null
            )
        }
        mTabHost?.newTabSpec("tab4")?.setIndicator("Đã hủy")?.let {
            mTabHost?.addTab(
                it,
                DaHuyFragment::class.java, null
            )
        }
    }

}