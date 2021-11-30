package hu.bme.aut.android.costofliving.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.costofliving.ExpenseActivity
import hu.bme.aut.android.costofliving.data.ExpenseItem
import hu.bme.aut.android.expenselist.databinding.DialogNewExpenseItemBinding
import java.util.*
import android.R

class NewExpenseItemDialogFragment(val user: String) : DialogFragment() {
    interface NewExpenseItemDialogListener {
        fun onExpenseItemCreated(newItem: ExpenseItem)
    }

    var categorySet: MutableSet<String> = mutableSetOf<String>()
    private lateinit var listener: NewExpenseItemDialogListener
    private lateinit var binding: DialogNewExpenseItemBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewExpenseItemDialogListener
            ?: throw RuntimeException("Activity must implement the NewExpenseItemDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNewExpenseItemBinding.inflate(LayoutInflater.from(context))

        //checks if categories shared preferences is not empty
        val tempSet = (activity as ExpenseActivity?)?.getCategories(user)!!
        if(tempSet != resources.getStringArray(hu.bme.aut.android.expenselist.R.array.category_items).toMutableSet()){
            categorySet = (activity as ExpenseActivity?)?.getCategories(user)!!
        }
        else{
            categorySet =
                resources.getStringArray(hu.bme.aut.android.expenselist.R.array.category_items).toMutableSet()
        }

        binding.spCategory.adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_dropdown_item,
            categorySet.toTypedArray()
        )

        binding.btAddCategory.setOnClickListener {
            if (binding.etNewCategory.text.isEmpty()){
                Toast.makeText(activity, "Enter a new category first!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else{
                // save and set categorySet (shared preferences)
                categorySet.add(binding.etNewCategory.text.toString())
                (activity as ExpenseActivity?)?.addNewCategory(user, categorySet)
                categorySet = (activity as ExpenseActivity?)?.getCategories(user)!!

                binding.spCategory.adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.simple_spinner_dropdown_item,
                    categorySet.toTypedArray()
                )
                return@setOnClickListener
            }
        }

        binding.btRemoveCategory.setOnClickListener {
            //TODO: a fenti kód (else) másolása, később refactorálni
            val categoryToBeDeleted = binding.spCategory.selectedItem.toString()
            categorySet.remove(categoryToBeDeleted)
            (activity as ExpenseActivity?)?.addNewCategory(user, categorySet)
            categorySet = (activity as ExpenseActivity?)?.getCategories(user)!!
            binding.spCategory.adapter = ArrayAdapter(
                requireContext(),
                R.layout.simple_spinner_dropdown_item,
                categorySet.toTypedArray()
            )
            return@setOnClickListener
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(hu.bme.aut.android.expenselist.R.string.new_expense_item)
            .setView(binding.root)
            .setPositiveButton(hu.bme.aut.android.expenselist.R.string.button_ok) { dialogInterface, i ->
                if (isValid()) {
                    listener.onExpenseItemCreated(getExpenseItem())
                }
            }
            .setNegativeButton(hu.bme.aut.android.expenselist.R.string.button_cancel, null)
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    private fun isValid() = binding.etName.text.isNotEmpty()

    private fun getExpenseItem() = ExpenseItem(
        name = binding.etName.text.toString(),
        description = binding.etDescription.text.toString(),
        cost = binding.etCost.text.toString().toIntOrNull() ?: 0,
        category =  binding.spCategory.selectedItem.toString(),
        isExpense = binding.tbExpenseToggle.isChecked,
        username = user
    )

    companion object {
        const val TAG = "NewExpenseItemDialogFragment"
    }
}
