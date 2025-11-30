package com.example.a2048game

import android.app.AlertDialog
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var gestureDetector: GestureDetector
    private var currentLevel = Level.MEDIUM
    private val game = GameManager(currentLevel)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Swipe
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (e1 == null) return false
                val dx = e2.x - e1.x
                val dy = e2.y - e1.y
                val moved = if (Math.abs(dx) > Math.abs(dy)) {
                    if (dx > 0) game.moveRight() else game.moveLeft()
                } else {
                    if (dy > 0) game.moveDown() else game.moveUp()
                }
                if (moved) updateUI()
                if (game.isGameOver()) Toast.makeText(this@MainActivity, "Game Over! Score: ${game.score}", Toast.LENGTH_LONG).show()
                return true
            }
        })

        // Botón Nueva Partida
        findViewById<Button>(R.id.buttonNewGame).setOnClickListener {
            game.level = currentLevel
            game.initBoard()
            updateUI()
        }

        // Botón Nivel
        findViewById<Button>(R.id.buttonLevel).setOnClickListener {
            val levels = arrayOf("Fácil", "Medio", "Difícil")
            AlertDialog.Builder(this)
                .setTitle("Selecciona Nivel")
                .setItems(levels) { _, which ->
                    currentLevel = when (which) {
                        0 -> Level.EASY
                        1 -> Level.MEDIUM
                        else -> Level.HARD
                    }
                    game.level = currentLevel
                    game.initBoard()
                    updateUI()
                }
                .show()
        }

        // Cargar partida guardada si existe
        if (!loadGame()) {
            game.initBoard()
        }
        updateUI()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let { gestureDetector.onTouchEvent(it) }
        return super.onTouchEvent(event)
    }

    private fun updateUI() {
        val ids = arrayOf(
            arrayOf(R.id.tile00, R.id.tile01, R.id.tile02, R.id.tile03),
            arrayOf(R.id.tile10, R.id.tile11, R.id.tile12, R.id.tile13),
            arrayOf(R.id.tile20, R.id.tile21, R.id.tile22, R.id.tile23),
            arrayOf(R.id.tile30, R.id.tile31, R.id.tile32, R.id.tile33)
        )
        for (i in 0..3) for (j in 0..3) {
            val tv = findViewById<TextView>(ids[i][j])
            val value = game.board[i][j].value
            tv.text = if (value == 0) "" else value.toString()
            tv.setBackgroundColor(getColorForValue(value))
        }
        findViewById<TextView>(R.id.scoreText).text = "Score: ${game.score}"
    }

    private fun getColorForValue(value: Int): Int {
        return when (value) {
            0 -> 0xFFCDC1B4.toInt()
            2 -> 0xFFEEE4DA.toInt()
            4 -> 0xFFEDE0C8.toInt()
            8 -> 0xFFF2B179.toInt()
            16 -> 0xFFF59563.toInt()
            32 -> 0xFFF67C5F.toInt()
            64 -> 0xFFF65E3B.toInt()
            128 -> 0xFFEDCF72.toInt()
            256 -> 0xFFEDCC61.toInt()
            512 -> 0xFFEDC850.toInt()
            1024 -> 0xFFEDC53F.toInt()
            2048 -> 0xFFEDC22E.toInt()
            else -> 0xFF3C3A32.toInt()
        }
    }

    // Guardar partida
    private fun saveGame() {
        val prefs = getSharedPreferences("game2048", MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("board", game.boardToString())
        editor.putInt("score", game.score)
        editor.putString("level", game.level.name)
        editor.apply()
    }

    // Cargar partida
    private fun loadGame(): Boolean {
        val prefs = getSharedPreferences("game2048", MODE_PRIVATE)
        val boardString = prefs.getString("board", null) ?: return false
        val score = prefs.getInt("score", 0)
        val levelName = prefs.getString("level", Level.MEDIUM.name)!!
        val level = Level.valueOf(levelName)
        currentLevel = level
        game.level = level
        game.stringToBoard(boardString)
        game.score = score
        return true
    }

    override fun onPause() {
        super.onPause()
        saveGame()
    }

    override fun onResume() {
        super.onResume()
        loadGame()
        updateUI()
    }
}
