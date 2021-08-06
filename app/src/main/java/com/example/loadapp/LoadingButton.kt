package com.example.loadapp

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var loadingWidth = 0f
    private var loadingAngle = 0f
    var downloadStatus = "Download"
    private var circleAnimator = ValueAnimator()
    private var btnAnimator = ValueAnimator()
    private val paintBtn = Paint()
    private val paintCircle = Paint()
    private val paintBtnText = Paint().apply {
        textSize = 55f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("", Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Completed -> {
                downloadStatus = "Download"
                btnAnimator.end()
                circleAnimator.end()
                loadingWidth = 0f
                loadingAngle = 0f
                invalidate()
            }
            ButtonState.Loading -> {
                downloadStatus = "Downloading..."

                btnAnimator = ValueAnimator.ofFloat(0f, measuredWidth.toFloat())
                    .apply {
                        duration = 2000
                        repeatMode = ValueAnimator.RESTART
                        repeatCount = ValueAnimator.INFINITE
                        addUpdateListener {
                            loadingWidth = animatedValue as Float
                            this@LoadingButton.invalidate()
                        }
                        start()
                    }

                circleAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
                    duration = 2000
                    repeatMode = ValueAnimator.RESTART
                    repeatCount = ValueAnimator.INFINITE
                    interpolator = AccelerateInterpolator(1f)
                    addUpdateListener {
                        loadingAngle = animatedValue as Float
                        this@LoadingButton.invalidate()
                    }
                    start()
                }
            }
        }
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }

    init {
        isClickable = true
    }

    override fun performClick(): Boolean {
        if (super.performClick()) {
            return true
        }
        invalidate()
        return true
    }


    override fun onDraw(canvas: Canvas?) {
        paintBtnText.color = Color.WHITE
        paintCircle.color = Color.WHITE
        paintBtn.color = resources.getColor(R.color.colorPrimary)
        canvas?.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paintBtn)
        paintBtn.color = context.getColor(R.color.colorAccent)
        canvas?.drawRect(0f, 0f, loadingWidth, measuredHeight.toFloat(), paintBtn)
        canvas?.drawText(downloadStatus, width / 2.0f, height / 1.7f, paintBtnText)
        canvas?.drawArc(
            measuredWidth - 100f,
            (measuredHeight / 2) - 30f,
            measuredWidth - 50f,
            (measuredHeight / 2) + 30f,
            0f, loadingAngle, true, paintCircle
        )
        super.onDraw(canvas)
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
}