package com.example.flappybird

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private val backgroundBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.bg)
    private val birdBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.bird)

    private lateinit var scaledBackground: Bitmap
    private lateinit var scaledBird: Bitmap

    private lateinit var bird: Bird
    private val pipes = mutableListOf<Pipe>()
    private var score = 0
    private var isGameOver = false
    private var gameThread: GameThread? = null

    private val pipeGap = 400f
    private val pipeDistance = 800f
    private val pipeSpeed = 10f

    private val paint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        textSize = 64f
        color = android.graphics.Color.BLACK
    }

    init { holder.addCallback(this); isFocusable = true }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // Scale background and bird
        scaledBackground = Bitmap.createScaledBitmap(backgroundBitmap, width, height, false)
        val birdScale = width / 15
        scaledBird = Bitmap.createScaledBitmap(birdBitmap, birdScale, birdScale, false)

        bird = Bird(width / 4f, height / 2f)

        resetGameObjects()

        gameThread = GameThread(holder, this)
        gameThread?.running = true
        gameThread?.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) { pauseGame() }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            if (!isGameOver) bird.flap()
            else resetGameObjects()
        }
        return true
    }

    private fun resetGameObjects() {
        bird.reset()
        pipes.clear()
        var x = width.toFloat()
        while (x < width * 2) { pipes.add(Pipe(x, height, pipeGap)); x += pipeDistance }
        score = 0
        isGameOver = false
    }

    fun update() {
        if (!this::bird.isInitialized || isGameOver) return

        bird.update()

        // Increase speed based on score thresholds
        val currentSpeed = when {
            score >= 50 -> pipeSpeed * 1.6f
            score>=20->pipeSpeed*1.3f
            score>=10->pipeSpeed*1.2f// Double speed at score 50
            score >= 5 -> pipeSpeed * 1.1f   // 1.5x speed at score 25
            else -> pipeSpeed
        }

        val birdRect = bird.getBounds(scaledBird.width.toFloat(), scaledBird.height.toFloat())
        val iterator = pipes.iterator()

        val pipesToAdd = mutableListOf<Pipe>()

        while (iterator.hasNext()) {
            val pipe = iterator.next()
            pipe.update(currentSpeed)

            if (pipe.collidesRect(birdRect)) isGameOver = true

            if (pipe.offScreen()) {
                iterator.remove()
                pipesToAdd.add(Pipe(width.toFloat(), height, pipeGap))
                score++
            }
        }

        pipes.addAll(pipesToAdd)

        if (birdRect.bottom >= height) isGameOver = true
    }


    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        canvas.drawBitmap(scaledBackground, 0f, 0f, null)
        canvas.drawBitmap(scaledBird, bird.x, bird.y, null)
        pipes.forEach { it.draw(canvas) }

        canvas.drawText("Score: $score", width / 2f, 100f, paint)

        if (isGameOver) {
            paint.color = android.graphics.Color.RED
            paint.textSize = 120f
            canvas.drawText("GAME OVER", width / 2f, height / 2f, paint)
            paint.color = android.graphics.Color.BLACK
            paint.textSize = 64f
        }
    }

    // ---------------- Pause & Resume ----------------
    fun pauseGame() {
        gameThread?.running = false
        try { gameThread?.join() } catch (e: InterruptedException) {}
    }

    fun resumeGame() {
        if (gameThread == null || !gameThread!!.running) {
            gameThread = GameThread(holder, this)
            gameThread?.running = true
            gameThread?.start()
        }
    }
}
