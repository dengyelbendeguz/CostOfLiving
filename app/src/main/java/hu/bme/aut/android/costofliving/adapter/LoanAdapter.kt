package hu.bme.aut.android.costofliving.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.costofliving.data.LoanItem
import hu.bme.aut.android.expenselist.databinding.ItemLoanListBinding

class LoanAdapter(private val listener: LoanItemClickListener) :
    RecyclerView.Adapter<LoanAdapter.LoanViewHolder>() {

    private val items = mutableListOf<LoanItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LoanViewHolder(
        ItemLoanListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: LoanViewHolder, position: Int) {
        val loanItem = items[position]

        holder.binding.tvLoanerName.text = loanItem.loanerName
        holder.binding.tvDescription.text = loanItem.description
        holder.binding.tvCost.text = "${loanItem.amount} Ft"
        val color = setColor(loanItem.isLoaner)
        holder.binding.linLayItemLoanList.setBackgroundColor(color)
        holder.binding.ibRemove.setOnClickListener{
            listener.onItemDeleted(loanItem)
        }
    }

    private fun setColor(isLoan: Boolean): Int {
        if (!isLoan)
            return "#3363FF".toColorInt()
        else
            return "#B3151A".toColorInt()
    }

    fun addItem(item: LoanItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun update(loanItems: List<LoanItem>) {
        items.clear()
        items.addAll(loanItems)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    interface LoanItemClickListener {
        fun onItemChanged(item: LoanItem)
        fun onItemDeleted(item: LoanItem)
    }

    inner class LoanViewHolder(val binding: ItemLoanListBinding) : RecyclerView.ViewHolder(binding.root)
}

