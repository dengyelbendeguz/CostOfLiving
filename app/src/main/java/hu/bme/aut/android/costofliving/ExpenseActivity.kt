package hu.bme.aut.android.costofliving

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.costofliving.adapter.ExpenseAdapter
import hu.bme.aut.android.costofliving.data.ExpenseItem
import hu.bme.aut.android.costofliving.data.ExpenseListDatabase
import hu.bme.aut.android.costofliving.fragments.NewExpenseItemDialogFragment
import hu.bme.aut.android.expenselist.R
import hu.bme.aut.android.expenselist.databinding.ActivityExpenseBinding
import java.util.*
import kotlin.concurrent.thread

class ExpenseActivity : AppCompatActivity(), ExpenseAdapter.ExpenseItemClickListener,
    NewExpenseItemDialogFragment.NewExpenseItemDialogListener {

    private lateinit var binding: ActivityExpenseBinding
    private lateinit var database: ExpenseListDatabase
    private lateinit var adapter: ExpenseAdapter
    private lateinit var expenseItems: List<ExpenseItem>
    private lateinit var user: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        user = intent.getStringExtra("username") ?: ""
        database = ExpenseListDatabase.getDatabase(applicationContext)
        binding.fab.setOnClickListener{
            NewExpenseItemDialogFragment(user).show(
                supportFragmentManager,
                NewExpenseItemDialogFragment.TAG
            )
        }
        initRecyclerView()
    }
    private fun initRecyclerView() {
        adapter = ExpenseAdapter(this)
        binding.rvExpense.layoutManager = LinearLayoutManager(this)
        binding.rvExpense.adapter = adapter
        loadItemsInBackground()
    }

    private fun loadItemsInBackground() {
        thread {
            val items = database.expenseItemDao().getAll(user)
            runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onItemChanged(item: ExpenseItem) {
        thread {
            database.expenseItemDao().update(item)
            Log.d("ExpenseActivity", "ExpenseItem update was successful")
        }
    }

    override fun onItemDeleted(item: ExpenseItem) {
        thread{
            database.expenseItemDao().deleteItem(item)
            Log.d("ExpenseActivity", "ExpenseItem delete was successful")
            loadItemsInBackground()
        }
    }

    override fun onExpenseItemCreated(newItem: ExpenseItem) {
        thread {
            database.expenseItemDao().insert(newItem)

            runOnUiThread {
                adapter.addItem(newItem)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.expense_menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_monthly_graph -> {
                loadMonthlyItems()
                createGraphSP()
                val profileIntent = Intent(this, GraphActivity::class.java)
                profileIntent.putExtra("text", "Monthly expenses")
                startActivity(profileIntent)
                true
            }
            R.id.action_yearly_graph -> {
                loadYearlyItems()
                createGraphSP()
                val profileIntent = Intent(this, GraphActivity::class.java)
                profileIntent.putExtra("text", "Yearly expenses")
                startActivity(profileIntent)
                true
            }
            R.id.action_list_expenses -> {
                loadItemsInBackground()
                true
            }
            R.id.action_log_out -> {
                val profileIntent = Intent(this, LoginActivity::class.java)
                profileIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(profileIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadYearlyItems() {
        var items: List<ExpenseItem>
        thread {
            items = database.expenseItemDao().getYearlyExpenses(
                user,
                Calendar.getInstance().get(Calendar.YEAR)
            )
            runOnUiThread {
                adapter.update(items)
            }
            expenseItems = items
        }
    }

    private fun loadMonthlyItems() {
        thread {
            val items = database.expenseItemDao().getMonthlyExpenses(
                user,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH)
            )
            runOnUiThread {
                adapter.update(items)
            }
            expenseItems = items
        }
    }

    private fun createGraphSP(){
        val categories = getCategories(user)
        val graphSP = this.getSharedPreferences("GRAPH", Context.MODE_PRIVATE)
        val editor:SharedPreferences.Editor =  graphSP.edit()
        editor.clear()
        editor.apply()
        for(category in categories){
            var cnt = 0
            for(item in expenseItems){
                if (item.category == category)
                    cnt++
            }
            editor.putFloat(category, cnt.toFloat())
        }
        editor.apply()
    }

    fun addNewCategory(user: String, categorySet: MutableSet<String>){
        val categoriesSP = this.getSharedPreferences("CATEGORIES", Context.MODE_PRIVATE)
        val editor:SharedPreferences.Editor =  categoriesSP.edit()
        editor.putStringSet(user, categorySet)
        editor.apply()
    }

    fun getCategories(user: String): MutableSet<String> {
        val categoriesSP = this.getSharedPreferences("CATEGORIES", Context.MODE_PRIVATE)
        val defaultSet: MutableSet<String> =
            resources.getStringArray(R.array.category_items).toMutableSet()
        return categoriesSP.getStringSet(user, setOf())?.toMutableSet() ?: defaultSet
    }
}