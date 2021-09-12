package com.liz.auckland.model

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.liz.auckland.ui.home.HomeFragment2
import javax.inject.Inject

class MainPagerAdapter2 @Inject constructor (
    fa: FragmentActivity,
    private val homeFragment: HomeFragment2
) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 1

    override fun createFragment(position: Int) = homeFragment
}