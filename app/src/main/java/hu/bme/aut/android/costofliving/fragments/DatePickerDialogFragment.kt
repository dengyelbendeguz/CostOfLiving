package hu.bme.aut.android.costofliving.fragments

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.expenselist.databinding.DialogDatePickerBinding
import java.util.*


class DatePickerDialogFragment: DialogFragment() {
    interface DatePickerDialogListener {
        fun onDatePicked(queryParams: MutableList<String>)
    }

    private lateinit var listener: DatePickerDialogListener
    private lateinit var binding: DialogDatePickerBinding
    private lateinit var datePicker: DatePicker

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? DatePickerDialogListener
            ?: throw RuntimeException("Activity must implement the DatePickerDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogDatePickerBinding.inflate(LayoutInflater.from(context))

        datePicker = binding.dpDatePicker
        datePicker.init(
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        ) { _, _, _, _ -> }
        val daySpinnerId: Int = Resources.getSystem().getIdentifier("day", "id", "android")
        if (daySpinnerId != 0) {
            val daySpinner: View = datePicker.findViewById(daySpinnerId)
            daySpinner.visibility = View.INVISIBLE
        }


        return AlertDialog.Builder(requireContext())
            .setTitle(hu.bme.aut.android.expenselist.R.string.date_picker)
            .setView(binding.root)
            .setPositiveButton(hu.bme.aut.android.expenselist.R.string.button_ok) { _, _ ->
                listener.onDatePicked(getDateFrom(datePicker) as MutableList<String>)
            }
            .setNegativeButton(hu.bme.aut.android.expenselist.R.string.button_cancel, null)
            .create()
    }

    private fun getDateFrom(picker: DatePicker): List<String> {
        val queryParams = mutableListOf<String>()
        queryParams.add(picker.year.toString())
        queryParams.add(picker.month.toString())
        queryParams.add(binding.rbYear.isChecked.toString())
        return queryParams
    }

    companion object {
        const val TAG = "DatePickerDialogFragment"
    }
}
