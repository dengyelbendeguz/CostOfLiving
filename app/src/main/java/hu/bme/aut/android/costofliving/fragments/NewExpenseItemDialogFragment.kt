package hu.bme.aut.android.expenselist.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.expenselist.R
import hu.bme.aut.android.expenselist.data.ExpenseItem
import hu.bme.aut.android.expenselist.databinding.DialogNewExpenseItemBinding

class NewExpenseItemDialogFragment : DialogFragment() {
    interface NewExpenseItemDialogListener {
        fun onExpenseItemCreated(newItem: ExpenseItem)
    }

    private lateinit var listener: NewExpenseItemDialogListener

    private lateinit var binding: DialogNewExpenseItemBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewExpenseItemDialogListener
            ?: throw RuntimeException("Activity must implement the NewExpenseItemDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNewExpenseItemBinding.inflate(LayoutInflater.from(context))
        binding.spCategory.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.category_items)
        )

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.new_expense_item)
            .setView(binding.root)
            .setPositiveButton(R.string.button_ok) { dialogInterface, i ->
                if (isValid()) {
                    listener.onExpenseItemCreated(getExpenseItem())
                }
            }
            .setNegativeButton(R.string.button_cancel, null)
            .create()
    }

    private fun isValid() = binding.etName.text.isNotEmpty()

    private fun getExpenseItem() = ExpenseItem(
        name = binding.etName.text.toString(),
        description = binding.etDescription.text.toString(),
        estimatedPrice = binding.etEstimatedPrice.text.toString().toIntOrNull() ?: 0,
        category = ExpenseItem.Category.getByOrdinal(binding.spCategory.selectedItemPosition)
            ?: ExpenseItem.Category.BOOK/*,
        isBought = binding.cbAlreadyPurchased.isChecked*/
    )


    companion object {
        const val TAG = "NewExpenseItemDialogFragment"
    }
}
