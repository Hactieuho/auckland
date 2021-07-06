package com.liz.auckland.ui.info

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.liz.auckland.R
import com.liz.auckland.databinding.FragmentInfoBinding
import com.liz.auckland.ui.main.MainViewModel
import java.util.*

class InfoFragment : Fragment() {
    lateinit var binding: FragmentInfoBinding
    val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_info, container, false)
        initUI()
        observeViewModel()
        return binding.root
    }

    private fun initUI() {
    }

    private fun observeViewModel() {
    }

    fun createAlarm(date: Date?) {
        val i = Intent(AlarmClock.ACTION_SET_ALARM)
        i.putExtra(AlarmClock.EXTRA_MESSAGE, "New Alarm")
        i.putExtra(AlarmClock.EXTRA_HOUR, 10)
        i.putExtra(AlarmClock.EXTRA_SKIP_UI, false)
        i.putExtra(AlarmClock.EXTRA_MINUTES, 30)
        startActivity(i)
    }

    companion object {
        // Singleton
        val instance = InfoFragment()
    }
}