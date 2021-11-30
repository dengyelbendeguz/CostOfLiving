package hu.bme.aut.android.costofliving

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.bme.aut.android.expenselist.databinding.ActivityListBinding

class ListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TODO: uncommetn it to us login activity + manifest change launcher activity
        val username = "test_user"
        //val username = intent.getStringExtra("username") ?: ""

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
}