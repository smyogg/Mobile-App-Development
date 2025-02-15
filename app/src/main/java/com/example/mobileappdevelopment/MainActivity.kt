package com.example.mobileappdevelopment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                GameScreen()
            }
        }
    }
}

@Composable
fun GameScreen() {
    val gameBoard = remember { mutableStateListOf(*Array(9) { "" }) }
    val currentPlayerSymbol = remember { mutableStateOf("X") }
    val isGameActive = remember { mutableStateOf(true) }
    val winningIndices = remember { mutableStateOf<List<Int>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when {
                winningIndices.value != null -> "Player ${gameBoard[winningIndices.value!![0]]} Wins!"
                !isGameActive.value -> "Game Draw!"
                else -> "Player ${currentPlayerSymbol.value}'s Turn"
            },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Box(modifier = Modifier.size(300.dp)) {
            GameBoard(
                boardState = gameBoard,
                isGameActive = isGameActive.value,
                winningIndices = winningIndices.value
            ) { index ->
                if (isGameActive.value && gameBoard[index].isEmpty()) {
                    gameBoard[index] = currentPlayerSymbol.value
                    val winIndices = checkWinner(gameBoard)
                    if (winIndices != null) {
                        isGameActive.value = false
                        winningIndices.value = winIndices
                    } else if (isBoardFull(gameBoard)) {
                        isGameActive.value = false
                    } else {
                        currentPlayerSymbol.value = if (currentPlayerSymbol.value == "X") "O" else "X"
                    }
                }
            }
        }
        Button(
            onClick = {
                gameBoard.indices.forEach { index -> gameBoard[index] = "" }
                currentPlayerSymbol.value = "X"
                isGameActive.value = true
                winningIndices.value = null
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Reset Game", color = Color.White)
        }
    }
}

@Composable
fun GameBoard(
    boardState: List<String>,
    isGameActive: Boolean,
    winningIndices: List<Int>?,
    onCellClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.size(300.dp)
    ) {
        itemsIndexed(boardState) { index, symbol ->
            val row = index / 3
            val col = index % 3
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        if (winningIndices?.contains(index) == true) Color.Yellow.copy(alpha = 0.3f)
                        else Color.LightGray
                    )
                    .clickable { onCellClick(index) }
                    .drawGridBorders(row, col)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = symbol,
                    fontSize = 40.sp,
                    color = when (symbol) {
                        "X" -> Color(0xFF6A1B9A) // Dark Purple
                        "O" -> Color(0xFF009688)  // Teal
                        else -> Color.Transparent
                    }
                )
            }
        }
    }
}

fun Modifier.drawGridBorders(row: Int, col: Int): Modifier = this.then(
    drawWithContent {
        drawContent()

        // Draw right border if not last column
        if (col < 2) {
            drawLine(
                color = Color.DarkGray,
                start = Offset(size.width - 1f, 0f),
                end = Offset(size.width - 1f, size.height),
                strokeWidth = 4f
            )
        }

        // Draw bottom border if not last row
        if (row < 2) {
            drawLine(
                color = Color.DarkGray,
                start = Offset(0f, size.height - 1f),
                end = Offset(size.width, size.height - 1f),
                strokeWidth = 4f
            )
        }
    }
)

fun checkWinner(boardState: List<String>): List<Int>? {
    // Check rows
    for (i in 0..6 step 3) {
        if (boardState[i].isNotEmpty() && boardState[i] == boardState[i+1] && boardState[i] == boardState[i+2]) {
            return listOf(i, i+1, i+2)
        }
    }

    // Check columns
    for (i in 0..2) {
        if (boardState[i].isNotEmpty() && boardState[i] == boardState[i+3] && boardState[i] == boardState[i+6]) {
            return listOf(i, i+3, i+6)
        }
    }

    // Check diagonals
    if (boardState[0].isNotEmpty() && boardState[0] == boardState[4] && boardState[0] == boardState[8]) {
        return listOf(0, 4, 8)
    }
    if (boardState[2].isNotEmpty() && boardState[2] == boardState[4] && boardState[2] == boardState[6]) {
        return listOf(2, 4, 6)
    }

    return null
}

fun isBoardFull(boardState: List<String>): Boolean {
    return boardState.none { it.isEmpty() }
}
