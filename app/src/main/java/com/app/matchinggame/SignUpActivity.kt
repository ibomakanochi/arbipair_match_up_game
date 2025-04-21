package com.app.matchinggame

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPrefManager
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        sharedPref = SharedPrefManager(this)

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        signupButton = findViewById(R.id.signupButton)

        signupButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else if (sharedPref.isUserRegistered(username)) {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
            } else {
                val success = sharedPref.registerUser(username, password)
                Toast.makeText(this, "Register success: $success", Toast.LENGTH_SHORT).show()
                if (success) {
                    sharedPref.saveUsername(username) // log them in directly
                    Toast.makeText(this, "Sign-up successful!", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this, MainMenuActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error saving account", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}
