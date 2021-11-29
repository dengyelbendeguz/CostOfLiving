package hu.bme.aut.android.costofliving

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import hu.bme.aut.android.expenselist.R
import hu.bme.aut.android.expenselist.databinding.ActivityLoginBinding
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

//class LoginActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//    }
//}
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var username = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val credentialsSharedPreferences = this.getSharedPreferences("credentials.txt", Context.MODE_PRIVATE)
//        val editor:SharedPreferences.Editor =  credentialsSharedPreferences.edit()
//        editor.clear()
//        editor.apply()

        //LOGIN:
        binding.loginBT.setOnClickListener {
//            val myIntent: Intent = Intent()
//            myIntent.setClass(this@LoginActivity, MainActivity::class.java)
//            startActivity(myIntent)

            val profileIntent = Intent(this, MainActivity::class.java)
            startActivity(profileIntent)




            //TODO: DEBUG IT!!!
            /*if (binding.userET.text.toString().isEmpty() || binding.passET.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.warn_message, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else{
                username = binding.userET.text.toString()
                password = binding.passET.text.toString()
                //TODO: check for password hash
                /*val hashPass = checkForUser(username, credentialsSharedPreferences)
                val inputHashPass = hashPassword(password)
                if (hashPass == inputHashPass){
                    Toast.makeText(this, "jรณ: $hashPass  vs $inputHashPass", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }*/
                // Hash a given password using a given number of salt round.
                val hashPass = checkForUser(username, credentialsSharedPreferences)

                // Verify a given password matches a previously hashed password
                if (Bcrypt.verify(password, hashPass.toByteArray())) {
                    println("It's a match!")
                }
                else{
                    println("not a match")
                }

                /*val editor:SharedPreferences.Editor =  credentialsSharedPreferences.edit()
                editor.putString("username",username)
                editor.putString("password",password)
                editor.apply()*/
            }*/
        }

        //REGISTER:
        /*binding.registerBT.setOnClickListener {
            if (binding.userRegisterET.text.toString().isEmpty() || binding.passRegisterET.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.warn_message, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else{
                username = binding.userRegisterET.text.toString()
                password = binding.passRegisterET.text.toString()

                //checks for existing username
                val tmpHashPass = checkForUser(username, credentialsSharedPreferences)
                if (tmpHashPass != ""){
                    Toast.makeText(this, R.string.user_exists_message, Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                else{
                    //val hashedPassword = hashPassword(password)
                    // Hash a given password using a given number of salt round.
                    val hashedPassword = Bcrypt.hash(password, 3)

                    val editor:SharedPreferences.Editor = credentialsSharedPreferences.edit()
                    editor.putString(username, hashedPassword.toString())
                    editor.apply()
                    Toast.makeText(this, R.string.successful_registration_message, Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }
        }*/
    }

    private fun hashPassword(password: String): String{
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt, 65536, 128)
        val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val hash = factory.generateSecret(spec).encoded
        return hash.toString()
    }

    private fun checkForUser(username: String, sp: SharedPreferences): String{
        val keys: Map<String, *> = sp.all
        var hashedPassword = ""
        for ((key, value) in keys) {
            if(key == username){
                hashedPassword = value.toString()
            }
            Log.d("map values", key.toString() + ": " + value.toString())
        }
        return hashedPassword
    }
}