package com.obu.restaurant.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.obu.restaurant.presentation.fragments.AddingProductsFragment
import com.obu.restaurant.presentation.fragments.OrderedProductsFragment

class AtencionAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OrderedProductsFragment()
            1 -> AddingProductsFragment()
            else -> OrderedProductsFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2 // Dos pestañas
    }
}