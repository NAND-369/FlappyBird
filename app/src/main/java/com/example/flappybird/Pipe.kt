package com.example.flappybird

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import kotlin.random.Random

class Pipe(private val startX: Float, private val screenHeight: Int, private val gap: Float) {

    private val width = 150f
    private var x = startX
    private val paint = Paint().apply { color = Color.GREEN }

    private val topHeight = Random.nextInt(100, screenHeight / 2)
    private val bottomY = topHeight + gap

    fun update(speed: Float) { x -= speed }

    fun draw(canvas: Canvas) {
        canvas.drawRect(RectF(x, 0f, x + width, topHeight.toFloat()), paint)
        canvas.drawRect(RectF(x, bottomY, x + width, screenHeight.toFloat()), paint)
    }

    fun offScreen(): Boolean = x + width < 0

    fun collidesRect(rect: RectF): Boolean {
        val offset = 10f  // example padding value in pixels

        val topRect = RectF(x + offset, 0f, x + width - offset, topHeight.toFloat() - offset)
        val bottomRect = RectF(x + offset, bottomY + offset, x + width - offset, screenHeight.toFloat())
        return RectF.intersects(rect, topRect) || RectF.intersects(rect, bottomRect)
    }
}
