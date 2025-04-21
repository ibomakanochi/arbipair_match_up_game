package com.app.matchinggame

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPrefManager
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPref = SharedPrefManager(this)

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signupButton = findViewById(R.id.signupButton)

        // Auto-login if already logged in
        if (sharedPref.isLoggedIn()) {
            navigateToMainMenu()
        }

        loginButton.setOnClickListener {
            val username = usernameEditText.text?.toString()?.trim() ?: ""
            val password = passwordEditText.text?.toString()?.trim() ?: ""

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (sharedPref.isLoginValid(username, password)) {
                sharedPref.saveUsername(username) // Save current session
                val savedUsername = sharedPref.getUsername()
                Toast.makeText(this, "Welcome, $savedUsername!", Toast.LENGTH_SHORT).show()
                navigateToMainMenu()
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }

            // for debug
            Log.d("LoginDebug", "Entered username: $username, password: $password")
            Log.d("LoginDebug", "Is user registered: ${sharedPref.isUserRegistered(username)}")
            Log.d("LoginDebug", "Login valid: ${sharedPref.isLoginValid(username, password)}")

        }

        signupButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun navigateToMainMenu() {
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
        finish()
    }
}
