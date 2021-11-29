package hu.bme.aut.android.costofliving

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.costofliving.adapter.ExpenseAdapter
import hu.bme.aut.android.costofliving.data.ExpenseItem
import hu.bme.aut.android.costofliving.data.ExpenseListDatabase
import hu.bme.aut.android.expenselist.databinding.ActivityMainBinding
import hu.bme.aut.android.costofliving.fragments.NewExpenseItemDialogFragment
import hu.bme.aut.android.expenselist.R
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), ExpenseAdapter.ExpenseItemClickListener,
    NewExpenseItemDialogFragment.NewExpenseItemDialogListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: ExpenseListDatabase
    private lateinit var adapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        //TODO: cseréld vissza az inetnre, ha már működik
        /*val user = this.intent.getStringExtra("username")
        Toast.makeText(this, "username: "+user, Toast.LENGTH_LONG).show()*/
        val user = "test_user"

        database = ExpenseListDatabase.getDatabase(applicationContext)

        binding.fab.setOnClickListener{
            NewExpenseItemDialogFragment().show(
                supportFragmentManager,
                NewExpenseItemDialogFragment.TAG
            )
        }


        initRecyclerView()
    }
    private fun initRecyclerView() {
        adapter = ExpenseAdapter(this)
        binding.rvMain.layoutManager = LinearLayoutManager(this)
        binding.rvMain.adapter = adapter
        loadItemsInBackground()
    }

    private fun loadItemsInBackground() {
        thread {
            val items = database.expenseItemDao().getAll()
            runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onItemChanged(item: ExpenseItem) {
        thread {
            database.expenseItemDao().update(item)
            Log.d("MainActivity", "ExpenseItem update was successful")
        }
    }

    override fun onItemDeleted(item: ExpenseItem) {
        thread{
            database.expenseItemDao().deleteItem(item)
            Log.d("MainActivity", "ExpenseItem delete was successful")
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
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            /*R.id.action_monthly_graph -> {
                Toast.makeText(applicationContext, "click on first", Toast.LENGTH_LONG).show()
                true
            }*/
            R.id.action_log_out -> {
                finish()
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