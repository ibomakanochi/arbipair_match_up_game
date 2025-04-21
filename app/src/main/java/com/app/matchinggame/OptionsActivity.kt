package com.app.matchinggame

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class OptionsActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPrefManager
    private lateinit var difficultySpinner: Spinner
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        sharedPref = SharedPrefManager(this)

        difficultySpinner = findViewById(R.id.difficultySpinner)
        saveButton = findViewById(R.id.saveButton)

        // Set up the difficulty levels spinner
        val difficultyLevels = arrayOf("Easy", "Medium", "Hard")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, difficultyLevels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        difficultySpinner.adapter = adapter

        // Set the saved difficulty level
        val savedDifficulty = sharedPref.getDifficultyLevel()
        val difficultyIndex = difficultyLevels.indexOf(savedDifficulty)
        difficultySpinner.setSelection(difficultyIndex)

        saveButton.setOnClickListener {
            val selectedDifficulty = difficultySpinner.selectedItem.toString()
            sharedPref.saveDifficulty(selectedDifficulty)

            // Display confirmation
            Toast.makeText(this, "Difficulty saved: $selectedDifficulty", Toast.LENGTH_SHORT).show()
        }

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // closes OptionsActivity and returns to MainMenuActivity
        }

    }
}
