package hu.bme.aut.android.costofliving

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hu.bme.aut.android.expenselist.R
import hu.bme.aut.android.expenselist.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var username: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val credentialsSharedPreferences = this.getSharedPreferences("credentials.txt", Context.MODE_PRIVATE)

        //LOGIN:
        binding.loginBT.setOnClickListener {
            if (binding.userET.text.toString().isEmpty() || binding.passET.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.warn_message, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else{
                username = binding.userET.text.toString()
                password = binding.passET.text.toString()
                val savedPassword = checkForUser(username, credentialsSharedPreferences)
                if(savedPassword != "" && savedPassword == password){
                    val profileIntent = Intent(this, ListActivity::class.java)
                    profileIntent.putExtra("username", username)
                    startActivity(profileIntent)
                }
                else{
                    Toast.makeText(this, R.string.wrong_credentials_message, Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                //HASHING:
                /*val savedHash = checkForUser(username, credentialsSharedPreferences)
                if (savedHash != ""){
                    if (Bcrypt.verify(password, savedHash.toByteArray())) {
                        val profileIntent = Intent(this, ExpenseActivity::class.java)
                        startActivity(profileIntent)
                    }
                }
                else{
                    Toast.makeText(this, R.string.wrong_credentials_message, Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }*/
            }
        }

        //REGISTER:
        binding.registerBT.setOnClickListener {
            if (binding.userRegisterET.text.toString().isEmpty() || binding.passRegisterET.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.warn_message, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else{
                username = binding.userRegisterET.text.toString()
                password = binding.passRegisterET.text.toString()
                val tempPassword = checkForUser(username, credentialsSharedPreferences)
                if (tempPassword != ""){
                    Toast.makeText(this, R.string.user_exists_message, Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                else{
                    val editor:SharedPreferences.Editor = credentialsSharedPreferences.edit()
                    editor.putString(username, password)
                    editor.apply()
                    Toast.makeText(this, R.string.successful_registration_message, Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                //HASHING:
                /*val tmpHashPass = checkForUser(username, credentialsSharedPreferences)
                if (tmpHashPass != ""){
                    Toast.makeText(this, R.string.user_exists_message, Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                else{
                    val hashedPassword = Bcrypt.hash(password, 4)

                    val editor:SharedPreferences.Editor = credentialsSharedPreferences.edit()
                    editor.putString(username, hashedPassword.toString())
                    editor.apply()
                    Toast.makeText(this, R.string.successful_registration_message, Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }*/
            }
        }
    }

    private fun checkForUser(username: String, sp: SharedPreferences): String{
        val keys: Map<String, *> = sp.all
        var hashedPassword = ""
        for ((key, value) in keys) {
            if(key == username){
                hashedPassword = value.toString()
            }
        }
        return hashedPassword
    }
}