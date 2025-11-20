package com.example.hydrationtime.ui.components

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator

/**
 * DrinkTypeCircularChart - Circular ring chart showing drink distribution with glow effect
 */
class DrinkTypeCircularChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var data: Map<String, Float> = emptyMap()
    private var animatedProgress = 0f

    private val drinkColors = mapOf(
        "Water" to Color.parseColor("#29B6F6"),
        "Tea" to Color.parseColor("#66BB6A"),
        "Coffee" to Color.parseColor("#8D6E63"),
        "Juice" to Color.parseColor("#FF9800"),
        "Smoothie" to Color.parseColor("#E91E63"),
        "Milk" to Color.parseColor("#EEEEEE")
    )

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 40f
        strokeCap = Paint.Cap.ROUND
    }

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 50f
        maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 48f
        color = Color.WHITE
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 16f
        color = Color.parseColor("#B3FFFFFF")
    }

    private var animator: ValueAnimator? = null

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null) // Enable blur
    }

    fun setData(newData: Map<String, Float>) {
        data = newData

        // Animate from 0 to 100%
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                animatedProgress = animation.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (data.isEmpty()) return

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (Math.min(width, height) / 2f) - 60f

        val rect = RectF(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )

        // Calculate total
        val total = data.values.sum()
        if (total == 0f) return

        var startAngle = -90f // Start from top

        // Draw each segment
        data.forEach { (drink, amount) ->
            val sweepAngle = (amount / total) * 360f * animatedProgress
            val color = drinkColors[drink] ?: Color.GRAY

            // Draw glow
            glowPaint.color = color
            glowPaint.alpha = 80
            canvas.drawArc(rect, startAngle, sweepAngle, false, glowPaint)

            // Draw arc
            arcPaint.color = color
            canvas.drawArc(rect, startAngle, sweepAngle, false, arcPaint)

            startAngle += sweepAngle
        }

        // Draw percentage in center
        val mainDrink = data.maxByOrNull { it.value }
        if (mainDrink != null) {
            val percentage = ((mainDrink.value / total) * 100 * animatedProgress).toInt()
            canvas.drawText("$percentage%", centerX, centerY + 15, textPaint)
            canvas.drawText(mainDrink.key, centerX, centerY + 45, labelPaint)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
}
