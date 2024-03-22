package com.app.shimmertextview

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.RectF
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.IntDef
import androidx.annotation.Px
import kotlin.math.roundToInt
import kotlin.math.sin


class Shimmer {
    companion object {
        private const val COMPONENT_COUNT = 4
    }

    val positions: FloatArray = FloatArray(COMPONENT_COUNT)
    val colors: IntArray = IntArray(COMPONENT_COUNT)
    private val bounds = RectF()

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(Shape.LINEAR, Shape.RADIAL)
    annotation class Shape {
        companion object {
            const val LINEAR = 0
            const val RADIAL = 1
        }
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(Direction.LEFT_TO_RIGHT,
        Direction.TOP_TO_BOTTOM,
        Direction.RIGHT_TO_LEFT,
        Direction.BOTTOM_TO_TOP)
    annotation class Direction {
        companion object {
            const val LEFT_TO_RIGHT = 0
            const val TOP_TO_BOTTOM = 1
            const val RIGHT_TO_LEFT = 2
            const val BOTTOM_TO_TOP = 3
        }
    }

    @Direction
    var direction = Direction.LEFT_TO_RIGHT

    @ColorInt
    var highLightColor = Color.WHITE

    @ColorInt
    var baseColor = 0x4Cffffff

    @Shape
    var shape = Shape.LINEAR
    var fixedWidth = 0
    var fixedHeight = 0

    var widthRatio = 1F
    var heightRatio = 1F
    var intensity = 0F
    var dropOff = 0.5F
    var tilt = 20F
    var isColored = false

    var clipToChildren = true
    var autoStart = true
    var alphaShimmer = true

    var repeatCount = ValueAnimator.INFINITE
    var repeatMode = ValueAnimator.RESTART
    var animationDuration = 1000L
    var repeatDelay: Long = 0
    var startDelay: Long = 0

    fun width(width: Int): Int {
        return if (fixedWidth > 0) fixedWidth else (widthRatio * width).roundToInt()
    }

    fun height(height: Int): Int {
        return if (fixedHeight > 0) fixedHeight else (heightRatio * height).roundToInt()
    }

    private fun updateColor() {
        when (shape) {
            Shape.LINEAR -> {
                colors[0] = baseColor
                colors[1] = highLightColor
                colors[2] = highLightColor
                colors[3] = baseColor
            }
            Shape.RADIAL -> {
                colors[0] = highLightColor
                colors[1] = highLightColor
                colors[2] = baseColor
                colors[3] = baseColor
            }
        }
    }

    private fun updatePosition() {
        when (shape) {
            Shape.LINEAR -> {
                positions[0] = ((1F - intensity - dropOff) / 2F).coerceAtLeast(0F)
                positions[1] = ((1F - intensity - 0.001F) / 2F).coerceAtLeast(0F)
                positions[2] = ((1F + intensity + 0.001F) / 2F).coerceAtMost(1F)
                positions[3] = ((1F + intensity + dropOff) / 2F).coerceAtMost(1F)
            }
            Shape.RADIAL -> {
                positions[0] = 0F
                positions[1] = intensity.coerceAtMost(1F)
                positions[2] = (intensity + dropOff).coerceAtMost(1F)
                positions[3] = 1F
            }
        }
    }

    private fun updateBounds(viewWidth: Int, viewHeight: Int) {
        val magnitude = viewWidth.coerceAtLeast(viewHeight)
        val rad = Math.PI / 2F - Math.toRadians((tilt % 90F).toDouble())
        val hyp = magnitude / sin(rad)
        val padding = 3 * ((hyp - magnitude) / 2F).roundToInt()
        bounds.set(-padding.toFloat(),
            -padding.toFloat(),
            (width(viewWidth) + padding).toFloat(),
            (height(viewHeight) + padding).toFloat())
    }

    abstract class Builder<T : Builder<T>> {
        val shimmer = Shimmer()

        protected abstract fun getThis(): T

        fun consumeAttributes(context: Context, attrs: AttributeSet): T {
            val typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.ShimmerTextView, 0, 0)
            return consumeAttributes(typedArray)
        }

        open fun consumeAttributes(typedArray: TypedArray): T {
            if (typedArray.hasValue(R.styleable.ShimmerTextView_shimmer_clip_to_children)) {
                setClipToChildren(typedArray.getBoolean(R.styleable.ShimmerTextView_shimmer_clip_to_children,
                    shimmer.clipToChildren))
            }
            if (typedArray.hasValue(R.styleable.ShimmerTextView_shimmer_auto_start)) {
                setAutoStart(typedArray.getBoolean(R.styleable.ShimmerTextView_shimmer_auto_start,
                    shimmer.autoStart))
            }
            if (typedArray.hasValue(R.styleable.ShimmerTextView_shimmer_base_alpha)) {
                setBaseAlpha(typedArray.getFloat(R.styleable.ShimmerTextView_shimmer_base_alpha,
                    0.3F))
            }
            if (typedArray.hasValue(R.styleable.ShimmerTextView_shimmer_highlight_alpha)) {
                setHighlightAlpha(typedArray.getFloat(R.styleable.ShimmerTextView_shimmer_highlight_alpha,
                    1F))
            }
            if (typedArray.hasValue(R.styleable.ShimmerTextView_shimmer_duration)) {
                setDuration(typedArray.getInt(R.styleable.ShimmerTextView_shimmer_duration,
                    shimmer.animationDuration.toInt()).toLong())
            }
            if (typedArray.hasValue(R.styleable.ShimmerTextView_shimmer_repeat_count)) {
                setRepeatCount(typedArray.getInt(R.styleable.ShimmerTextView_shimmer_repeat_count,
                    shimmer.repeatCount))
            }
            if (typedArray.hasValue(R.styleable.ShimmerTextView_shimmer_repeat_delay)) {
                setRepeatDelay(typedArray.getInt(R.styleable.ShimmerTextView_shimmer_repeat_delay,
                    shimmer.repeatDelay.toInt()).toLong())
            }
            if (typedArray.hasValue(R.styleable.ShimmerTextView_shimmer_repeat_mode)) {
                setRepeatMode(typedArray.getInt(R.styleable.ShimmerTextView_shimmer_repeat_mode,
                    shimmer.repeatMode))
            }
            if (typedArray.hasValue(R.styleable.ShimmerTextView_shimmer_start_delay)) {
                setStartDelay(typedArray.getInt(R.styleable.ShimmerTextView_shimmer_start_delay,
                    shimmer.startDelay.toInt()).toLong())
            }
            if (typedArray.hasValue(R.styleable.ShimmerTextView_shimmer_direction)) {
                when (typedArray.getInt(R.styleable.ShimmerTextView_shimmer_direction,
                    shimmer.direction)) {
                    Direction.LEFT_TO_RIGHT -> {
                        setDirection(Direction.LEFT_TO_RIGHT)
                    }
                    Direction.TOP_TO_BOTTOM -> {
                        setDirection(Direction.TOP_TO_BOTTOM)
                    }
                    Direction.RIGHT_TO_LEFT -> {
                        setDirection(Direction.RIGHT_TO_LEFT)
                    }
                    Direction.BOTTOM_TO_TOP -> {
                        setDirection(Direction.BOTTOM_TO_TOP)
                    }
                }
            }
            if (typedArray.hasValue(R.styleable.ShimmerTextView_shimmer_shape)) {
                when (typedArray.getInt(R.styleable.ShimmerTextView_shimmer_shape, shimmer.shape)) {
                    Shape.LINEAR -> {
                        setShape(Shape.LINEAR)
                    }
                    Shape.RADIAL -> {
                        setShape(Shape.RADIAL)
                    }
                }
            }
            if (typedArray.hasValue(R.styleable.ShimmerTextView_shimmer_dropoff)) {
                setDropOff(typedArray.getFloat(R.styleable.ShimmerTextView_shimmer_dropoff,
                    shimmer.dropOff))
            }
            if (typedArray.hasValue(R.styleable.ShimmerTextView_shimmer_fixed_width)) {
                setFixedWidth(typedArray.getDimensionPixelSize(R.styleable.ShimmerTextView_shimmer_fixed_width,
                    shimmer.fixedWidth))
            }
            if (typedArray.hasValue(R.styleable.ShimmerTextView_shimmer_fixed_height)) {
                setFixedHeight(typedArray.getDimensionPixelSize(R.styleable.ShimmerTextView_shimmer_fixed_height,
                    shimmer.fixedHeight))
            }
            if (typedArray.hasValue(R.styleable.ShimmerTextView_shimmer_intensity)) {
                setIntensity(typedArray.getFloat(R.styleable.ShimmerTextView_shimmer_intensity,
                    shimmer.intensity))
            }
            if (typedArray.hasValue(R.styleable.ShimmerTextView_shimmer_width_ratio)) {
                setWidthRatio(typedArray.getFloat(R.styleable.ShimmerTextView_shimmer_width_ratio,
                    shimmer.widthRatio))
            }
            if (typedArray.hasValue(R.styleable.ShimmerTextView_shimmer_height_ratio)) {
                setHeightRatio(typedArray.getFloat(R.styleable.ShimmerTextView_shimmer_height_ratio,
                    shimmer.heightRatio))
            }
            if (typedArray.hasValue(R.styleable.ShimmerTextView_shimmer_tilt)) {
                setTilt(typedArray.getFloat(R.styleable.ShimmerTextView_shimmer_tilt, shimmer.tilt))
            }

            if (typedArray.hasValue(R.styleable.ShimmerTextView_shimmer_colored)) {
                setColored(
                    typedArray.getBoolean(R.styleable.ShimmerTextView_shimmer_colored, shimmer.isColored)
                )
            }

            return getThis()
        }

        fun copyFrom(other: Shimmer): T {
            setClipToChildren(other.clipToChildren)
            setAutoStart(other.autoStart)
            setRepeatCount(other.repeatCount)
            setDirection(other.direction)
            setShape(other.shape)
            setTilt(other.tilt)
            setIntensity(other.intensity)
            setFixedWidth(other.fixedWidth)
            setFixedHeight(other.fixedHeight)
            setWidthRatio(other.widthRatio)
            setHeightRatio(other.heightRatio)
            setDropOff(other.dropOff)
            setRepeatMode(other.repeatMode)
            setRepeatDelay(other.repeatDelay)
            setStartDelay(other.startDelay)
            setDuration(other.animationDuration)
            shimmer.baseColor = other.baseColor
            shimmer.highLightColor = other.highLightColor
            return getThis()
        }

        fun setClipToChildren(status: Boolean): T {
            shimmer.clipToChildren = status
            return getThis()
        }

        fun setAutoStart(status: Boolean): T {
            shimmer.autoStart = status
            return getThis()
        }

        fun setBaseAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float): T {
            val intAlpha: Int = (clamp(0F, 1F, alpha) * 255F).toInt()
            shimmer.baseColor = intAlpha shl 24 or (shimmer.baseColor and 0x00FFFFFF)
            return getThis()
        }

        private fun clamp(min: Float, max: Float, value: Float): Float {
            return max.coerceAtMost(min.coerceAtLeast(value))
        }

        fun setHighlightAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float): T {
            val intAlpha = (clamp(0F, 1F, alpha) * 255F).toInt()
            shimmer.highLightColor = intAlpha shl 24 or (shimmer.highLightColor and 0x00FFFFFF)
            return getThis()
        }

        fun setDuration(millis: Long): T {
            if (millis < 0) {
                throw IllegalArgumentException("Given a negative duration: $millis")
            }
            shimmer.animationDuration = millis
            return getThis()
        }

        fun setRepeatCount(repeatCount: Int): T {
            shimmer.repeatCount = repeatCount
            return getThis()
        }

        fun setRepeatDelay(millis: Long): T {
            if (millis < 0) {
                throw IllegalArgumentException("Given a negative repeat delay")
            }
            shimmer.repeatDelay = millis
            return getThis()
        }

        fun setStartDelay(millis: Long): T {
            if (millis < 0) {
                throw IllegalArgumentException("Given a negative start delay: $millis")
            }
            shimmer.startDelay = millis
            return getThis()
        }

        fun setRepeatMode(mode: Int): T {
            shimmer.repeatMode = mode
            return getThis()
        }

        fun setDirection(@Direction direction: Int): T {
            shimmer.direction = direction
            return getThis()
        }

        fun setShape(@Shape shape: Int): T {
            shimmer.shape = shape
            return getThis()
        }

        fun setDropOff(dropOff: Float): T {
            if (dropOff < 0F) {
                throw IllegalArgumentException("Given invalid drop off value: $dropOff")
            }
            shimmer.dropOff = dropOff
            return getThis()
        }

        fun setFixedWidth(@Px fixedWidth: Int): T {
            if (fixedWidth < 0) {
                throw IllegalArgumentException("Given invalid width: $fixedWidth")
            }
            shimmer.fixedWidth = fixedWidth
            return getThis()
        }

        fun setFixedHeight(@Px fixedHeight: Int): T {
            if (fixedHeight < 0) {
                throw IllegalArgumentException("Given invalid height: $fixedHeight")
            }
            shimmer.fixedHeight = fixedHeight
            return getThis()
        }

        fun setIntensity(intensity: Float): T {
            if (intensity < 0F) {
                throw IllegalArgumentException("Given invalid intensity value: $intensity")
            }
            shimmer.intensity = intensity
            return getThis()
        }

        fun setWidthRatio(widthRatio: Float): T {
            if (widthRatio < 0F) {
                throw IllegalArgumentException("Given invalid width ratio: $widthRatio")
            }
            shimmer.widthRatio = widthRatio
            return getThis()
        }

        fun setHeightRatio(heightRatio: Float): T {
            if (heightRatio < 0F) {
                throw IllegalArgumentException("Given invalid height ratio: $heightRatio")
            }
            shimmer.heightRatio = heightRatio
            return getThis()
        }

        fun setTilt(tilt: Float): T {
            shimmer.tilt = tilt
            return getThis()
        }

        fun setColored(isColored: Boolean): T {
            shimmer.isColored = isColored
            return getThis()
        }

        fun build(): Shimmer {
            shimmer.updateColor()
            shimmer.updatePosition()
            return shimmer
        }
    }

    class AlphaHighlightBuilder : Builder<AlphaHighlightBuilder>() {
        override fun getThis(): AlphaHighlightBuilder {
            return this
        }

        init {
            shimmer.alphaShimmer = true
        }
    }

    class ColorHighlightBuilder : Builder<ColorHighlightBuilder>() {
        /** Sets the highlight color for the shimmer.  */
        fun setHighlightColor(@ColorInt color: Int): ColorHighlightBuilder {
            shimmer.highLightColor = color
            return getThis()
        }

        /** Sets the base color for the shimmer.  */
        fun setBaseColor(@ColorInt color: Int): ColorHighlightBuilder {
            shimmer.baseColor = color
            return getThis()
        }

        override fun consumeAttributes(typedArray: TypedArray): ColorHighlightBuilder {
            super.consumeAttributes(typedArray)
            if (typedArray.hasValue(R.styleable.ShimmerTextView_shimmer_base_color)) {
                setBaseColor(
                    typedArray.getColor(R.styleable.ShimmerTextView_shimmer_base_color,
                        shimmer.baseColor))
            }
            if (typedArray.hasValue(R.styleable.ShimmerTextView_shimmer_highlight_color)) {
                setHighlightColor(
                    typedArray.getColor(
                        R.styleable.ShimmerTextView_shimmer_highlight_color,
                        shimmer.highLightColor))
            }
            return getThis()
        }

        override fun getThis(): ColorHighlightBuilder {
            return this
        }

        init {
            shimmer.alphaShimmer = false
        }
    }

}