package com.example.giuaky1.Administrator.Activitys
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
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
    private var btnBack:ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_list)
        btnBack=findViewById(R.id.backBtn)
        btnBack?.setOnClickListener {
            finish()
        }
        mTabHost = findViewById(R.id.tabHost)
        tabWidget = findViewById(R.id.tabWidget)
        mTabHost?.setup(this, supportFragmentManager, R.id.tabContent)

        mTabHost?.newTabSpec("tab1")?.setIndicator(getString(R.string.ch_x_c_nh_n))?.let {
            mTabHost?.addTab(
                it,
                ChoXacNhanFragment::class.java, null
            )
        }
        mTabHost?.newTabSpec("tab2")?.setIndicator(getString(R.string.ch_giao_h_ng))?.let {
            mTabHost?.addTab(
                it,
                ChoGiaoHangFragment::class.java, null
            )
        }
        mTabHost?.newTabSpec("tab3")?.setIndicator(getString(R.string.giao))?.let {
            mTabHost?.addTab(
                it,
                DaGiaoFragment::class.java, null
            )
        }
        mTabHost?.newTabSpec("tab4")?.setIndicator(getString(R.string.h_y))?.let {
            mTabHost?.addTab(
                it,
                DaHuyFragment::class.java, null
            )
        }
    }

}