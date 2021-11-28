package hu.bme.aut.android.costofliving.adapter

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.expenselist.R
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

        //holder.binding.ivIcon.setImageResource(getImageResource(expenseItem.category))
        //holder.binding.cbIsBought.isChecked = expenseItem.isBought
        holder.binding.tvName.text = expenseItem.name
        holder.binding.tvDescription.text = expenseItem.description
        holder.binding.tvCategory.text = expenseItem.category.name
        holder.binding.tvCost.text = "${expenseItem.cost} Ft"

        val color = setColor(expenseItem.isExpense)
        holder.binding.linLayItemExpenseList.setBackgroundColor(color)
        /*{ buttonView, isChecked ->
            expenseItem.isExpense = isChecked
            listener.onItemChanged(expenseItem)
        }*/
        holder.binding.ibRemove.setOnClickListener{
            listener.onItemDeleted(expenseItem)
        }
    }

    private fun setColor(isExpense: Boolean): Int {
        if (isExpense)
            return "#3363FF".toColorInt()
        else
            return "#B3151A".toColorInt()
    }

    fun addItem(item: ExpenseItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun update(expenseItems: List<ExpenseItem>) {
        items.clear()
        items.addAll(expenseItems)
        notifyDataSetChanged()
    }


    @DrawableRes()
    private fun getImageResource(category: ExpenseItem.Category): Int {
        return when (category) {
            ExpenseItem.Category.FOOD -> R.drawable.groceries
            ExpenseItem.Category.ELECTRONIC -> R.drawable.lightning
            ExpenseItem.Category.BOOK -> R.drawable.open_book
        }
    }


    override fun getItemCount(): Int = items.size

    interface ExpenseItemClickListener {
        fun onItemChanged(item: ExpenseItem)
        fun onItemDeleted(item: ExpenseItem)
    }

    inner class ExpenseViewHolder(val binding: ItemExpenseListBinding) : RecyclerView.ViewHolder(binding.root)
}

