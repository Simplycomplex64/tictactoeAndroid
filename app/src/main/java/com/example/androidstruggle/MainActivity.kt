package com.example.androidstruggle

import PlayerInfo
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.androidstruggle.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val addNewPlayerLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent = result.data ?: return@registerForActivityResult
                val newPlayerName = data.getStringExtra("newPlayerName")
                if (!newPlayerName.isNullOrBlank()) {
                    // Handle the new player name and update the Spinner here
                    initSpinner(binding.spinner1, newPlayerName, 1)
                }
            }
        }

    private val gameActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val player1Score = result.data?.getIntExtra("player1Score", 0)
                val player2Score = result.data?.getIntExtra("player2Score", 0)

                // Update the scores in your spinners or wherever they are displayed
                // For example, you can update the scores in your spinners.
                updateSpinnerScores(player1Score, player2Score)
            }
        }

    private fun updateSpinnerScores(player1Score: Int?, player2Score: Int?) {
        val playerInfoList = mutableListOf<PlayerInfo>()

        // Fetch existing player names from SharedPreferences
        val savedPlayerNames = sharedPreferences.getStringSet("playerNames", null)
        if (savedPlayerNames != null) {
            for (name in savedPlayerNames) {
                val score = sharedPreferences.getInt("${name}_score", 0)
                playerInfoList.add(PlayerInfo(name, score))
            }
        }

        // Find the index of the player whose score needs to be updated
        val player1Index = playerInfoList.indexOfFirst {
            val player1Name = null
            it.name == player1Name
        }
        val player2Index = playerInfoList.indexOfFirst {
            val player2Name = null
            it.name == player2Name
        }

        // Update the scores
        if (player1Index >= 0 && player1Score != null) {
            playerInfoList[player1Index].score = player1Score
        }
        if (player2Index >= 0 && player2Score != null) {
            playerInfoList[player2Index].score = player2Score
        }

        // Create a custom adapter to display player names and scores
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, playerInfoList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set the adapter for the spinners
        binding.spinner1.adapter = adapter
        binding.spinner2.adapter = adapter
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        sharedPreferences = getSharedPreferences("PlayerData", MODE_PRIVATE)

        // Load player data from SharedPreferences
        val player1Name = sharedPreferences.getString("playerName1", "")
        val player2Name = sharedPreferences.getString("playerName2", "")

        // Initialize spinner1 with player 1's data
        if (player1Name != null) {
            initSpinner(binding.spinner1, player1Name,  1)
        }

        // Initialize spinner2 with player 2's data
        val nextPlayer = getNextPlayer(player1Name, player2Name)
        initSpinner(binding.spinner2, nextPlayer, 2)

        binding.addNewPlayer.setOnClickListener {
            navigateToAddNewPlayer()
        }
    }

    private fun getNextPlayer(currentPlayerName: String?, otherPlayerName: String?): String {
        // Fetch existing player names and scores from SharedPreferences
        val savedPlayerNames = sharedPreferences.getStringSet("playerNames", null)
        if (savedPlayerNames != null) {
            val players = savedPlayerNames.toList()
            if (players.size > 2) {
                for (i in players.indices) {
                    if (players[i] == currentPlayerName) {
                        return if (i < players.size - 1) players[i + 1] else players[0]
                    }
                }
            }
        }
        return ""
    }

    private fun navigateToAddNewPlayer() {
        val intent = Intent(this, AddNewPlayer::class.java)
        addNewPlayerLauncher.launch(intent)
    }

    private fun initSpinner(spinner: Spinner, nextPlayer: String, playerNumber: Int) {
        val playerInfoList = mutableListOf<PlayerInfo>()

        // Fetch existing player names and scores from SharedPreferences
        val savedPlayerNames = sharedPreferences.getStringSet("playerNames", null)
        if (savedPlayerNames != null) {
            for (name in savedPlayerNames) {
                val score = sharedPreferences.getInt("${name}_score", 0)
                playerInfoList.add(PlayerInfo(name, score))
            }
        }

        // Create a custom adapter to display player names and scores
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, playerInfoList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set a custom view for the adapter to display both name and score
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set the adapter for the spinner
        spinner.adapter = adapter

        // Set the selection based on player number
        if (playerNumber <= playerInfoList.size) {
            spinner.setSelection(playerNumber - 1)
        }

        binding.startGameBtn.setOnClickListener {

            fun showToast(message: String) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }

            fun startGame() {
                val selectedPlayer = binding.spinner1.selectedItem.toString()
                val selectedPlayer2 = binding.spinner2.selectedItem.toString()

                val intent = Intent(this, GameInterface::class.java)
                intent.putExtra("selectedPlayer1", selectedPlayer) // Corrected the key
                intent.putExtra("selectedPlayer2", selectedPlayer2) // Corrected the key
                startActivity(intent)
            }

            val player1Name = binding.spinner1.selectedItem.toString()
            val player2Name = binding.spinner2.selectedItem.toString()

            if (player1Name.isEmpty() || player2Name.isEmpty()) {
                // Display a Toast message if a player name is empty
                showToast("Player name cannot be empty.")
            } else if (player1Name == player2Name) {
                // Display a Toast message if both player names are the same
                showToast("Player names must be different.")
            } else {
                // Start the game if everything is valid
                startGame()
            }
        }
    }
}
