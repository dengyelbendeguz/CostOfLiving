package hu.bme.aut.android.costofliving

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.raphaelebner.roomdatabasebackup.core.RoomBackup
import hu.bme.aut.android.costofliving.data.ExpenseListDatabase
import hu.bme.aut.android.costofliving.data.LoanListDatabase
import hu.bme.aut.android.expenselist.R
import hu.bme.aut.android.expenselist.databinding.ActivityListBinding
import hu.bme.aut.android.costofliving.file.FileHandler

import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.concurrent.thread


class ListActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityListBinding
    lateinit var backup: RoomBackup
    private lateinit var user: String
    private lateinit var fileHandler: FileHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        user = intent.getStringExtra("username") ?: ""
        backup = RoomBackup(this)
        fileHandler = FileHandler(applicationContext, user)

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
                fileHandler.exportToCSV()
                runOnUiThread {
                    Toast.makeText(this, R.string.export_succesfull, Toast.LENGTH_LONG).show()
                }
                true
            }
            /*R.id.backup_dbs -> {
                true
            }
            R.id.restore_dbs -> {
                true
            }*/
            else -> super.onOptionsItemSelected(item)
        }
    }



    companion object {
        const val TAG = "ListActivity"
    }
}