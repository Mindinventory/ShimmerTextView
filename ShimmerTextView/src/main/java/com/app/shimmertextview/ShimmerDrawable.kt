package com.app.shimmertextview

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.animation.LinearInterpolator
import androidx.annotation.FloatRange
import androidx.annotation.Nullable
import androidx.annotation.Px
import kotlin.math.sqrt
import kotlin.math.tan

class ShimmerDrawable : Drawable() {

    private val shimmerPaint = Paint()
    private val rect = Rect()
    private val shaderMatrix = Matrix()

    @Nullable
    private var valueAnimator: ValueAnimator? = null
    private var staticAnimationProgress = -1F

    private var shimmer: Shimmer? = null

    init {
        shimmerPaint.isAntiAlias = true
    }

    private val valueAnimatorUpdateListener = ValueAnimator.AnimatorUpdateListener {
        invalidateSelf()
    }

    fun setShimmer(@Nullable shimmer: Shimmer) {
        this.shimmer = shimmer
        if (this.shimmer != null) {
            shimmerPaint.xfermode =
                PorterDuffXfermode(if (shimmer.alphaShimmer) PorterDuff.Mode.DST_IN else PorterDuff.Mode.SRC_IN)
        }
        updateShader()
        updateValueAnimator()
        invalidateSelf()
    }


    @Nullable
    fun getShimmer(): Shimmer? {
        return shimmer
    }

    fun startShimmer() {
        if (valueAnimator != null && !isShimmerStarted() && callback != null) {
            valueAnimator?.start()
        }
    }

    fun stopShimmer() {
        if (valueAnimator != null && isShimmerStarted()) {
            valueAnimator?.cancel()
        }
    }

    fun isShimmerStarted(): Boolean {
        return valueAnimator != null && valueAnimator?.isStarted == true
    }

    fun isShimmerRunning(): Boolean {
        return valueAnimator != null && valueAnimator?.isRunning == true
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        bounds?.let {
            rect.set(it)
            updateShader()
            maybeStartShimmer()
        }
    }

    fun maybeStartShimmer() {
        valueAnimator?.let { animator ->
            if (!animator.isStarted && shimmer != null && shimmer?.autoStart == true && callback != null) {
                animator.start()
            }
        }
    }

    fun setStaticAnimationProgress(value: Float) {
        if (value.compareTo(staticAnimationProgress) == 0 || (value < 0F && staticAnimationProgress < 0F)) {
            return
        }
        staticAnimationProgress = value.coerceAtMost(1F)
        invalidateSelf()
    }

    fun clearStaticAnimationProgress() {
        setStaticAnimationProgress(-1F)
    }

    override fun draw(canvas: Canvas) {
        if (shimmer == null || shimmerPaint.shader == null) {
            return
        }

        val tiltTan: Float = tan(Math.toRadians(shimmer?.tilt?.toDouble() ?: 0.0)).toFloat()
        val translateHeight = rect.height() + tiltTan * rect.width()
        val translateWidth = rect.width() + tiltTan * rect.height()
        var dx = 0F
        var dy = 0F
        var animatedValue = 0F

        animatedValue = if (staticAnimationProgress < 0F) {
            if (valueAnimator != null) valueAnimator?.animatedValue as Float else 0F
        } else {
            staticAnimationProgress
        }

        when (shimmer?.direction) {
            Shimmer.Direction.LEFT_TO_RIGHT -> {
                dx = offset(-translateWidth, translateWidth, animatedValue)
                dy = 0F
            }
            Shimmer.Direction.RIGHT_TO_LEFT -> {
                dx = offset(translateWidth, -translateWidth, animatedValue)
                dy = 0f
            }
            Shimmer.Direction.TOP_TO_BOTTOM -> {
                dx = 0F
                dy = offset(-translateHeight, translateHeight, animatedValue)
            }
            Shimmer.Direction.BOTTOM_TO_TOP -> {
                dx = 0F
                dy = offset(translateHeight, -translateHeight, animatedValue)
            }
        }

        shaderMatrix.reset()
        shaderMatrix.setRotate(shimmer?.tilt ?: 0F, rect.width() / 2F, rect.height() / 2F)
        shaderMatrix.preTranslate(dx, dy)
        shimmerPaint.shader.setLocalMatrix(shaderMatrix)
        canvas.drawRect(rect, shimmerPaint)
    }

    private fun offset(start: Float, end: Float, percent: Float) = start + (end - start) * percent

    override fun setAlpha(p0: Int) {
    }

    override fun setColorFilter(p0: ColorFilter?) {
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int {
        return if (shimmer != null && (shimmer?.clipToChildren == true || shimmer?.alphaShimmer == true)) PixelFormat.TRANSLUCENT else PixelFormat.OPAQUE
    }

    private fun updateValueAnimator() {
        shimmer?.let { shimmer ->
            val started: Boolean

            if (valueAnimator != null) {
                started = valueAnimator?.isStarted == true
                valueAnimator?.cancel()
                valueAnimator?.removeAllUpdateListeners()
            } else {
                started = false
            }


            valueAnimator =
                ValueAnimator.ofFloat(0F, 1F + (shimmer.repeatDelay / shimmer.animationDuration))
            valueAnimator?.interpolator = LinearInterpolator()
            valueAnimator?.repeatMode = shimmer.repeatMode
            valueAnimator?.startDelay = shimmer.startDelay
            valueAnimator?.repeatCount = shimmer.repeatCount
            valueAnimator?.duration = shimmer.animationDuration + shimmer.repeatDelay
            valueAnimator?.addUpdateListener(valueAnimatorUpdateListener)
            if (started) {
                valueAnimator?.start()
            }
        }
    }

    private fun updateShader() {
        val bounds = bounds
        val boundsWidth = bounds.width()
        val boundsHeight = bounds.height()

        if (boundsWidth == 0 || boundsHeight == 0 || shimmer == null) {
            return
        }

        val width = shimmer?.width(boundsWidth)
        val height = shimmer?.height(boundsHeight)

        var shader: Shader? = null

        when (shimmer?.shape) {
            Shimmer.Shape.LINEAR -> {
                val vertical =
                    shimmer?.direction == Shimmer.Direction.TOP_TO_BOTTOM || shimmer?.direction == Shimmer.Direction.BOTTOM_TO_TOP
                val endX = if (vertical) 0 else width
                val endY = if (vertical) height else 0

                shader = LinearGradient(
                    0F,
                    0F,
                    endX?.toFloat() ?: 0F,
                    endY?.toFloat() ?: 0F,
                    shimmer?.colors ?: intArrayOf(),
                    shimmer?.positions ?: floatArrayOf(),
                    Shader.TileMode.CLAMP)
            }
            Shimmer.Shape.RADIAL -> {
                val radius = ((width
                    ?: 0).coerceAtLeast(height ?: 0) / sqrt(2.0))
                shader = RadialGradient(width?.div(2F) ?: 0F,
                    height?.div(2F) ?: 0F,
                    radius.toFloat(),
                    shimmer?.colors ?: intArrayOf(),
                    shimmer?.positions ?: floatArrayOf(),
                    Shader.TileMode.CLAMP)
            }
        }
        shimmerPaint.shader = shader
    }
}