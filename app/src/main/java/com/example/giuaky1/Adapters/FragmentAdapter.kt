package com.example.giuaky1.Adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.giuaky1.Fragments.OrderCompleted
import com.example.giuaky1.Fragments.OrderNotCompleted

class FragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle

)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            OrderCompleted()
        } else {
            OrderNotCompleted()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}
