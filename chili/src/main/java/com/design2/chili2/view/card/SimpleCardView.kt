package com.design2.chili2.view.card

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.design2.chili2.R

class SimpleCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.simpleCardViewDefaultStyle,
    defStyleRes: Int = R.style.Chili_CardViewStyle_SimpleCardView
): BaseCardView(context, attrs, defStyleAttr, defStyleRes) {

    private lateinit var view: SimpleCardViewViewVariables

    override val styleableAttrRes: IntArray = R.styleable.SimpleCardView

    override val rootContainer: View
        get() = view.root

    override fun inflateView(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.chili_view_simple_card, this, true)
        this.view = SimpleCardViewViewVariables(
            tvTitle = view.findViewById(R.id.tv_title),
            ivIcon = view.findViewById(R.id.iv_icon),
            root = view.findViewById(R.id.root_view)
        )
    }

    init { initView(context, attrs, defStyleAttr, defStyleRes) }


    override fun TypedArray.obtainAttributes() {
        setTitle(getText(R.styleable.SimpleCardView_title))
        getResourceId(R.styleable.SimpleCardView_titleTextAppearance, -1)
            .takeIf { it != -1 }?.let { setTitleTextAppearance(it) }
        getResourceId(R.styleable.SimpleCardView_icon, -1)
            .takeIf { it != -1 }?.let { setIcon(it) }
    }

    fun setTitle(charSequence: CharSequence) {
        view.tvTitle.text = charSequence
    }

    fun setTitle(resId: Int) {
        view.tvTitle.setText(resId)
    }

    fun setTitleTextAppearance(resId: Int) {
        view.tvTitle.setTextAppearance(resId)
    }

    fun setIcon(resId: Int) {
        view.ivIcon.setImageResource(resId)
    }

    fun setIcon(drawable: Drawable) {
        view.ivIcon.setImageDrawable(drawable)
    }
}

data class SimpleCardViewViewVariables(
    val tvTitle: TextView,
    val ivIcon: ImageView,
    val root: LinearLayout
)