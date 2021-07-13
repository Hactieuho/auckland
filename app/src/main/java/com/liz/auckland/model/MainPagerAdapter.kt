package com.liz.auckland.model

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.liz.auckland.ui.home.HomeFragment
import com.liz.auckland.ui.info.InfoFragment
import com.liz.auckland.ui.reminder.ReminderFragment

class MainPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 1

//    override fun createFragment(position: Int): Fragment = when(position) {
//        1 -> ReminderFragment.instance
//        2 -> InfoFragment.instance
//        else -> HomeFragment.instance
//    }

    override fun createFragment(position: Int) = HomeFragment.instance
}