package hu.bme.aut.android.costofliving

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hu.bme.aut.android.costofliving.file.FileHandler
import hu.bme.aut.android.expenselist.R
import hu.bme.aut.android.expenselist.databinding.ActivityListBinding

class ListActivity: AppCompatActivity() {
    private lateinit var binding: ActivityListBinding
    private lateinit var user: String
    private lateinit var fileHandler: FileHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        //DO NOT DELETE THE COMMENT BELOW! (FOR TEST USE)
        //user = "test_user"
        //IF EMPTY "bende" IS DEFAULT!!!
        user = intent.getStringExtra("username") ?: "bende"
        fileHandler = FileHandler(applicationContext, user)

        //DAILY BACKUP
        fileHandler.prepareIO("BACKUP", "EXPENSES", user)
        fileHandler.prepareIO("BACKUP", "LOANS", user)

        binding.btnExpense.setOnClickListener {
            val profileIntent = Intent(this, ExpenseActivity::class.java)
            profileIntent.putExtra("username", user)
            startActivity(profileIntent)
        }

        binding.btnLoan.setOnClickListener {
            val profileIntent = Intent(this, LoanActivity::class.java)
            profileIntent.putExtra("username", user)
            startActivity(profileIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.backup_menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.export_expenses -> {
                fileHandler.prepareIO("EXPORT", "EXPENSES", user)
                runOnUiThread {
                    Toast.makeText(this, R.string.expenses_exported, Toast.LENGTH_LONG).show()
                }
                true
            }
            R.id.export_loans -> {
                fileHandler.prepareIO("EXPORT", "LOANS", user)
                runOnUiThread {
                    Toast.makeText(this, R.string.loans_exported, Toast.LENGTH_LONG).show()
                }
                true
            }
            R.id.import_expenses -> {
                fileHandler.prepareIO("IMPORT", "EXPENSES", user)
                runOnUiThread {
                    Toast.makeText(this, R.string.expenses_imported, Toast.LENGTH_LONG).show()
                }
                true
            }
            R.id.import_loans -> {
                fileHandler.prepareIO("IMPORT", "LOANS", user)
                runOnUiThread {
                    Toast.makeText(this, R.string.loans_imported, Toast.LENGTH_LONG).show()
                }
                true
            }
            /*R.id.add_test_item_to_db -> {
                fileHandler.addTestItem()
                runOnUiThread {
                    Toast.makeText(this, "test data added", Toast.LENGTH_LONG).show()
                }
                true
            }*/
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object
}