package com.app.matchinggame

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.CountDownTimer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

private var isProcessingTurn = false

class GameActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPrefManager
    private lateinit var difficultyText: TextView
    private lateinit var cardRecyclerView: RecyclerView

    private lateinit var cards: List<MemoryCard>
    private lateinit var adapter: CardAdapter
    private var indexOfSingleSelectedCard: Int? = null

    private lateinit var pauseButton: Button
    private lateinit var timerTextView: TextView
    private var isGamePaused = false
    private lateinit var gameTimer: CountDownTimer
    private var remainingTime: Long = 0
    private var score = 0
    private lateinit var scoreTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        sharedPref = SharedPrefManager(this)
        difficultyText = findViewById(R.id.difficultyText)
        val difficultyLevel = sharedPref.getDifficultyLevel()
        val difficultyLabel = when (difficultyLevel.lowercase()) {
            "easy" -> "Easy"
            "medium" -> "Medium"
            "hard" -> "Hard"
            else -> "Easy"
        }
        difficultyText.text = "Difficulty: $difficultyLabel"

        cardRecyclerView = findViewById(R.id.cardRecyclerView)

        val columns = when (difficultyLevel.lowercase()) {
            "easy" -> 3
            "medium" -> 4
            "hard" -> 4
            else -> 3
        }

        cardRecyclerView.layoutManager = GridLayoutManager(this, columns)

        // Initialize the Pause Button here inside the onCreate method
        pauseButton = findViewById(R.id.btnPause)
        timerTextView = findViewById(R.id.timerTextView)

        scoreTextView = findViewById(R.id.scoreTextView)
        scoreTextView.text = "Score: $score"


        // Set the click listener for the Pause button
        pauseButton.setOnClickListener {
            if (isGamePaused) {
                resumeGame()
            } else {
                pauseGame()
            }
        }

        // Initialize the game logic here (cards, timer, etc.)
        setupGame()
    }

    private fun onCardClicked(position: Int) {
        if (isGamePaused || isProcessingTurn) return

        val card = cards[position]
        if (card.isFaceUp || card.isMatched) return

        card.isFaceUp = true
        adapter.notifyItemChanged(position)

        if (indexOfSingleSelectedCard == null) {
            indexOfSingleSelectedCard = position
        } else {
            isProcessingTurn = true
            val matched = cards[position].identifier == cards[indexOfSingleSelectedCard!!].identifier

            if (matched) {
                cards[position].isMatched = true
                cards[indexOfSingleSelectedCard!!].isMatched = true
                score += 10
                updateScoreUI()
                indexOfSingleSelectedCard = null
                isProcessingTurn = false

                if (cards.all { it.isMatched }) {
                    gameTimer.cancel()
                    showWinDialog()
                }
            } else {
                score -= 2
                if (score < 0) score = 0
                updateScoreUI()

                // Delay to show both cards before flipping back
                val prevIndex = indexOfSingleSelectedCard!!
                indexOfSingleSelectedCard = null
                cardRecyclerView.postDelayed({
                    cards[prevIndex].isFaceUp = false
                    cards[position].isFaceUp = false
                    adapter.notifyItemChanged(prevIndex)
                    adapter.notifyItemChanged(position)
                    isProcessingTurn = false
                }, 1000)
            }
        }
    }

    private fun updateScoreUI() {
        scoreTextView.text = "Score: $score"
    }

    private fun restoreCards() {
        for (card in cards) {
            if (!card.isMatched) {
                card.isFaceUp = false
            }
        }
    }

    private fun setupGame() {
        val difficultyLevel = sharedPref.getDifficultyLevel()
        val spanCount = when (difficultyLevel.lowercase()) {
            "easy" -> 3
            "medium" -> 4
            "hard" -> 6
            else -> 3
        }

        initializeCards(spanCount)

        indexOfSingleSelectedCard = null
        score = 0
        updateScoreUI()

        remainingTime = when (difficultyLevel) {
            "Easy" -> 60000
            "Medium" -> 120000
            "Hard" -> 180000
            else -> 60000
        }

        val layoutManager = GridLayoutManager(this, spanCount)
        cardRecyclerView.layoutManager = layoutManager

        startGameTimer()
    }

    private fun startGameTimer() {
        // Start the countdown timer
        gameTimer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                updateTimerUI()
            }

            override fun onFinish() {
                val bonus = (remainingTime / 1000).toInt()
                score += bonus
                updateScoreUI()
                gameOver()
            }
        }.start()
    }

    private fun updateTimerUI() {
        // Update the Timer TextView to show the remaining time
        val seconds = remainingTime / 1000
        val minutes = seconds / 60
        val secondsRemaining = seconds % 60
        timerTextView.text = String.format("%02d:%02d", minutes, secondsRemaining)
    }

    private fun pauseGame() {
        // Stop the game timer and set the game to paused state
        gameTimer.cancel()
        isGamePaused = true

        // Show the pause dialog with options (Resume, Restart, Main Menu)
        showPauseDialog()
    }

    private fun resumeGame() {
        // Restart the timer from the remaining time
        startGameTimer()
        isGamePaused = false
    }

    private fun showPauseDialog() {
        val pauseDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle("Game Paused")
            .setMessage("What would you like to do?")
            .setPositiveButton("Resume") { dialog, _ ->
                resumeGame()
                dialog.dismiss()
            }
            .setNegativeButton("Restart") { dialog, _ ->
                restartGame()
                dialog.dismiss()
            }
            .setNeutralButton("Main Menu") { dialog, _ ->
                navigateToMainMenu()
                dialog.dismiss()
            }
            .create()

        pauseDialog.show()
    }

    private fun restartGame() {
        gameTimer.cancel() // stop old timer if still running
        isGamePaused = false
        setupGame()
    }

    private fun navigateToMainMenu() {
        // Navigate back to the MainMenuActivity
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun gameOver() {
        // Show the Game Over dialog
        val gameOverDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle("Game Over")
            .setMessage("Time's up! Would you like to play again or go to the main menu?")
            .setPositiveButton("Play Again") { dialog, _ ->
                restartGame() // Restart the game
                dialog.dismiss()
            }
            .setNegativeButton("Main Menu") { dialog, _ ->
                navigateToMainMenu() // Navigate to the main menu
                dialog.dismiss()
            }
            .create()

        gameOverDialog.show()
    }

    private fun showWinDialog() {
        // Calculate the score
        val correctMatches = getCorrectMatches()
        val remainingSeconds = getRemainingTime()
        val calculatedScore = correctMatches * 10 + remainingSeconds * 2

        // Save the best score if it's the highest
        val difficulty = sharedPref.getDifficultyLevel()
        val bestScore = sharedPref.getBestScore(difficulty)
        if (calculatedScore > bestScore) {
            sharedPref.saveBestScore(calculatedScore, difficulty)
            showToast("Score: $calculatedScore!")
        }

        // Skip dialog and go directly to EnterNameActivity
        val intent = Intent(this, EnterNameActivity::class.java)
        intent.putExtra("SCORE", calculatedScore)
        startActivity(intent)
        finish()
    }

    private fun getCorrectMatches(): Int {
        return cards.count { it.isMatched } / 2
    }

    private fun getRemainingTime(): Int {
        return (remainingTime / 1000).toInt()
    }

    private fun initializeCards(spanCount: Int) {
        val imageResIds = listOf(
            R.drawable.imgpc,
            R.drawable.imgbug,
            R.drawable.imgerror,
            R.drawable.imgphone,
            R.drawable.imgkeyboard,
            R.drawable.imgusb,
            R.drawable.imgmouse,
            R.drawable.imgmotherboard,
            R.drawable.imgai,
            R.drawable.imgdisk,
            R.drawable.imgantivirus,
            R.drawable.imgprogramming,
            R.drawable.imgcloud,
            R.drawable.imgdatabase,
            R.drawable.imgwifi,
            R.drawable.imgprocessor,
            R.drawable.imgwww,
            R.drawable.imgdebugging
        )

        val difficultyLevel = sharedPref.getDifficultyLevel()
        val pairs = when (difficultyLevel.lowercase()) {
            "easy" -> 6
            "medium" -> 10
            "hard" -> 18
            else -> 6
        }

        val chosenImages = imageResIds.shuffled().take(pairs)
        val randomizedCards = (chosenImages + chosenImages).shuffled()

        cards = randomizedCards.map { imageResId ->
            MemoryCard(imageResId, imageResId)
        }

        adapter = CardAdapter(cards, spanCount) { position -> onCardClicked(position) }
        cardRecyclerView.adapter = adapter
    }

}
