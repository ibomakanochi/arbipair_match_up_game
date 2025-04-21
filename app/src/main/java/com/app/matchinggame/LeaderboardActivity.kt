package com.app.matchinggame

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ScoreAdapter
    private lateinit var sharedPref: SharedPrefManager
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        sharedPref = SharedPrefManager(this)

        recyclerView = findViewById(R.id.recyclerViewScores)
        recyclerView.layoutManager = LinearLayoutManager(this)

        emptyText = findViewById(R.id.emptyLeaderboardText)

        val topScores = sharedPref.getTopScores()
        if (topScores.isEmpty()) {
            emptyText.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyText.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            adapter = ScoreAdapter(topScores)
            recyclerView.adapter = adapter
        }

        val backToMenuBtn: Button = findViewById(R.id.btnBackToMenu)
        backToMenuBtn.setOnClickListener {
            startActivity(Intent(this, MainMenuActivity::class.java))
            finish()
        }
    }
}