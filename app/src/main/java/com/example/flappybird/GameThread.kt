package com.example.flappybird

import android.graphics.Canvas
import android.view.SurfaceHolder

class GameThread(private val surfaceHolder: SurfaceHolder, private val gameView: GameView) : Thread() {

    @Volatile var running = false
    private val targetFPS = 60
    private val targetTime = (1000 / targetFPS).toLong()

    override fun run() {
        while (running) {
            val startTime = System.currentTimeMillis()
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                if (canvas != null) {
                    synchronized(surfaceHolder) {
                        gameView.update()
                        gameView.draw(canvas)
                    }
                }
            } finally { if (canvas != null) surfaceHolder.unlockCanvasAndPost(canvas) }

            val timeTaken = System.currentTimeMillis() - startTime
            val sleepTime = targetTime - timeTaken
            if (sleepTime > 0) try { sleep(sleepTime) } catch (e: InterruptedException) {}
        }
    }
}
