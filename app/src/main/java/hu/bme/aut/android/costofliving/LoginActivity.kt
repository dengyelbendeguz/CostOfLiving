package hu.bme.aut.android.costofliving

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.toxicbakery.bcrypt.Bcrypt
import hu.bme.aut.android.expenselist.R
import hu.bme.aut.android.expenselist.databinding.ActivityLoginBinding
import java.util.*


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
                val savedHash = checkForHash(username, credentialsSharedPreferences)
                if (savedHash.isNotEmpty()){
                    if (Bcrypt.verify(password, savedHash)) {
                        val profileIntent = Intent(this, ExpenseActivity::class.java)
                        profileIntent.putExtra("username", username)
                        startActivity(profileIntent)
                    }
                }
                else{
                    Toast.makeText(this, R.string.wrong_credentials_message, Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
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
                val tmpHashPass = checkForHash(username, credentialsSharedPreferences)
                if (tmpHashPass.isNotEmpty()){
                    Toast.makeText(this, R.string.user_exists_message, Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                else{
                    val hashedPassword = Bcrypt.hash(password, 4)
                    saveHash(username,
                        hashedPassword.contentToString(), credentialsSharedPreferences)
                    Toast.makeText(this, R.string.successful_registration_message, Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }
        }
    }

    private fun saveHash(username: String, hashedPassword: String, sp: SharedPreferences){
        val editor = sp.edit()
        editor.putString(username, hashedPassword)
        editor.apply()
    }

    private fun checkForHash(username: String, sp: SharedPreferences): ByteArray{
        val stringArray = sp.getString(username, null)
        var byteArray = byteArrayOf()
        if (stringArray != null) {
            val split = stringArray.substring(1, stringArray.length - 1).split(", ").toTypedArray()
            byteArray = ByteArray(split.size)
            for (i in split.indices) {
                byteArray[i] = split[i].toByte()
            }
        }
        return byteArray
    }
}