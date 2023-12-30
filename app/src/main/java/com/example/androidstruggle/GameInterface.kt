package com.example.androidstruggle

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidstruggle.databinding.InterfaceGameBinding

class GameInterface : AppCompatActivity() {
    private lateinit var binding: InterfaceGameBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var gridTextViews: Array<Array<TextView>>

    // Initialize the board with empty cells (0 represents empty, 1 for X, 2 for O)
    private val board = Array(3) { IntArray(3) }
    private var currentPlayer = 1 // Player 1 (X) starts
    private var gameState = "ongoing"
    private var player1Name = ""
    private var player2Name = ""
    private var player1Score: Int = 0
    private var player2Score: Int = 0


    private val playerScores = mutableMapOf<String, Int>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = InterfaceGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedPreferences = getSharedPreferences("GameScores", Context.MODE_PRIVATE)

        // Receive data from the Intent
        player1Name = intent.getStringExtra("selectedPlayer1") ?: "Player 1"
        player2Name = intent.getStringExtra("selectedPlayer2") ?: "Player 2"

        player1Score = sharedPreferences.getInt(player1Name, 0)
        player2Score = sharedPreferences.getInt(player2Name, 0)

        playerScores[player1Name] = player1Score
        playerScores[player2Name] = player2Score

        // Update the TextViews with the selected player information using View Binding
        binding.player1ScoreLabel.text = "$player1Name - Score: ${player1Score} - You are: X"
        binding.player2ScoreLabel.text = "$player2Name - Score: ${player2Score} - You are: O"

        // At the beginning of your onCreate function
        currentPlayer = 1
        binding.textView6.text = "${player1Name}'s turn"

        binding.mainMenuBtn.setOnClickListener {
            navigateToMainMenu()
        }

        binding.resetBoardBtn.setOnClickListener {
            resetBoard()
        }

        sharedPreferences = getSharedPreferences("GameScores", Context.MODE_PRIVATE)

        // Load player scores from shared preferences
        for (playerName in playerScores.keys) {
            playerScores[playerName] = sharedPreferences.getInt(playerName, 0)
        }

        // Initialize your grid TextViews
        gridTextViews = Array(3) { row ->
            Array(3) { col ->
                when (row * 3 + col) {
                    0 -> binding.textView
                    1 -> binding.textView2
                    2 -> binding.textViewNo3
                    3 -> binding.textView4
                    4 -> binding.textView5
                    5 -> binding.textView12
                    6 -> binding.textView13
                    7 -> binding.textView8
                    else -> binding.textView3
                }
            }
        }

        // Set click listeners for the grid cells
        for (row in 0 until 3) {
            for (col in 0 until 3) {
                val textView = gridTextViews[row][col]
                textView.setOnClickListener {
                    if (gameState == "ongoing" && board[row][col] == 0) {
                        // Update the board and cell text
                        board[row][col] = currentPlayer
                        textView.text = if (currentPlayer == 1) "X" else "O"

                        // Check for a win or draw
                        if (checkWin(row, col, currentPlayer)) {
                            gameState = if (currentPlayer == 1) "Player 1 wins" else "Player 2 wins"
                            // Update the score and display it
                            updateScore()
                        } else if (isBoardFull()) {
                            gameState = "Draw"
                            // Handle a draw scenario
                            updateScore()
                        } else {
                            currentPlayer = 3 - currentPlayer // Switch player
                            // Update the player turn text
                            val currentPlayerName = if (currentPlayer == 1) player1Name else player2Name
                            binding.textView6.text = "$currentPlayerName's turn"
                        }
                    }
                }
            }
        }
    }

    // Check if a player has won
    private fun checkWin(row: Int, col: Int, player: Int): Boolean {
        // Check row, column, and diagonals for a win
        return (board[row][0] == player && board[row][1] == player && board[row][2] == player) ||
                (board[0][col] == player && board[1][col] == player && board[2][col] == player) ||
                (row == col && board[0][0] == player && board[1][1] == player && board[2][2] == player) ||
                (row + col == 2 && board[0][2] == player && board[1][1] == player && board[2][0] == player)
    }

    // Check if the board is full (a draw)
    private fun isBoardFull(): Boolean {
        for (row in 0 until 3) {
            for (col in 0 until 3) {
                if (board[row][col] == 0) {
                    return false
                }
            }
        }
        return true
    }

    // Update the player scores and display the result
    @SuppressLint("SetTextI18n")
    private fun updateScore() {
        when (gameState) {
            "Player 1 wins" -> {
                val playerName = player1Name
                playerScores[playerName] = playerScores[playerName]?.plus(1) ?: 1
                // Update the player score label based on the player name
                updatePlayerScoreLabel(playerName)
                binding.textView6.text = "$playerName wins"
            }
            "Player 2 wins" -> {
                val playerName = player2Name
                playerScores[playerName] = playerScores[playerName]?.plus(1) ?: 1
                // Update the player score label based on the player name
                updatePlayerScoreLabel(playerName)
                binding.textView6.text = "$playerName wins"
            }
            "Draw" -> {
                // Switch players after a draw
                switchPlayers()
                binding.textView6.text = "It's a draw"
            }
        }

        updatePlayerScores()
        savePlayerScores()
    }



    @SuppressLint("SetTextI18n")
    private fun switchPlayers() {
        val currentPlayerName = if (currentPlayer == 1) player1Name else player2Name
        binding.textView6.text = "$currentPlayerName's turn"
    }

    private fun navigateToMainMenu() {
        // Store the current player in SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putInt("currentPlayer", currentPlayer)
        editor.apply()

        val mainMenuIntent = Intent(this, MainActivity::class.java)
        startActivity(mainMenuIntent)
    }


    // Function to reset the Tic Tac Toe board
    @SuppressLint("SetTextI18n")
    private fun resetBoard() {
        for (row in 0 until 3) {
            for (col in 0 until 3) {
                board[row][col] = 0 // Reset the board to empty cells
                val textView = gridTextViews[row][col]
                textView.text = "" // Clear the text in the TextView cells
            }
        }

        if (gameState == "Player 1 wins" || gameState == "Draw") {
            // If Player 1 won the previous game or it was a draw, Player 1 starts
            currentPlayer = 1
            binding.textView6.text = "$player1Name's turn"
        } else {
            // If Player 2 won the previous game, Player 2 starts
            currentPlayer = 2
            binding.textView6.text = "$player2Name's turn"
        }

        gameState = "ongoing" // Reset the game state

        updatePlayerScores()
    }



    @SuppressLint("SetTextI18n")
    private fun updatePlayerScoreLabel(playerName: String) {
        if (playerName == player1Name) {
            binding.scoreLabelDisplay.text = "X score : ${playerScores[playerName]}"
        } else if (playerName == player2Name) {
            binding.scoreLabelDisplay2.text = "O score : ${playerScores[playerName]}"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updatePlayerScores() {
        binding.player1ScoreLabel.text = "$player1Name - Score: ${playerScores[player1Name]} - You are: X"
        binding.player2ScoreLabel.text = "$player2Name - Score: ${playerScores[player2Name]} - You are: O"
    }

    private fun savePlayerScores() {
        val editor = sharedPreferences.edit()
        for ((playerName, score) in playerScores) {
            editor.putInt(playerName, score)
        }
        editor.apply()
    }
}
