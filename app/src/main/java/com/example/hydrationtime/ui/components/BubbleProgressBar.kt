package com.example.hydrationtime.ui.components

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.sin

/**
 * BubbleProgressBar - Animated progress bar with moving bubbles
 */
class BubbleProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress = 0f // 0 to 100
    private var animatedProgress = 0f
    private var bubbleOffset = 0f

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#E0E0E0")
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#80FFFFFF")
    }

    private val bubbles = mutableListOf<Bubble>()
    private var bubbleAnimator: ValueAnimator? = null
    private var progressAnimator: ValueAnimator? = null

    init {
        startBubbleAnimation()
        generateBubbles()
    }

    private fun startBubbleAnimation() {
        bubbleAnimator = ValueAnimator.ofFloat(0f, 1000f).apply {
            duration = 5000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                bubbleOffset = animation.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun generateBubbles() {
        for (i in 0..20) {
            bubbles.add(
                Bubble(
                    offsetX = (Math.random() * 100).toFloat(),
                    y = (0.2 + Math.random() * 0.6).toFloat(),
                    radius = (3 + Math.random() * 6).toFloat(),
                    speed = (0.5 + Math.random() * 1.5).toFloat()
                )
            )
        }
    }

    fun setProgress(value: Float) {
        val targetProgress = value.coerceIn(0f, 100f)

        progressAnimator?.cancel()
        progressAnimator = ValueAnimator.ofFloat(animatedProgress, targetProgress).apply {
            duration = 800
            addUpdateListener { animation ->
                animatedProgress = animation.animatedValue as Float
                invalidate()
            }
            start()
        }

        progress = targetProgress
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cornerRadius = height / 2f
        val progressWidth = (width * animatedProgress / 100f).coerceAtLeast(cornerRadius * 2)

        // Draw background
        canvas.drawRoundRect(
            0f, 0f, width.toFloat(), height.toFloat(),
            cornerRadius, cornerRadius,
            backgroundPaint
        )

        // Draw progress with gradient
        if (animatedProgress > 0) {
            val gradient = LinearGradient(
                0f, 0f, progressWidth, 0f,
                intArrayOf(
                    Color.parseColor("#4FC3F7"),
                    Color.parseColor("#29B6F6"),
                    Color.parseColor("#0288D1")
                ),
                null,
                Shader.TileMode.CLAMP
            )
            progressPaint.shader = gradient

            canvas.drawRoundRect(
                0f, 0f, progressWidth, height.toFloat(),
                cornerRadius, cornerRadius,
                progressPaint
            )

            // Draw bubbles in progress area
            drawBubbles(canvas, progressWidth, cornerRadius)
        }
    }

    private fun drawBubbles(canvas: Canvas, progressWidth: Float, cornerRadius: Float) {
        bubbles.forEach { bubble ->
            // Calculate bubble position
            val bubbleX = ((bubble.offsetX + bubbleOffset * bubble.speed) % 100) / 100f * progressWidth
            val bubbleY = height * bubble.y

            // Only draw if within progress area
            if (bubbleX < progressWidth - cornerRadius) {
                // Add wave motion
                val wave = sin((bubbleOffset * 0.01 + bubble.offsetX * 0.1).toDouble()).toFloat() * 3
                canvas.drawCircle(bubbleX, bubbleY + wave, bubble.radius, bubblePaint)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        bubbleAnimator?.cancel()
        progressAnimator?.cancel()
    }

    private data class Bubble(
        val offsetX: Float,
        val y: Float,
        val radius: Float,
        val speed: Float
    )
}
