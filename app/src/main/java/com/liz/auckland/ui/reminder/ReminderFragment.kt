package com.liz.auckland.ui.reminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.liz.auckland.R
import com.liz.auckland.databinding.FragmentInfoBinding
import com.liz.auckland.databinding.FragmentReminderBinding
import com.liz.auckland.ui.main.MainViewModel

class ReminderFragment : Fragment() {
    lateinit var binding: FragmentReminderBinding
    lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reminder, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        initUI()
        observeViewModel()
        return binding.root
    }

    private fun initUI() {
    }

    private fun observeViewModel() {
    }

    companion object {
        // Singleton
        val instance = ReminderFragment()
    }
}