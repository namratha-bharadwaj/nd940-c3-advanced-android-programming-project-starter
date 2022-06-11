package com.udacity.customButton

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.addListener
import androidx.core.content.withStyledAttributes
import com.udacity.R
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var text: String? = null
    private var progressText: String? = null
    private var animationDuration: Long = 500
    private var drawAngle = 0F
    private val textBounds = Rect()

    //loading in progress variables
    private var completedColor = Color.GREEN
    private var progressColor = Color.RED
    private var pacmanColor = Color.YELLOW

    private var progressRect = Rect(0, 0, 0, 0)
    private var textToDraw: String? = null

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 26F
        typeface = Typeface.SERIF
    }

    private val valueAnimator = ValueAnimator()
    private var onAnimationEnd: (() -> Unit)? = null


    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new) {
            ButtonState.Clicked -> startButtonAnimation()
            ButtonState.Loading -> showProgressAnimation()
            ButtonState.Completed -> resetProgressAnimation()
        }

    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            progressColor = getColor(R.styleable.LoadingButton_progressColor, Color.RED)
            completedColor = getColor(R.styleable.LoadingButton_defaultColor, Color.GREEN)
            pacmanColor = getColor(R.styleable.LoadingButton_pacmanProgressColor, Color.YELLOW)
            animationDuration =
                getInteger(R.styleable.LoadingButton_defaultAnimationDuration, 500).toLong()
            text = getString(R.styleable.LoadingButton_android_text)
            progressText = getString(R.styleable.LoadingButton_progressText)
            paint.textSize =
                getDimensionPixelSize(R.styleable.LoadingButton_android_textSize, 16).toFloat()
        }
        textToDraw = text

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawColor(completedColor)
        progressPaint.color = progressColor
        canvas?.drawRect(progressRect, progressPaint)
        textToDraw?.let {
            paint.getTextBounds(it, 0, it.length, textBounds)
            canvas?.drawText(
                it,
                (widthSize / 2).toFloat(),
                heightSize / 2 - textBounds.exactCenterY(),
                paint
            )
        }

        progressPaint.color = pacmanColor
        val start = (widthSize / 2 + textBounds.exactCenterX()) + textBounds.height() / 2
        canvas?.drawArc(
            start,
            (heightSize / 2 - textBounds.height() / 2).toFloat(),
            start + textBounds.height(),
            (heightSize / 2 + textBounds.height() / 2).toFloat(),
            0F,
            drawAngle,
            true,
            progressPaint
        )

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {
        restartAnimation()
        super.performClick()
        return true
    }

    private fun restartAnimation() {
        buttonState = ButtonState.Clicked
    }

    private fun startButtonAnimation() {
        textToDraw = progressText
        buttonState = ButtonState.Loading
    }

    private fun showProgressAnimation() {
        valueAnimator.apply {
            removeAllUpdateListeners()
            removeAllListeners()
            cancel()
            interpolator = AccelerateDecelerateInterpolator()
            setIntValues(widthSize)
            addUpdateListener {
                progressRect.right = it.animatedValue as Int
                drawAngle = ((it.animatedValue as Int) * 360 / widthSize).toFloat()
                progressRect.bottom = heightSize
                if (it.animatedValue == widthSize && buttonState == ButtonState.Completed) {
                    valueAnimator.cancel()
                }
                invalidate()
            }
            addListener(
                onEnd = {
                    buttonState = ButtonState.Completed
                    onAnimationEnd?.invoke()
                }
            )
            duration = animationDuration
            start()
        }

    }

    private fun resetProgressAnimation() {
        progressRect.right = 0
        drawAngle = 0F
        textToDraw = text
        invalidate()
    }

}
