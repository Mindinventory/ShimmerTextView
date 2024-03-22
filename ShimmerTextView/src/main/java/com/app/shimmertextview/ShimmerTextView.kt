package com.app.shimmertextview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.FloatRange
import androidx.annotation.Px
import androidx.appcompat.widget.AppCompatTextView

class ShimmerTextView : AppCompatTextView {

    private val paint: Paint = Paint()
    private val shimmerDrawable: ShimmerDrawable = ShimmerDrawable()

    private var showShimmer = true
    private var stoppedShimmerBecauseVisibility = true

    private var shimmerBuilder: Shimmer.Builder<*>? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context?, attrs: AttributeSet?) {
        setWillNotDraw(false)
        shimmerDrawable.callback = this

        if (attrs == null) {
            setShimmer(Shimmer.AlphaHighlightBuilder().build())
            return
        }

        val typedArray: TypedArray? =
            context?.obtainStyledAttributes(attrs, R.styleable.ShimmerTextView, 0, 0)
        try {
            shimmerBuilder =
                if (typedArray?.hasValue(R.styleable.ShimmerTextView_shimmer_colored) == true && typedArray.getBoolean(
                        R.styleable.ShimmerTextView_shimmer_colored,
                        false
                    )
                ) Shimmer.ColorHighlightBuilder() else Shimmer.AlphaHighlightBuilder()
            typedArray?.let {
                setShimmer(shimmerBuilder?.consumeAttributes(it)?.build())
            }
        } finally {
            typedArray?.recycle()
        }
    }

    private fun setShimmer(shimmer: Shimmer?): ShimmerTextView {
        shimmer?.let {
            shimmerDrawable.setShimmer(it)

            if (it.clipToChildren) {
                setLayerType(LAYER_TYPE_HARDWARE, paint)
            } else {
                setLayerType(LAYER_TYPE_NONE, null)
            }
        }

        return this
    }

    fun getShimmer(): Shimmer? {
        return shimmerDrawable.getShimmer()
    }

    fun startShimmer() {
        shimmerDrawable.startShimmer()
    }

    fun stopShimmer() {
        stoppedShimmerBecauseVisibility = false
        shimmerDrawable.stopShimmer()
    }

    private fun isShimmerStarted(): Boolean {
        return shimmerDrawable.isShimmerStarted()
    }

    fun showShimmer(startShimmer: Boolean) {
        showShimmer = true
        if (startShimmer) {
            startShimmer()
        }
        invalidate()
    }

    fun hideShimmer() {
        stopShimmer()
        showShimmer = false
        invalidate()
    }

    fun isShimmerVisible(): Boolean {
        return showShimmer
    }

    fun isShimmerRunning(): Boolean {
        return shimmerDrawable.isShimmerRunning()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        shimmerDrawable.setBounds(0, 0, width, height)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (shimmerDrawable == null) {
            return
        }

        if (visibility != View.VISIBLE) {
            if (isShimmerStarted()) {
                stopShimmer()
                stoppedShimmerBecauseVisibility = true
            }
        } else if (stoppedShimmerBecauseVisibility) {
            shimmerDrawable.maybeStartShimmer()
            stoppedShimmerBecauseVisibility = false
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        shimmerDrawable.maybeStartShimmer()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopShimmer()
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (showShimmer) {
            shimmerDrawable.draw(canvas)
        }
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || who == shimmerDrawable
    }

    fun setStaticAnimationProgress(value: Float) {
        shimmerDrawable.setStaticAnimationProgress(value)
    }

    fun clearStaticAnimationProgress() {
        shimmerDrawable.clearStaticAnimationProgress()
    }

    fun setBaseColor(baseColor: Int): ShimmerTextView {
        if (shimmerBuilder is Shimmer.ColorHighlightBuilder) {
            (shimmerBuilder as Shimmer.ColorHighlightBuilder).setBaseColor(baseColor)
        }
        return this
    }

    fun setHighLightColor(highlightColor: Int): ShimmerTextView {
        if (shimmerBuilder is Shimmer.ColorHighlightBuilder) {
            (shimmerBuilder as Shimmer.ColorHighlightBuilder).setHighlightColor(highlightColor)
        }
        return this
    }

    fun setClipToChildren(status: Boolean): ShimmerTextView {
        shimmerBuilder?.setClipToChildren(status)
        return this
    }

    fun setAutoStart(status: Boolean): ShimmerTextView {
        shimmerBuilder?.setAutoStart(status)
        return this
    }

    fun setBaseAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float): ShimmerTextView {
        shimmerBuilder?.setBaseAlpha(alpha)
        return this
    }

    fun setHighlightAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float): ShimmerTextView {
        shimmerBuilder?.setHighlightAlpha(alpha)
        return this
    }

    fun setDuration(millis: Long): ShimmerTextView {
        shimmerBuilder?.setDuration(millis)
        return this
    }

    fun setRepeatCount(repeatCount: Int): ShimmerTextView {
        shimmerBuilder?.setRepeatCount(repeatCount)
        return this
    }

    fun setRepeatDelay(millis: Long): ShimmerTextView {
        shimmerBuilder?.setRepeatDelay(millis)
        return this
    }

    fun setStartDelay(millis: Long): ShimmerTextView {
        shimmerBuilder?.setStartDelay(millis)
        return this
    }

    fun setRepeatMode(mode: Int): ShimmerTextView {
        shimmerBuilder?.setRepeatMode(mode)
        return this
    }

    fun setDirection(@Shimmer.Direction direction: Int): ShimmerTextView {
        shimmerBuilder?.setDirection(direction)
        return this
    }

    fun setShape(@Shimmer.Shape shape: Int): ShimmerTextView {
        shimmerBuilder?.setShape(shape)
        return this
    }

    fun setDropOff(dropOff: Float): ShimmerTextView {
        shimmerBuilder?.setDropOff(dropOff)
        return this
    }

    fun setFixedWidth(@Px fixedWidth: Int): ShimmerTextView {
        shimmerBuilder?.setFixedWidth(fixedWidth)
        return this
    }

    fun setFixedHeight(@Px fixedHeight: Int): ShimmerTextView {
        shimmerBuilder?.setFixedHeight(fixedHeight)
        return this
    }

    fun setIntensity(intensity: Float): ShimmerTextView {
        shimmerBuilder?.setIntensity(intensity)
        return this
    }

    fun setWidthRatio(widthRatio: Float): ShimmerTextView {
        shimmerBuilder?.setWidthRatio(widthRatio)
        return this
    }

    fun setHeightRatio(heightRatio: Float): ShimmerTextView {
        shimmerBuilder?.setHeightRatio(heightRatio)
        return this
    }

    fun setTilt(tilt: Float): ShimmerTextView {
        shimmerBuilder?.setTilt(tilt)
        return this
    }

    fun setColored(isColored: Boolean): ShimmerTextView {
        shimmerBuilder?.setColored(isColored)
        return this
    }

    fun build() {
        shimmerBuilder?.build()
    }
}