package com.example.madproject

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
    val cells = remember { mutableStateListOf(*Array(9) { "" }) }
    val currentPlayer = remember { mutableStateOf("X") }
    val gameActive = remember { mutableStateOf(true) }
    val winningLine = remember { mutableStateOf<List<Int>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when {
                winningLine.value != null -> "Player ${cells[winningLine.value!![0]]} Wins!"
                !gameActive.value -> "Game Draw!"
                else -> "Player ${currentPlayer.value}'s Turn"
            },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Box(modifier = Modifier.size(300.dp)) {
            Board(cells, gameActive.value, winningLine.value) { index ->
                if (gameActive.value && cells[index].isEmpty()) {
                    cells[index] = currentPlayer.value
                    val winIndices = checkWinner(cells)
                    if (winIndices != null) {
                        gameActive.value = false
                        winningLine.value = winIndices
                    } else if (isBoardFull(cells)) {
                        gameActive.value = false
                    } else {
                        currentPlayer.value = if (currentPlayer.value == "X") "O" else "X"
                    }
                }
            }
        }
        Button(
            onClick = {
                cells.replaceAll { "" }
                currentPlayer.value = "X"
                gameActive.value = true
                winningLine.value = null
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Reset Game")
        }
    }
}

@Composable
fun Board(
    cells: List<String>,
    gameActive: Boolean,
    winningLine: List<Int>?,
    onCellClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.size(300.dp)
    ) {
        itemsIndexed(cells) { index, cell ->
            val row = index / 3
            val col = index % 3
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        if (winningLine?.contains(index) == true) Color.Red.copy(alpha = 0.3f)
                        else Color.White
                    )
                    .clickable { onCellClick(index) }
                    .drawGridBorders(row, col)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cell,
                    fontSize = 40.sp,
                    color = if (cell == "X") Color.Red else Color.Blue
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
                color = Color.Black,
                start = Offset(size.width - 1f, 0f),
                end = Offset(size.width - 1f, size.height),
                strokeWidth = 10f
            )
        }

        // Draw bottom border if not last row
        if (row < 2) {
            drawLine(
                color = Color.Black,
                start = Offset(0f, size.height - 1f),
                end = Offset(size.width, size.height - 1f),
                strokeWidth = 10f
            )
        }
    }
)
fun checkWinner(cells: List<String>): List<Int>? {
    // Check rows
    for (i in 0..6 step 3) {
        if (cells[i].isNotEmpty() && cells[i] == cells[i+1] && cells[i] == cells[i+2]) {
            return listOf(i, i+1, i+2)
        }
    }

    // Check columns
    for (i in 0..2) {
        if (cells[i].isNotEmpty() && cells[i] == cells[i+3] && cells[i] == cells[i+6]) {
            return listOf(i,i+3,i+6)
        }
    }

    // Check diagonals
    if (cells[0].isNotEmpty() && cells[0] == cells[4] && cells[0] == cells[8]){
        return listOf(0,4,8)
    }
    if (cells[2].isNotEmpty() && cells[2] == cells[4] && cells[2] == cells[6]){
        return listOf(2,4,6)
    }

    return null
}

fun isBoardFull(cells: List<String>): Boolean {
    return cells.none { it.isEmpty() }
}