package com.app.matchinggame

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainMenuActivity : AppCompatActivity() {

    private lateinit var playButton: Button
    private lateinit var optionsButton: Button
    private lateinit var leaderboardButton: Button
    private lateinit var logoutButton: Button
    private lateinit var exitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        playButton = findViewById(R.id.playButton)
        optionsButton = findViewById(R.id.optionsButton)
        leaderboardButton = findViewById(R.id.leaderboardButton)
        logoutButton = findViewById(R.id.logoutButton)
        exitButton = findViewById(R.id.exitButton)

        playButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        optionsButton.setOnClickListener {
            val intent = Intent(this, OptionsActivity::class.java)
            startActivity(intent)
        }

        leaderboardButton.setOnClickListener {
            val intent = Intent(this, LeaderboardActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            SharedPrefManager(this).logout()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        exitButton.setOnClickListener {
            finishAffinity() // closes the app completely
        }
    }
}
