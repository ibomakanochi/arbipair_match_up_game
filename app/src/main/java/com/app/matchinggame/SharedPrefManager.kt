package com.app.matchinggame

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPrefManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // ----- MULTI-USER SUPPORT -----
    fun registerUser(username: String, password: String): Boolean {
        if (isUserRegistered(username)) return false // prevent duplicate usernames

        val originalSet = prefs.getStringSet("registered_users", emptySet()) ?: emptySet()
        val users = HashSet(originalSet) // clone to avoid SharedPreferences bugs
        users.add(username)
        prefs.edit().putStringSet("registered_users", users).apply()
        prefs.edit().putString("password_$username", password).apply()

        // for debug
        Log.d("SharedPref", "Registered users: $users")
        Log.d("SharedPref", "Saved password for $username: $password")
        Log.d("SharedPref", "All saved users: ${prefs.getStringSet("registered_users", emptySet())}")

        return true
    }

    fun isUserRegistered(username: String): Boolean {
        val users = prefs.getStringSet("registered_users", emptySet())
        return users?.contains(username) ?: false
    }

    fun isLoginValid(username: String, password: String): Boolean {
        val storedPassword = prefs.getString("password_$username", null)
        Log.d("SharedPref", "Login attempt for $username, input: $password, stored: $storedPassword")
        return isUserRegistered(username) && storedPassword != null && storedPassword == password

    }

    // ----- USERNAME -----
    fun saveUsername(username: String) {
        prefs.edit().putString("username", username).apply()
    }

    fun getUsername(): String? {
        return prefs.getString("username", "") ?: ""
    }

    // ----- LOGIN STATUS -----
    fun isLoggedIn(): Boolean {
        return prefs.contains("username")
    }

    fun logout() {
        prefs.edit().remove("username").apply()
    }

    // ----- DIFFICULTY LEVEL -----
    fun saveDifficulty(level: String) {
        prefs.edit().putString("difficulty", level).apply()
    }

    fun getDifficultyLevel(): String {
        return prefs.getString("difficulty", "easy") ?: "easy"
    }

    // ----- BEST SCORE -----
    fun saveBestScore(score: Int, difficulty: String) {
        prefs.edit().putInt("best_score_$difficulty", score).apply()
    }

    fun getBestScore(difficulty: String): Int {
        return prefs.getInt("best_score_$difficulty", 0)
    }

    // ----- LEADERBOARD -----
    fun saveNewScore(name: String, score: Int, dateTime: String) {
        val leaderboardPrefs = context.getSharedPreferences("leaderboard", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = leaderboardPrefs.getString("scores", null)
        val type = object : TypeToken<MutableList<ScoreModel>>() {}.type
        val scoreList: MutableList<ScoreModel> = gson.fromJson(json, type) ?: mutableListOf()

        scoreList.add(ScoreModel(name, score, dateTime))

        // Sort in descending order by score
        scoreList.sortByDescending { it.score }

        val updatedJson = gson.toJson(scoreList)
        leaderboardPrefs.edit().putString("scores", updatedJson).apply()
    }

    fun getTopScores(): List<ScoreModel> {
        val leaderboardPrefs = context.getSharedPreferences("leaderboard", Context.MODE_PRIVATE)
        val json = leaderboardPrefs.getString("scores", null)
        return if (json != null) {
            val type = object : TypeToken<List<ScoreModel>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptyList()
        }
    }

}
