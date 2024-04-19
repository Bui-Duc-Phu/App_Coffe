package com.example.giuaky1.Administrator.Fragments

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomePageViewAdapter(fragmentActivity: Home) :
    FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeDonHang()
            1 -> HomeDoanhThu()
            2 -> HomeSanPham()
            else -> HomeDonHang()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}
