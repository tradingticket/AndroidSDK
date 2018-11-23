package it.trade.android.japanapp.ui.orderinput

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.widget.DatePicker
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var cb: (String) -> Unit

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(activity!!, this, year, month, day)
    }

    fun setDateSetPickerCallBack(cb: (String) -> Unit) {
        this.cb = cb
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        Log.d("OrderInputDatePicker", "picked a date: $year - ${month + 1} - $dayOfMonth")
        cb(String.format("%d%02d%02d", year, month + 1, dayOfMonth))
    }
}

