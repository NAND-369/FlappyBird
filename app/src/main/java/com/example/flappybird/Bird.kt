package com.example.flappybird

class Bird(private val startX: Float, private val startY: Float) {

    var x = startX
    var y = startY
    var velocity = 0f

    private val gravity = 0.6f   // classic gravity
    private val lift = -15f      // flap strength
    private val maxVelocity = 25f // max downward speed

    fun update() {
        velocity += gravity
        if (velocity > maxVelocity) velocity = maxVelocity
        y += velocity
        if (y < 0f) y = 0f
    }

    fun flap() { velocity = lift }

    fun reset() {
        y = startY
        velocity = 0f
    }

    fun getBounds(width: Float, height: Float): android.graphics.RectF {
        val offsetX = width * 0.2f  // 20% padding on X axis
        val offsetY = height * 0.2f // 20% padding on Y axis
        return android.graphics.RectF(x + offsetX, y + offsetY, x + width - offsetX, y + height - offsetY)
    }
}
