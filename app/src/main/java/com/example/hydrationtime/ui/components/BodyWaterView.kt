package com.example.hydrationtime.ui.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class BodyWaterView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val bodyPaint = Paint().apply {
        color = Color.parseColor("#4DFFFFFF") // Glassy Outline
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
    }

    private val waterPaint = Paint().apply {
        color = Color.parseColor("#29B6F6") // Water Blue
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private var currentPercentage = 0f
    private var tiltAngle = 0f
    private val bodyPath = Path()
    private val clipPath = Path()
    private val bubbles = ArrayList<Bubble>()
    private var lastAnimationTime = 0L

    init {
        for (i in 0..14) bubbles.add(Bubble())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createBodyPath(w.toFloat(), h.toFloat())
    }

    private fun createBodyPath(w: Float, h: Float) {
        bodyPath.reset()
        // Simple Human Silhouette Logic
        val headRadius = w * 0.15f
        val centerX = w / 2
        val topMargin = 50f

        // Head
        bodyPath.addCircle(centerX, topMargin + headRadius, headRadius, Path.Direction.CW)

        val shoulderY = topMargin + headRadius * 2 + 20
        val bodyBottom = h * 0.9f
        val bodyWidth = w * 0.45f

        // Torso/Legs Outline
        bodyPath.moveTo(centerX - bodyWidth/2, shoulderY) // Left Shoulder
        bodyPath.lineTo(centerX + bodyWidth/2, shoulderY) // Right Shoulder
        bodyPath.lineTo(centerX + bodyWidth/3, bodyBottom) // Right Leg
        bodyPath.lineTo(centerX - bodyWidth/3, bodyBottom) // Left Leg
        bodyPath.close()

        clipPath.set(bodyPath)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Clip to body shape
        canvas.save()
        canvas.clipPath(clipPath)

        // Horizon Stabilization Logic (Rotate water based on tilt)
        canvas.rotate(tiltAngle, width / 2f, height / 2f)

        // Calculate Water Level
        val waterHeight = height * currentPercentage
        val waterTop = height - waterHeight

        // Draw Water (Make it large enough to cover rotation)
        val extraSize = width.toFloat()
        canvas.drawRect(-extraSize, waterTop, width + extraSize, height + extraSize, waterPaint)

        // Draw Bubbles
        drawBubbles(canvas, waterTop)

        canvas.restore()

        // Draw Body Outline
        canvas.drawPath(bodyPath, bodyPaint)

        invalidate() // Loop animation
    }

    private fun drawBubbles(canvas: Canvas, waterLevel: Float) {
        val now = System.currentTimeMillis()
        val dt = if (lastAnimationTime == 0L) 16 else now - lastAnimationTime
        lastAnimationTime = now

        for (b in bubbles) {
            // Move bubbles up (relative to rotation makes it complex, keeping simple up for now)
            b.y -= b.speed * (dt / 16f)

            // Reset if bubble goes above water or off screen
            if (b.y < waterLevel - 100 || b.y < -500) { // -500 to handle rotation space
                b.y = height.toFloat() + 100
                b.x = (Math.random() * width * 2 - width).toFloat() // Wide spread
            }

            val bubblePaint = Paint().apply {
                color = Color.WHITE
                alpha = 80
            }
            canvas.drawCircle(b.x, b.y, b.radius, bubblePaint)
        }
    }

    fun setWaterLevel(percentage: Float) {
        this.currentPercentage = percentage.coerceIn(0f, 1f)
    }

    fun setTilt(angle: Float) {
        this.tiltAngle = angle
    }

    class Bubble {
        var x = 0f
        var y = 0f
        var radius = (4..12).random().toFloat()
        var speed = (3..7).random().toFloat()
    }
}