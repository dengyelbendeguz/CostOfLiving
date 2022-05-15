package hu.bme.aut.android.costofliving.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.costofliving.data.LoanItem
import hu.bme.aut.android.expenselist.databinding.DialogNewLoanItemBinding

class NewLoanItemDialogFragment(private val user: String) : DialogFragment() {
    interface NewLoanItemDialogListener {
        fun onLoanItemCreated(newItem: LoanItem)
    }

    private lateinit var listener: NewLoanItemDialogListener
    private lateinit var binding: DialogNewLoanItemBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewLoanItemDialogListener
            ?: throw RuntimeException("Activity must implement the NewLoanItemDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNewLoanItemBinding.inflate(LayoutInflater.from(context))

        return AlertDialog.Builder(requireContext())
            .setTitle(hu.bme.aut.android.expenselist.R.string.new_loan_item)
            .setView(binding.root)
            .setPositiveButton(hu.bme.aut.android.expenselist.R.string.button_ok) { _, _ ->
                if (isValid()) {
                    listener.onLoanItemCreated(getLoanItem())
                }
            }
            .setNegativeButton(hu.bme.aut.android.expenselist.R.string.button_cancel, null)
            .create()
    }

    private fun isValid() = binding.etLoanerName.text.isNotEmpty()

    private fun getLoanItem() = LoanItem(
        loanerName = binding.etLoanerName.text.toString(),
        description = binding.etDescription.text.toString(),
        amount = binding.etAmount.text.toString().toIntOrNull() ?: 0,
        isLoaner = binding.tbLoanerToggle.isChecked,
        username = user
    )

    companion object {
        const val TAG = "NewLoanItemDialogFragment"
    }
}
