package com.example.giuaky1.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.giuaky1.R
import com.example.giuaky1.databinding.FragmentHistoryBinding

import androidx.viewpager2.widget.ViewPager2
import com.example.giuaky1.Adapters.FragmentAdapter
import com.google.android.material.tabs.TabLayout


class HistoryFragment : Fragment() {
    lateinit var binding :FragmentHistoryBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater,container,false)


        setUpTabLayout()


        return binding.root
    }

    private fun setUpTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Chờ xác nhận"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Đang giao hàng") )
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Đã giao") )
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Đã hủy") )
        val adapter = FragmentAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter

        binding.tabLayout.addOnTabSelectedListener(object  : TabLayout.OnTabSelectedListener{
        override fun onTabSelected(tab: TabLayout.Tab?) {
            binding.viewPager.currentItem = tab!!.position
         }

        override fun onTabUnselected(tab: TabLayout.Tab?) {

        }

       override fun onTabReselected(tab: TabLayout.Tab?) {

       }})
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
           override fun onPageSelected(position: Int) {
               super.onPageSelected(position)
               binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
           }
        })
    }






}