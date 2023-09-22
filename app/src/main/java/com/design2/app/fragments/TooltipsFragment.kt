package com.design2.app.fragments

import android.os.Bundle
import android.view.View
import com.design2.app.MainActivity
import com.design2.app.base.BaseFragment
import com.design2.app.databinding.FragmentTooltipsBinding
import com.design2.chili2.view.tooltip.TooltipView
import com.design2.chili2.view.tooltip.createTooltipView

class TooltipsFragment : BaseFragment<FragmentTooltipsBinding>() {

    private var tooltipView: TooltipView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setUpHomeEnabled(true)
        initTooltip()
        initButton()
    }

    private fun initTooltip() {
        tooltipView = requireContext().createTooltipView(
            anchorView = vb.inputField,
            title = "Получи бонус 10 ГБ! (22)",
            subtitle = "При пополнении баланса на 120 с (32)"
        )
    }

    private fun initButton() {
        with(vb.inputField) {
            setOnClickListener { tooltipView?.show() }
            performClick()
        }
    }

    override fun inflateViewBinging(): FragmentTooltipsBinding {
        return FragmentTooltipsBinding.inflate(layoutInflater)
    }
}