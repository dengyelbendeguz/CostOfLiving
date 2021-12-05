package hu.bme.aut.android.costofliving

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.costofliving.adapter.LoanAdapter
import hu.bme.aut.android.costofliving.data.LoanItem
import hu.bme.aut.android.costofliving.data.LoanListDatabase
import hu.bme.aut.android.costofliving.fragments.NewLoanItemDialogFragment
import hu.bme.aut.android.expenselist.R
import hu.bme.aut.android.expenselist.databinding.ActivityLoanBinding
import kotlin.concurrent.thread

class LoanActivity: AppCompatActivity(), LoanAdapter.LoanItemClickListener,
    NewLoanItemDialogFragment.NewLoanItemDialogListener {

    private lateinit var binding: ActivityLoanBinding
    private lateinit var database: LoanListDatabase
    private lateinit var adapter: LoanAdapter
    private lateinit var user: String
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        user = intent.getStringExtra("username") ?: ""
        database = LoanListDatabase.getDatabase(applicationContext)
        binding.fabLoan.setOnClickListener{
            NewLoanItemDialogFragment(user).show(
                supportFragmentManager,
                NewLoanItemDialogFragment.TAG
            )
        }
        initRecyclerView()
    }
    private fun initRecyclerView() {
        adapter = LoanAdapter(this)
        binding.rvLoan.layoutManager = LinearLayoutManager(this)
        binding.rvLoan.adapter = adapter
        loadItemsInBackground()
    }

    private fun loadItemsInBackground() {
        thread {
            val items = database.loanItemDao().getAll(user)
            runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onItemChanged(item: LoanItem) {
        thread {
            database.loanItemDao().update(item)
            Log.d("LoanActivity", "LoanItem update was successful")
        }
    }

    override fun onItemDeleted(item: LoanItem) {
        thread{
            database.loanItemDao().deleteItem(item)
            Log.d("LoanActivity", "LoanItem delete was successful")
            loadItemsInBackground()
        }
    }

    override fun onLoanItemCreated(newItem: LoanItem) {
        thread {
            database.loanItemDao().insert(newItem)

            runOnUiThread {
                adapter.addItem(newItem)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.loan_menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_people_i_owe -> {
                thread {
                    val items = database.loanItemDao().getLoaners(user)
                    runOnUiThread {
                        adapter.update(items)
                    }
                }
                true
            }
            R.id.action_people_owe_me -> {
                thread {
                    val items = database.loanItemDao().getOwers(user)
                    runOnUiThread {
                        adapter.update(items)
                    }
                }
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
}