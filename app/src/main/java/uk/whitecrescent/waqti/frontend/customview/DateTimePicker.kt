package uk.whitecrescent.waqti.frontend.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.FrameLayout
import android.widget.TimePicker
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.Time
import uk.whitecrescent.waqti.frontend.hourCompat
import uk.whitecrescent.waqti.frontend.minuteCompat
import uk.whitecrescent.waqti.time

class DateTimePicker
@JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0)
    : FrameLayout(context, attributeSet, defStyle) {

    val datePicker = LayoutInflater.from(context)
            .inflate(R.layout.view_date_picker, this, false) as DatePicker
    val timePicker = LayoutInflater.from(context)
            .inflate(R.layout.view_time_picker, this, false) as TimePicker
    var time = Time.MIN!!

    init {
        timePicker.apply {
            setIs24HourView(true)
            visibility = View.INVISIBLE
        }
        this.addView(datePicker)
        this.addView(timePicker)
    }

    fun switchToTimePicker() {
        time = time(datePicker.year, datePicker.month + 1,
                datePicker.dayOfMonth, timePicker.hourCompat, timePicker.minuteCompat)
        timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            time = time(datePicker.year, datePicker.month + 1, datePicker.dayOfMonth, hourOfDay, minute)
        }

        datePicker.visibility = View.INVISIBLE
        timePicker.visibility = View.VISIBLE
    }

    fun setInitialTime(time: Time) {
        datePicker.updateDate(time.year, time.monthValue - 1, time.dayOfMonth)
        timePicker.apply {
            hourCompat = time.hour
            minuteCompat = time.minute
        }
    }

}