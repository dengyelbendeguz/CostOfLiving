package hu.bme.aut.android.costofliving.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.costofliving.data.ExpenseItem
import hu.bme.aut.android.expenselist.databinding.ItemExpenseListBinding

class ExpenseAdapter(private val listener: ExpenseItemClickListener) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private val items = mutableListOf<ExpenseItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ExpenseViewHolder(
        ItemExpenseListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expenseItem = items[position]

        holder.binding.cbIsShared.isChecked = expenseItem.isShared
        holder.binding.tvName.text = expenseItem.name
        holder.binding.tvDescription.text = expenseItem.description
        holder.binding.tvCategory.text = expenseItem.category
        holder.binding.tvCost.text = "${expenseItem.cost} Ft"
        val color = setColor(expenseItem.isExpense)
        holder.binding.linLayItemExpenseList.setBackgroundColor(color)
        holder.binding.ibRemove.setOnClickListener{
            listener.onItemDeleted(expenseItem)
        }
        holder.binding.cbIsShared.setOnClickListener {
            expenseItem.isShared = holder.binding.cbIsShared.isChecked
            listener.onItemChanged(expenseItem)
        }
    }

    private fun setColor(isExpense: Boolean): Int {
        if (isExpense)
            return "#3363FF".toColorInt()
        else
            return "#B3151A".toColorInt()
    }

    fun addItem(item: ExpenseItem) {
        //items.add(item)
        items.add(0, item)
        //notifyItemInserted(items.size - 1)
        notifyItemInserted(0)
        notifyDataSetChanged()
    }

    fun update(expenseItems: List<ExpenseItem>) {
        items.clear()
        //items.addAll(expenseItems)
        items.addAll(expenseItems.asReversed())
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    interface ExpenseItemClickListener {
        fun onItemChanged(item: ExpenseItem)
        fun onItemDeleted(item: ExpenseItem)
    }

    inner class ExpenseViewHolder(val binding: ItemExpenseListBinding) : RecyclerView.ViewHolder(binding.root)
}

