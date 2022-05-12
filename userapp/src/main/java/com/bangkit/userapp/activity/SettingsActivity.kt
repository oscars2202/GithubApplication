package com.bangkit.userapp.activity

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.userapp.R
import com.bangkit.userapp.alarm.AlarmReceiver
import com.bangkit.userapp.databinding.ActivitySettingsBinding
import com.bangkit.userapp.fragment.TimePickerFragment
import java.text.SimpleDateFormat
import java.util.*

class SettingsActivity : AppCompatActivity(), View.OnClickListener, TimePickerFragment.DialogTimeListener {

    private var binding: ActivitySettingsBinding? = null
    private lateinit var alarmReceiver: AlarmReceiver
    private lateinit var switch: Switch

    companion object {
        private const val TIME_PICKER_REPEAT_TAG = "TimePickerRepeat"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.btnSetRepeating?.setOnClickListener(this)
        binding?.btnRepeating?.setOnClickListener(this)

        alarmReceiver = AlarmReceiver()
        val filter = IntentFilter(ConnectivityManager.EXTRA_NO_CONNECTIVITY).apply {
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        }
        registerReceiver(alarmReceiver, filter)

        switch = findViewById(R.id.btnRepeating)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnRepeating -> {
                if (switch.isChecked) {
                    val repeatMessage = binding?.tvRepeatingTime?.text.toString()
                    alarmReceiver.setRepeatingAlarm(this, AlarmReceiver.TYPE_REPEATING, repeatMessage)
                    Toast.makeText(this, "Alarm set up", Toast.LENGTH_SHORT).show()
                } else {
                    alarmReceiver.cancelAlarm(this, AlarmReceiver.TYPE_REPEATING)
                    Toast.makeText(this, "Alarm turn off", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 9)
        }

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)

        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        when (tag) {
            TIME_PICKER_REPEAT_TAG -> binding?.tvRepeatingTime?.text = dateFormat.format(calendar.time)
            else -> {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}