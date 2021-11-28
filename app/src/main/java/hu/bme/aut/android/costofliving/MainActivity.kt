package hu.bme.aut.android.costofliving

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.costofliving.adapter.ExpenseAdapter
import hu.bme.aut.android.costofliving.data.ExpenseItem
import hu.bme.aut.android.costofliving.data.ExpenseListDatabase
import hu.bme.aut.android.expenselist.databinding.ActivityMainBinding
import hu.bme.aut.android.costofliving.fragments.NewExpenseItemDialogFragment
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
}