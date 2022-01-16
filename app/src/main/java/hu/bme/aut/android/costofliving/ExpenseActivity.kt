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
import hu.bme.aut.android.costofliving.fragments.DatePickerDialogFragment
import hu.bme.aut.android.costofliving.fragments.NewExpenseItemDialogFragment
import hu.bme.aut.android.expenselist.databinding.ActivityExpenseBinding
import kotlin.concurrent.thread


class ExpenseActivity : AppCompatActivity(), ExpenseAdapter.ExpenseItemClickListener,
    NewExpenseItemDialogFragment.NewExpenseItemDialogListener,
    DatePickerDialogFragment.DatePickerDialogListener {

    private lateinit var binding: ActivityExpenseBinding
    private lateinit var database: ExpenseListDatabase
    private lateinit var adapter: ExpenseAdapter
    private lateinit var expenseItems: List<ExpenseItem>
    private lateinit var user: String
    private var totalCost: Float = 0.0f
    private var totalExpenses: Float = 0.0f
    private var totalIncomes: Float = 0.0f
    private var sharedAppear: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        user = intent.getStringExtra("username") ?: ""
        database = ExpenseListDatabase.getDatabase(applicationContext)
        binding.fab.setOnClickListener {
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
            expenseItems = database.expenseItemDao().getAll(user)
            runOnUiThread {
                adapter.update(expenseItems)
            }
        }
    }

    private fun loadSharedExpenses() {
        thread {
            expenseItems = database.expenseItemDao().getSharedExpenses()
            runOnUiThread {
                adapter.update(expenseItems)
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
        thread {
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

    override fun onDatePicked(queryParams: MutableList<String>) {
        if (queryParams[queryParams.size-1] == "true")
            loadYearlyItems(queryParams[0].toInt())
        else
            loadMonthlyItems(queryParams[0].toInt(), queryParams[1].toInt())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(hu.bme.aut.android.expenselist.R.menu.expense_menu_toolbar, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(hu.bme.aut.android.expenselist.R.id.action_get_shared).isVisible =
            !sharedAppear
        menu.findItem(hu.bme.aut.android.expenselist.R.id.action_uncheck_shared).isVisible =
            sharedAppear
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        sharedAppear = false
        return when (item.itemId) {
            hu.bme.aut.android.expenselist.R.id.action_statistics -> {
                DatePickerDialogFragment().show(
                    supportFragmentManager,
                    DatePickerDialogFragment.TAG
                )
                true
            }
            hu.bme.aut.android.expenselist.R.id.action_get_shared -> {
                loadSharedExpenses()
                sharedAppear = true
                true
            }
            hu.bme.aut.android.expenselist.R.id.action_uncheck_shared -> {
                uncheckAll()
                true
            }
            hu.bme.aut.android.expenselist.R.id.action_list_expenses -> {
                loadItemsInBackground()
                true
            }
            hu.bme.aut.android.expenselist.R.id.action_log_out -> {
                val profileIntent = Intent(this, LoginActivity::class.java)
                profileIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(profileIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initializeGraph(dateType: String) {
        createGraphSP()
        val profileIntent = Intent(this, GraphActivity::class.java)
        profileIntent.putExtra("text", dateType)
        profileIntent.putExtra("totalCost", totalCost)
        profileIntent.putExtra("totalExpenses", totalExpenses)
        profileIntent.putExtra("totalIncomes", totalIncomes)
        startActivity(profileIntent)
    }

    private fun uncheckAll() {
        for (item in expenseItems) {
            item.isShared = false
            onItemChanged(item)
        }
        adapter.update(expenseItems)
    }

    private fun loadYearlyItems(year: Int) {
        thread {
            expenseItems = database.expenseItemDao().getYearlyExpenses(
                user,
                year
            )
            /*runOnUiThread {
                adapter.update(expenseItems)
            }*/
            initializeGraph("Yearly expenses")
        }
    }

    private fun loadMonthlyItems(year: Int, month: Int) {
        thread {
            expenseItems = database.expenseItemDao().getMonthlyExpenses(
                user,
                year,
                month
            )
            /*runOnUiThread {
                adapter.update(expenseItems)
            }*/
            initializeGraph("Monthly expenses")
        }
    }

    private fun createGraphSP() {
        val categories = getCategories(user)
        val graphSP = this.getSharedPreferences("GRAPH", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = graphSP.edit()
        editor.clear()
        editor.apply()
        totalCost = 0.0f
        totalExpenses = 0.0f
        totalIncomes = 0.0f
        for (category in categories!!) {
            var cost = 0.0f
            for (item in expenseItems) {
                if (item.category == category) {
                    cost += item.cost
                    if (item.isExpense)
                        totalIncomes += item.cost
                    else {
                        totalExpenses += item.cost
                    }
                }
                totalCost += item.cost
            }
            editor.putFloat(category, cost)
        }
        editor.apply()
    }

    fun addNewCategory(user: String, categorySet: MutableSet<String>) {
        val categoriesSP = this.getSharedPreferences("CATEGORIES", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = categoriesSP.edit()
        editor.putStringSet(user, categorySet)
        editor.apply()
    }

    fun getCategories(user: String): MutableSet<String>? {
        val categoriesSP = this.getSharedPreferences("CATEGORIES", Context.MODE_PRIVATE)
        val defaultSet: MutableSet<String> =
            resources.getStringArray(hu.bme.aut.android.expenselist.R.array.category_items)
                .toMutableSet()
        if (categoriesSP.getStringSet(user, setOf())?.size == 0)
            return defaultSet
        else
            return categoriesSP.getStringSet(user, setOf())?.toMutableSet()
    }
}