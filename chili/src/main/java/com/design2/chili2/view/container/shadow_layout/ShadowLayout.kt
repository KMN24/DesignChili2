package com.design2.chili2.view.container.shadow_layout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.widget.LinearLayout
import com.design2.chili2.R
import com.design2.chili2.util.RoundedCornerMode
import com.design2.chili2.view.container.shadow_layout.effect.*
import com.design2.chili2.view.container.shadow_layout.utils.ViewHelper
import com.google.android.material.color.MaterialColors
import kotlin.math.abs

open class ShadowLayout : LinearLayout {

    lateinit var roundedCornerMode: RoundedCornerMode

    private val viewHelper by lazy { ViewHelper(context) }
    private val background by lazy { Background() }
    private val gradient by lazy { Gradient() }
    private val backgroundShadowList by lazy { mutableListOf<Shadow>() }
    private val foregroundShadowList by lazy { mutableListOf<Shadow>() }

    private var clipOutLine = false

    private var isInit = false
    private var defaultAlpha = 0f

    constructor(context: Context) : super(context) {
        init(context, null, 0, 0)
    }
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(context, attributeSet, 0, 0)
    }

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init(context, attributeSet, defStyleAttr, defStyleRes)
    }

    private fun init(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {

        orientation = VERTICAL

        if (attributeSet == null) {
            return
        }

        initAttrsLayout(context, attributeSet, defStyleAttr, defStyleRes)
    }

    private fun initAttrsLayout(context: Context, attributeSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int) {

        context.obtainStyledAttributes(attributeSet, R.styleable.ShadowLayout, defStyleAttr, defStyleRes).run {
            try {
                clipOutLine = getBoolean(R.styleable.ShadowLayout_clipToOutline, false)
                defaultAlpha = getFloat(R.styleable.ShadowLayout_alpha, 1f)

                val backgroundColor = MaterialColors.getColor(this@ShadowLayout, R.attr.ChiliCellViewBackground)

                background.init(backgroundColor)
                background.updateAlpha(defaultAlpha)

                viewHelper.radiusInfo = Radius().apply {
                    radius = getDimension(R.styleable.ShadowLayout_background_radius, 0f)

                    if (radius == 0f) {
                        topLeftRadius =
                            getDimension(R.styleable.ShadowLayout_background_top_left_radius, 0f)
                        topRightRadius =
                            getDimension(R.styleable.ShadowLayout_background_top_right_radius, 0f)
                        bottomLeftRadius =
                            getDimension(R.styleable.ShadowLayout_background_bottom_left_radius, 0f)
                        bottomRightRadius =
                            getDimension(R.styleable.ShadowLayout_background_bottom_right_radius, 0f)
                    }

                    smoothCorner = getBoolean(R.styleable.ShadowLayout_smooth_corner, false)
                    radiusWeight = getFloat(R.styleable.ShadowLayout_background_radius_weight, 1f)
                }

                backgroundShadowList.apply {

                    val shadow = Shadow().apply {
                        val shadowColor = getColor(
                            R.styleable.ShadowLayout_shadow_color,
                            ViewHelper.NOT_SET_COLOR
                        )
                        val shadowOffsetX = getDimension(R.styleable.ShadowLayout_shadow_offset_x, 0f)
                        val shadowOffsetY = getDimension(R.styleable.ShadowLayout_shadow_offset_y, 0f)
                        val shadowBlurSize = getDimension(R.styleable.ShadowLayout_shadow_blur, 0f)

                        init(true, shadowBlurSize, shadowOffsetX, shadowOffsetY, shadowColor)

                        updateAlpha(defaultAlpha)
                    }

                    add(shadow)

                    val shadows = viewHelper.parseShadowArray(
                        true,
                        getString(R.styleable.ShadowLayout_shadow_array)
                    )

                    if (shadows != null) {
                        shadows.forEach {
                            it.updateAlpha(defaultAlpha)
                        }
                        addAll(shadows)
                    }
                }

                foregroundShadowList.apply {

                    val shadow = Shadow().apply {
                        val shadowColor = getColor(
                            R.styleable.ShadowLayout_inner_shadow_color,
                            ViewHelper.NOT_SET_COLOR
                        )
                        val shadowBlurSize =
                            getDimension(R.styleable.ShadowLayout_inner_shadow_blur, 0f)

                        init(false, shadowBlurSize, 0f, 0f, shadowColor)

                        val strokeType = getString(R.styleable.ShadowLayout_inner_shadow_type)

                        if (!strokeType.isNullOrEmpty()) {
                            setShadowType(strokeType)
                        }
                    }

                    add(shadow)

                    val shadows = viewHelper.parseShadowArray(
                        false,
                        getString(R.styleable.ShadowLayout_inner_shadow_array)
                    )

                    if (shadows != null) {
                        shadows.forEach {
                            it.updateAlpha(defaultAlpha)
                        }
                        addAll(shadows)
                    }
                }

                //gradient Settings
                val gradientStartColor = getColor(
                    R.styleable.ShadowLayout_gradient_start_color,
                    ViewHelper.NOT_SET_COLOR
                )
                val gradientCenterColor = getColor(
                    R.styleable.ShadowLayout_gradient_center_color,
                    ViewHelper.NOT_SET_COLOR
                )
                val gradientEndColor = getColor(
                    R.styleable.ShadowLayout_gradient_end_color,
                    ViewHelper.NOT_SET_COLOR
                )
                val gradientOffsetX = getDimension(R.styleable.ShadowLayout_gradient_offset_x, 0f)
                val gradientOffsetY = getDimension(R.styleable.ShadowLayout_gradient_offset_y, 0f)
                val gradientAngle = getInt(R.styleable.ShadowLayout_gradient_angle, -1)

                val gradients = viewHelper.parseGradientArray(getString(R.styleable.ShadowLayout_gradient_array))
                val gradientPositions = viewHelper.parseGradientPositions(getString(R.styleable.ShadowLayout_gradient_positions))

                gradient.init(
                    gradientAngle, gradientStartColor, gradientCenterColor, gradientEndColor,
                    gradientOffsetX, gradientOffsetY, gradients?.toIntArray(), gradientPositions?.toFloatArray()
                )
            } finally {
                recycle()
                isInit = true
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {

        if (canvas == null)
            return

        with (viewHelper) {

            updateCanvas(canvas)

            backgroundShadowList.forEach {
                drawEffect(it, roundedCornerMode)
            }

            drawEffect(background)
            drawEffect(gradient)

            foregroundShadowList.forEach {
                drawEffect(it)
            }
        }

        if (clipOutLine) {
            canvas.clipPath(background.path)
        }

        super.dispatchDraw(canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val width = abs(right - left)
        val height = abs(bottom - top)

        with (viewHelper) {

            updateCanvas(canvas)

            backgroundShadowList.forEach {
                updateOffset(it, width, height)
            }

            updateOffset(background, width, height)
            updateOffset(gradient, width, height)

            foregroundShadowList.forEach {
                updateOffset(it, width, height)
            }

        }

        for (i in 0 until childCount) {
            getChildAt(i)?.alpha = defaultAlpha
        }
    }

    fun updateBackgroundColor(color: Int) {
        background.setBackgroundColor(color)
        invalidate()
    }

    fun updateRadius(radius: Float) {

        if (viewHelper.radiusInfo == null) {
            viewHelper.radiusInfo = Radius()
        }

        viewHelper.radiusInfo!!.updateRadius(radius)
        invalidate()
    }

    fun updateRadius(topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float) {
        if (viewHelper.radiusInfo == null) {
            viewHelper.radiusInfo = Radius()
        }

        viewHelper.radiusInfo!!.updateRadius(topLeft, topRight, bottomLeft, bottomRight)
        invalidate()
    }

    fun addBackgroundShadow(blurSize: Float, offsetX: Float, offsetY: Float, shadowColor: Int) {
        val shadow = Shadow().apply {
            init(true, blurSize, offsetX, offsetY, shadowColor)
        }
        backgroundShadowList.add(shadow)
        invalidate()
    }

    fun removeBackgroundShadowLast() {
        backgroundShadowList.removeLastOrNull()
        invalidate()
    }

    fun removeBackgroundShadowFirst() {
        backgroundShadowList.removeFirstOrNull()
        invalidate()
    }

    fun removeAllBackgroundShadows() {
        backgroundShadowList.clear()
        invalidate()
    }

    fun removeBackgroundShadow(position: Int) {
        backgroundShadowList.removeAt(position)
        invalidate()
    }

    fun updateBackgroundShadow(position: Int, shadow: Shadow) {
        backgroundShadowList[position] = shadow
        invalidate()
    }

    fun updateBackgroundShadow(
        position: Int,
        blurSize: Float,
        offsetX: Float,
        offsetY: Float,
        color: Int
    ) {
        backgroundShadowList[position].init(true, blurSize, offsetX, offsetY, color)
        invalidate()
    }

    fun updateBackgroundShadow(shadow: Shadow) {
        updateBackgroundShadow(0, shadow)
    }

    fun updateBackgroundShadow(blurSize: Float, offsetX: Float, offsetY: Float, color: Int) {
        updateBackgroundShadow(0, blurSize, offsetX, offsetY, color)
    }

    fun getBackgroundShadowCount(): Int {
        return backgroundShadowList.size
    }

    fun addForegroundShadow(blurSize: Float, shadowColor: Int) {
        val shadow = Shadow().apply {
            init(true, blurSize, 0f, 0f, shadowColor)
        }
        backgroundShadowList.add(shadow)
        invalidate()
    }

    fun removeForegroundShadowLast() {
        backgroundShadowList.removeLastOrNull()
        invalidate()
    }

    fun removeForegroundShadowFirst() {
        backgroundShadowList.removeFirstOrNull()
        invalidate()
    }

    fun removeAllForegroundShadows() {
        backgroundShadowList.clear()
        invalidate()
    }

    fun removeForegroundShadow(position: Int) {
        backgroundShadowList.removeAt(position)
        invalidate()
    }

    fun updateForegroundShadow(position: Int, shadow: Shadow) {
        backgroundShadowList[position] = shadow.apply {
            updateShadowOffsetX(0f)
            updateShadowOffsetY(0f)
        }
        invalidate()
    }

    fun updateForegroundShadow(position: Int, blurSize: Float, color: Int) {
        backgroundShadowList[position].init(false, blurSize, 0f, 0f, color)
        invalidate()
    }

    fun updateForegroundShadow(shadow: Shadow) {
        shadow.apply {
            updateShadowOffsetX(0f)
            updateShadowOffsetY(0f)
        }
        updateForegroundShadow(0, shadow)
    }

    fun updateForegroundShadow(blurSize: Float, color: Int) {
        updateForegroundShadow(0, blurSize, color)
    }

    fun getForegroundShadowCount(): Int {
        return backgroundShadowList.size
    }

    fun updateGradientColor(startColor: Int, centerColor: Int, endColor: Int) {
        gradient.updateGradientColor(startColor, centerColor, endColor)
        invalidate()
    }

    fun updateGradientColor(startColor: Int, endColor: Int) {
        gradient.updateGradientColor(startColor, endColor)
        invalidate()
    }

    fun updateGradientAngle(angle: Int) {
        gradient.updateGradientAngle(angle)
        invalidate()
    }

    fun updateLocalMatrix(matrix: Matrix?) {
        gradient.updateLocalMatrix(matrix)
        invalidate()
    }

    fun updateGradientOffsetX(offset: Float) {
        gradient.updateGradientOffsetX(offset)
        invalidate()
    }

    fun updateGradientOffsetY(offset: Float) {
        gradient.updateGradientOffsetY(offset)
        invalidate()
    }

    fun updateSmoothCornerStatus(enable: Boolean) {

        if (viewHelper.radiusInfo == null) {
            return
        }

        viewHelper.radiusInfo!!.smoothCorner = enable

        invalidate()
    }

    fun getGradientInfo(): Gradient {
        return gradient
    }

    fun getRadiusInfo(): Radius? {
        return viewHelper.radiusInfo
    }

    override fun setAlpha(alpha: Float) {

        if (!isInit)
            return

        defaultAlpha = alpha

        backgroundShadowList.forEach {
            it.updateAlpha(alpha)
        }

        background.updateAlpha(alpha)

        foregroundShadowList.forEach {
            it.updateAlpha(alpha)
        }

        invalidate()

        for (i in 0 until childCount) {
            getChildAt(i)?.alpha = alpha
        }
    }

    override fun getAlpha(): Float {
        return defaultAlpha
    }
}