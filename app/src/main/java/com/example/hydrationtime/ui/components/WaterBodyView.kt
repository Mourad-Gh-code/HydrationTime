package com.example.hydrationtime.ui.components

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.sin

/**
 * WaterBodyView - Human silhouette with animated water level and horizon stabilization
 */
class WaterBodyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var waterLevel = 0.5f // 0.0 to 1.0
    private var tiltAngle = 0f // Device tilt angle
    private var waveOffset = 0f // For wave animation

    private val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#E0E0E0")
    }

    private val waterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val waterGradient: LinearGradient
        get() {
            val waterY = height * (1 - waterLevel)
            return LinearGradient(
                0f, waterY, 0f, height.toFloat(),
                intArrayOf(
                    Color.parseColor("#4FC3F7"),
                    Color.parseColor("#29B6F6"),
                    Color.parseColor("#0288D1")
                ),
                null,
                Shader.TileMode.CLAMP
            )
        }

    private val teaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#66BB6A")
    }

    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#80FFFFFF")
    }

    private val bodyPath = Path()
    private val waterPath = Path()
    private val bubbles = mutableListOf<Bubble>()

    private var waveAnimator: ValueAnimator? = null

    init {
        generateBubbles()
    }

    private fun startWaveAnimation() {
        waveAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 3000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                waveOffset = animation.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun generateBubbles() {
        for (i in 0..15) {
            bubbles.add(
                Bubble(
                    x = (Math.random() * width).toFloat(),
                    y = (Math.random() * height).toFloat(),
                    radius = (5 + Math.random() * 10).toFloat(),
                    speed = (1 + Math.random() * 3).toFloat()
                )
            )
        }
    }

    fun setWaterLevel(level: Float) {
        waterLevel = level.coerceIn(0f, 1f)
        invalidate()
    }

    fun setTilt(angle: Float) {
        tiltAngle = angle.coerceIn(-45f, 45f)
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createBodyShape()
        if (bubbles.isEmpty()) {
            generateBubbles()
        }
    }

    private fun createBodyShape() {
        bodyPath.reset()
        val centerX = width / 2f
        val headRadius = width * 0.15f
        val bodyWidth = width * 0.4f
        val bodyHeight = height * 0.8f

        // Head (circle)
        bodyPath.addCircle(centerX, headRadius + 20, headRadius, Path.Direction.CW)

        // Body (rounded rectangle)
        val bodyTop = headRadius * 2 + 40
        val bodyRect = RectF(
            centerX - bodyWidth / 2,
            bodyTop,
            centerX + bodyWidth / 2,
            bodyTop + bodyHeight
        )
        bodyPath.addRoundRect(bodyRect, 50f, 50f, Path.Direction.CW)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw body outline
        canvas.drawPath(bodyPath, bodyPaint)

        // Calculate water surface with tilt and wave
        val waterY = height * (1 - waterLevel)

        // Create water path with wave effect and tilt
        waterPath.reset()
        waterPath.moveTo(0f, height.toFloat())

        val waveAmplitude = 15f
        val waveFrequency = 0.02f

        for (x in 0..width step 5) {
            val wave = sin((x * waveFrequency + waveOffset * 0.05).toDouble()).toFloat() * waveAmplitude
            val tiltOffset = (x - width / 2) * tiltAngle * 0.01f
            val y = waterY + wave - tiltOffset
            waterPath.lineTo(x.toFloat(), y)
        }

        waterPath.lineTo(width.toFloat(), height.toFloat())
        waterPath.close()

        // Clip to body shape
        canvas.save()
        canvas.clipPath(bodyPath)

        // Draw water with gradient
        waterPaint.shader = waterGradient
        canvas.drawPath(waterPath, waterPaint)

        // Draw bubbles
        updateAndDrawBubbles(canvas, waterY)

        canvas.restore()
    }

    private fun updateAndDrawBubbles(canvas: Canvas, waterSurface: Float) {
        bubbles.forEach { bubble ->
            // Only draw bubbles below water surface
            if (bubble.y > waterSurface) {
                canvas.drawCircle(bubble.x, bubble.y, bubble.radius, bubblePaint)

                // Move bubble up
                bubble.y -= bubble.speed
                if (bubble.y < waterSurface) {
                    // Reset bubble to bottom
                    bubble.y = height.toFloat()
                    bubble.x = (Math.random() * width).toFloat()
                }
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Reset bubbles to ensure they animate properly
        bubbles.clear()
        generateBubbles()
        startWaveAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        waveAnimator?.cancel()
        waveAnimator = null
    }

    private data class Bubble(
        var x: Float,
        var y: Float,
        val radius: Float,
        val speed: Float
    )
}
