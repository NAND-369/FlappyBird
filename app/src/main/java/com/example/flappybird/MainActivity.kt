package com.example.flappybird

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameView = GameView(this)
        setContentView(gameView)
    }

    override fun onPause() {
        super.onPause()
        gameView.pauseGame()   // stop loop
    }

    override fun onResume() {
        super.onResume()
        gameView.resumeGame()  // restart loop
    }
}
