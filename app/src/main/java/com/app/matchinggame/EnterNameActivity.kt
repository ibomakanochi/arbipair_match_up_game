package com.app.matchinggame

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.app.AlertDialog
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Color

class EnterNameActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var scoreText: TextView
    private lateinit var submitButton: Button
    private var finalScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_name)

        nameInput = findViewById(R.id.nameInput)
        scoreText = findViewById(R.id.scoreText)
        submitButton = findViewById(R.id.submitButton)

        finalScore = intent.getIntExtra("SCORE", 0)
        scoreText.text = "Your Score: $finalScore"

        submitButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            if (name.isNotEmpty()) {
                saveScoreToLeaderboard(name, finalScore)
                showPostSubmissionDialog()
            } else {
                Toast.makeText(this, "Please enter your name!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveScoreToLeaderboard(name: String, score: Int) {
        // Save the name and score to SharedPreferences or a database
        val sharedPref = SharedPrefManager(this)

        val dateTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date())

        sharedPref.saveNewScore(name, score, dateTime)
    }

    private fun showPostSubmissionDialog() {
        val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle("What would you like to do next?")
            .setMessage("Your score has been recorded!")
            .setPositiveButton("Play Again") { dialogInterface, _ ->
                restartGame()
                dialogInterface.dismiss()
            }
            .setNegativeButton("View Leaderboard") { dialogInterface, _ ->
                val intent = Intent(this, LeaderboardActivity::class.java)
                startActivity(intent)
                dialogInterface.dismiss()
            }
            .setNeutralButton("Main Menu") { dialogInterface, _ ->
                navigateToMainMenu()
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()

        val buttonTextColor = Color.parseColor("#1B5E20")

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(buttonTextColor)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(buttonTextColor)
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL)?.setTextColor(buttonTextColor)
    }

    private fun restartGame() {
        // Code to restart the game
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToMainMenu() {
        // Code to navigate to the main menu
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
        finish()
    }
}

