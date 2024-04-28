package com.example.giuaky1.Adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.giuaky1.Fragments.ChoGiaoHangFragment
import com.example.giuaky1.Fragments.ChoXacNhanFragment
import com.example.giuaky1.Fragments.DaGiaoFragment
import com.example.giuaky1.Fragments.DaHuyFragment
import com.example.giuaky1.Fragments.OrderCompleted
import com.example.giuaky1.Fragments.OrderNotCompleted

class FragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle

)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                ChoXacNhanFragment()
            }
            1 -> {
                ChoGiaoHangFragment()
            }
            2 -> {
                DaGiaoFragment()
            }
            3 -> {
                DaHuyFragment()
            }

            else -> {
                ChoXacNhanFragment()}
        }
    }

    override fun getItemCount(): Int {
        return 4
    }
}
