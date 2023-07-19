package com.design2.chili2.view.container.shadow_layout.effect

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.design2.chili2.util.RoundedCornerMode

interface Effect {

    val paint: Paint
    val path: Path

    var offsetLeft: Float
    var offsetTop: Float
    var offsetRight: Float
    var offsetBottom: Float

    var alpha: Float

    fun updateOffset(left: Float, top: Float, right: Float, bottom: Float)
    fun updatePaint()
    fun updatePath(radiusInfo: Radius?, roundedCornerMode: RoundedCornerMode?)
    fun drawEffect(canvas: Canvas?)

    fun updateAlpha(alpha: Float)
}