package com.example.giuaky1.Fragments

import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.giuaky1.Adapters.OrderCompletedAdapter
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.databinding.FragmentOrderCompletedBinding
import com.google.firebase.auth.FirebaseAuth



class OrderCompleted : Fragment() {
    lateinit var binding:FragmentOrderCompletedBinding
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderCompletedBinding.inflate(layoutInflater,container,false)
        auth=FirebaseAuth.getInstance()
        init_()

        return binding.root
    }

    private fun init_() {

        recylerview()











    }

    private fun recylerview() {
        FirebaseFunction.readOrdersCompleted(auth.currentUser!!.uid) { list->
            val adapter  = OrderCompletedAdapter(requireContext(),list)
            binding.orderRecylerview.adapter = adapter
        }
    }


}