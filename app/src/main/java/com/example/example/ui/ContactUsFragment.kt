package com.example.example.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.example.R
import com.example.example.databinding.FragmentContactUsBinding


class ContactUsFragment : Fragment() {
    private lateinit var binding:FragmentContactUsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentContactUsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var toolbar= requireActivity().findViewById<Toolbar>(R.id.toolbar)
        val navController=Navigation.findNavController(requireActivity(),(R.id.nav_host_fragment))
        val appBarConfiguration=AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController(navController,appBarConfiguration)
    }
}