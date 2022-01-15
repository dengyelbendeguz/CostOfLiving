package hu.bme.aut.android.costofliving

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hu.bme.aut.android.costofliving.file.FileUtil
import hu.bme.aut.android.expenselist.R
import hu.bme.aut.android.expenselist.databinding.ActivityListBinding

class ListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListBinding
    private lateinit var fileUtil: FileUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        fileUtil = FileUtil()

        //DO NOT DELETE THE COMMENT  BELOW! (FOR TEST USE)
        //val username = "test_user"
        val username = intent.getStringExtra("username") ?: ""

        binding.btnExpense.setOnClickListener {
            val profileIntent = Intent(this, ExpenseActivity::class.java)
            profileIntent.putExtra("username", username)
            startActivity(profileIntent)
        }

        binding.btnLoan.setOnClickListener {
            val profileIntent = Intent(this, LoanActivity::class.java)
            profileIntent.putExtra("username", username)
            startActivity(profileIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.backup_menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.export_dbs -> {
                //exportDatabaseToCSVFile("expenses.csv")
                //exportDatabaseToCSVFile("loans.csv")
                true
            }
            R.id.import_dbs -> {
                true
            }
            R.id.auto_backup -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /*private fun exportDatabaseToCSVFile(fileName: String) {
        val csvFile = fileUtil.generateFile(this, fileName)
        if (csvFile != null) {
            if (MOVIES_SHOWN) {
                (shownFragment as MoviesListFragment).exportMoviesWithDirectorsToCSVFile(csvFile)
            } else {
                (shownFragment as DirectorsListFragment).exportDirectorsToCSVFile(csvFile)
            }

            Toast.makeText(this, getString(R.string.csv_file_generated_text), Toast.LENGTH_LONG).show()
            val intent = fileUtil.goToFileIntent(this, csvFile)
            startActivity(intent)
        } else {
            Toast.makeText(this, getString(R.string.csv_file_not_generated_text), Toast.LENGTH_LONG).show()
        }
    }*/
}