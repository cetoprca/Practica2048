package com.example.a2048game

enum class Level(val twoPercent: Double) {
    EASY(0.8),
    MEDIUM(0.9),
    HARD(0.95)
}

class GameManager(var level: Level = Level.MEDIUM) {

    var board = Array(4) { Array(4) { Tile() } }
    var score = 0

    init {
        initBoard()
    }

    fun initBoard() {
        for (i in 0..3) for (j in 0..3) board[i][j].value = 0
        addRandomTile()
        addRandomTile()
        score = 0
    }

    fun addRandomTile() {
        val emptyTiles = mutableListOf<Pair<Int, Int>>()
        for (i in 0..3) for (j in 0..3) if (board[i][j].isEmpty()) emptyTiles.add(Pair(i, j))
        if (emptyTiles.isNotEmpty()) {
            val (row, col) = emptyTiles.random()
            board[row][col].value = if (Math.random() < level.twoPercent) 2 else 4
        }
    }

    private fun mergeRow(row: MutableList<Int>): MutableList<Int> {
        val newRow = row.filter { it != 0 }.toMutableList()
        var i = 0
        while (i < newRow.size - 1) {
            if (newRow[i] == newRow[i + 1]) {
                newRow[i] *= 2
                score += newRow[i]
                newRow.removeAt(i + 1)
            }
            i++
        }
        while (newRow.size < 4) newRow.add(0)
        return newRow
    }

    fun moveLeft(): Boolean {
        var moved = false
        for (i in 0..3) {
            val row = board[i].map { it.value }.toMutableList()
            val newRow = mergeRow(row)
            for (j in 0..3) {
                if (board[i][j].value != newRow[j]) {
                    board[i][j].value = newRow[j]
                    moved = true
                }
            }
        }
        if (moved) addRandomTile()
        return moved
    }

    fun moveRight(): Boolean {
        rotateBoard(180)
        val moved = moveLeft()
        rotateBoard(180)
        return moved
    }

    fun moveUp(): Boolean {
        rotateBoard(270)
        val moved = moveLeft()
        rotateBoard(90)
        return moved
    }

    fun moveDown(): Boolean {
        rotateBoard(90)
        val moved = moveLeft()
        rotateBoard(270)
        return moved
    }

    private fun rotateBoard(degrees: Int) {
        repeat((degrees / 90) % 4) {
            val newBoard = Array(4) { Array(4) { Tile() } }
            for (i in 0..3) for (j in 0..3) newBoard[j][3 - i].value = board[i][j].value
            board = newBoard
        }
    }

    fun isGameOver(): Boolean {
        for (i in 0..3) for (j in 0..3) {
            if (board[i][j].isEmpty()) return false
            if (i < 3 && board[i][j].value == board[i + 1][j].value) return false
            if (j < 3 && board[i][j].value == board[i][j + 1].value) return false
        }
        return true
    }

    fun boardToString(): String = board.flatten().joinToString(",") { it.value.toString() }

    fun stringToBoard(s: String) {
        val values = s.split(",").map { it.toInt() }
        for (i in 0..3) for (j in 0..3) board[i][j].value = values[i * 4 + j]
    }
}
