package uk.whitecrescent.waqti.frontend.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.FrameLayout
import android.widget.TimePicker
import com.github.basshelal.threetenktx.threetenabp.time
import org.threeten.bp.LocalDateTime
import uk.whitecrescent.waqti.R

class DateTimePicker
@JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0)
    : FrameLayout(context, attributeSet, defStyle) {

    val datePicker = LayoutInflater.from(context)
            .inflate(R.layout.view_date_picker, this, false) as DatePicker
    val timePicker = LayoutInflater.from(context)
            .inflate(R.layout.view_time_picker, this, false) as TimePicker
    var time = LocalDateTime.MIN!!

    init {
        timePicker.apply {
            setIs24HourView(true)
            visibility = View.INVISIBLE
        }
        this.addView(datePicker)
        this.addView(timePicker)
    }

    fun switchToTimePicker() {
        this.time = time(datePicker.year, datePicker.month + 1,
                datePicker.dayOfMonth, timePicker.hour, timePicker.minute)
        timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            this.time = time(datePicker.year, datePicker.month + 1, datePicker.dayOfMonth, hourOfDay, minute)
        }

        datePicker.visibility = View.INVISIBLE
        timePicker.visibility = View.VISIBLE
    }

    fun setInitialTime(time: LocalDateTime) {
        datePicker.updateDate(time.year, time.monthValue - 1, time.dayOfMonth)
        timePicker.apply {
            hour = time.hour
            minute = time.minute
        }
    }

}