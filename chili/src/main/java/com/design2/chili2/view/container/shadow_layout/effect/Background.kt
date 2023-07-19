package com.design2.chili2.view.container.shadow_layout.effect

import android.graphics.*
import com.design2.chili2.util.RoundedCornerMode
import com.design2.chili2.view.container.shadow_layout.utils.Util
import com.design2.chili2.view.container.shadow_layout.utils.ViewHelper

class Background : Effect {

    override val paint by lazy { Paint() }
    override val path by lazy { Path() }

    override var offsetLeft = 0f
    override var offsetTop = 0f
    override var offsetRight = 0f
    override var offsetBottom = 0f

    override var alpha = 0f

    private var backgroundColor = ViewHelper.NOT_SET_COLOR
    private var shadowInfo: Shadow? = null

    fun init(backgroundColor: Int) {
        this.backgroundColor = backgroundColor

        updatePaint()
    }

    override fun updateOffset(left: Float, top: Float, right: Float, bottom: Float) {
        this.offsetLeft = left
        this.offsetTop = top
        this.offsetRight = right
        this.offsetBottom = bottom
    }

    override fun updatePaint() {

        paint.apply {
            isAntiAlias = true
            color = backgroundColor
            style = Paint.Style.FILL_AND_STROKE

            if (Util.onSetAlphaFromColor(this@Background.alpha, backgroundColor)) {
                alpha = Util.getIntAlpha(this@Background.alpha)
            }
        }
    }

    override fun updatePath(radiusInfo: Radius?, roundedCornerMode: RoundedCornerMode?) {

        val rect = RectF(offsetLeft, offsetTop, offsetRight, offsetBottom)

        path.apply {

            reset()

            if (radiusInfo == null) {
                addRect(rect, Path.Direction.CW)
            } else {
                val height = (offsetBottom - offsetTop).toInt()
                addRoundRect(rect, radiusInfo.getRadiusArray(height), Path.Direction.CW)
            }

            close()
        }
    }

    override fun drawEffect(canvas: Canvas?) {
        canvas?.drawPath(path, paint)
    }

    override fun updateAlpha(alpha: Float) {
        this.alpha = alpha
        updatePaint()
    }

    fun setBackgroundColor(color: Int) {
        this.backgroundColor = color
        updatePaint()
    }
}