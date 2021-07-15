package com.liz.auckland.util

import android.content.Intent
import android.content.SharedPreferences
import android.provider.AlarmClock
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liz.auckland.R
import com.liz.auckland.resource.Resource
import com.liz.auckland.ui.main.MainActivity
import java.text.SimpleDateFormat
import java.util.*

inline fun <reified T> SharedPreferences.saveItem(key: String, item: T?) {
    val json = Gson().toJson(item)
    edit { putString(key, json) }
}

inline fun <reified T> SharedPreferences.getItem(key: String): T? {
    val json = getString(key, "")
    if (!json.isNullOrBlank()) {
        val type = object : TypeToken<T>() {}.type
        return Gson().fromJson(json, type)
    }
    return null
}

inline fun <reified T> SharedPreferences.getList(spListKey: String): ArrayList<T>? {
    val listJson = getString(spListKey, "")
    if (!listJson.isNullOrBlank()) {
        val type = object : TypeToken<ArrayList<T>>() {}.type
        return Gson().fromJson(listJson, type)
    }
    return null
}

fun Date.formatDate() : String? {
    val dateTimeFormat = SimpleDateFormat("EEEE, dd MMMM", Locale.US)
    dateTimeFormat.timeZone = TimeZone.getTimeZone("UTC")
    return dateTimeFormat.format(this)
}

fun Date.formatTime() : String? {
    val dateTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
    dateTimeFormat.timeZone = TimeZone.getTimeZone("UTC")
    return dateTimeFormat.format(this)
}

fun Date.formatKey() : String? {
    val dateTimeFormat = SimpleDateFormat("yyyyMMdd HHmmss", Locale.US)
    dateTimeFormat.timeZone = TimeZone.getTimeZone("UTC")
    return dateTimeFormat.format(this)
}

fun Date.formatRequestCode() = (this.time/1000).toInt()

@BindingAdapter("app:onClick", "app:key")
fun Button.onClick(check: (String?) -> Unit, key: String?) {
    setOnClickListener { check(key) }
}

fun <T: Any?> LifecycleOwner.handleError(result: MutableLiveData<Resource<T>>) {
    result.observe(this) {
        if (it is Resource.Error) {
            ToastUtils.showShort(it.message)
        }
    }
}

fun Fragment.showList(title: Int, result: MutableLiveData<Resource<List<String?>?>>, current: MutableLiveData<String?>) {
    val nameList = result.value?.data?.sortedBy { it }?.toTypedArray()
    val checkedName = current.value?.let {
        nameList?.indexOf(
            it
        )
    } ?: 0

    MaterialAlertDialogBuilder(requireContext())
        .setTitle(resources.getString(title))
        .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which -> }
        .setSingleChoiceItems(nameList, checkedName) { dialog, which ->
            current.postValue(nameList?.get(which))
            dialog.dismiss()
        }
        .show()
}

// Chon thoi diem bao thuc (ngay, gio)
fun onChooseTime(date: Date?, title: String?, activity: AppCompatActivity) {
    // Chon ngay
    val datePicker = MaterialDatePicker.Builder.datePicker()
        .setTitleText("Choose a day")
        .setSelection(date?.time)
        .build()
    datePicker.addOnPositiveButtonClickListener { selection: Long? ->
        // Chon gio
        val c = Calendar.getInstance()
        date?.time?.let { c.timeInMillis = it - c.timeZone.rawOffset }
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(c[Calendar.HOUR_OF_DAY])
            .setMinute(c[Calendar.MINUTE])
            .setTitleText("Choose a time")
            .build()
        timePicker.show(
            activity.supportFragmentManager,
            MainActivity::class.java.simpleName
        )
        timePicker.addOnPositiveButtonClickListener { v: View? ->
            val calendar = Calendar.getInstance()
            selection?.let { calendar.timeInMillis = it }
            calendar[Calendar.HOUR_OF_DAY] = timePicker.hour
            calendar[Calendar.MINUTE] = timePicker.minute
            createAlarm(calendar.time, title, activity)
        }
    }
    datePicker.show(
        activity.supportFragmentManager,
        MainActivity::class.java.simpleName
    )
}

fun createAlarm(date: Date?, title: String?, activity: AppCompatActivity) {
    val i = Intent(AlarmClock.ACTION_SET_ALARM)
    val calendar = Calendar.getInstance()
    date?.let { calendar.time = it }
    val extraDays = arrayListOf(calendar.get(Calendar.DAY_OF_WEEK))
    val extraHours = calendar.get(Calendar.HOUR_OF_DAY)
    val extraMinutes = calendar.get(Calendar.MINUTE)
    i.putExtra(AlarmClock.EXTRA_MESSAGE, title)
    i.putExtra(AlarmClock.EXTRA_DAYS, extraDays)
    i.putExtra(AlarmClock.EXTRA_HOUR, extraHours)
    i.putExtra(AlarmClock.EXTRA_MINUTES, extraMinutes)
    i.putExtra(AlarmClock.EXTRA_SKIP_UI, false)
    activity.startActivity(i)
}