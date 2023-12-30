package com.example.androidstruggle

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.androidstruggle.databinding.PlayerNewAddBinding

class AddNewPlayer : AppCompatActivity() {
    private lateinit var binding: PlayerNewAddBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PlayerNewAddBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        sharedPreferences = getSharedPreferences("PlayerData", MODE_PRIVATE)

        binding.savePlayerBtn.setOnClickListener {
            savePlayer()
        }

        binding.cancelBtn.setOnClickListener {
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun savePlayer() {
        val playerName = binding.editTextField.text.toString()
        val score = 0

        // Fetch existing player names
        val playerNames = sharedPreferences.getStringSet("playerNames", HashSet())?.toMutableList() ?: mutableListOf()

        // Add the new player name
        playerNames.add(playerName)

        // Update the player names in SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putStringSet("playerNames", playerNames.toSet())
        editor.putString("playerName1", playerName)
        editor.putInt("${playerName}_score", score)
        editor.apply()

        // Return to the main activity
        navigateToMainActivity()
    }
}
