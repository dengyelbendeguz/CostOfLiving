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
import kotlin.concurrent.thread

class ExpenseActivity() : AppCompatActivity(), ExpenseAdapter.ExpenseItemClickListener,
    NewExpenseItemDialogFragment.NewExpenseItemDialogListener {

    private lateinit var binding: ActivityExpenseBinding
    private lateinit var database: ExpenseListDatabase
    private lateinit var adapter: ExpenseAdapter
    var user = ""

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
            /*R.id.action_monthly_graph -> {
                Toast.makeText(applicationContext, "click on first", Toast.LENGTH_LONG).show()
                true
            }*/
            R.id.action_log_out -> {
                val profileIntent = Intent(this, LoginActivity::class.java)
                profileIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(profileIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun addNewCategory(user: String, categorySet: MutableSet<String>){
        val categoriesSP = this.getSharedPreferences("CATEGORIES", Context.MODE_PRIVATE)
        val editor:SharedPreferences.Editor =  categoriesSP.edit()
        editor.putStringSet(user, categorySet)
        editor.apply()
    }

    fun getCategories(user: String): MutableSet<String>{
        val categoriesSP = this.getSharedPreferences("CATEGORIES", Context.MODE_PRIVATE)
        val defaultSet: MutableSet<String> =
        resources.getStringArray(hu.bme.aut.android.expenselist.R.array.category_items).toMutableSet()
        val categories: MutableSet<String> =
            categoriesSP.getStringSet(user, setOf<String>())?.toMutableSet() ?: defaultSet
        return  categories
    }
}