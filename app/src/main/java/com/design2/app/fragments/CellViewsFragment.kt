package com.design2.app.fragments

import android.os.Bundle
import android.view.View
import androidx.core.text.parseAsHtml
import com.design2.app.MainActivity
import com.design2.app.base.BaseFragment
import com.design2.app.databinding.FragmentCellBinding
import com.design2.chili2.view.shimmer.startGroupShimmering
import com.design2.chili2.view.shimmer.stopGroupShimmering


class CellViewsFragment : BaseFragment<FragmentCellBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setUpHomeEnabled(true)
        vb.actionCell.setIsChevronVisible(false)
        vb.btnP1.setOnClickListener {
            vb.progress.setProgressPercent(100)
        }
        vb.btnP2.setOnClickListener {
            vb.progress.setProgressPercent(0)
        }
        vb.btnP4.setOnClickListener {
            vb.progress4.setProgressPercent(80)
        }
        vb.btnP5.setOnClickListener {
            vb.progress4.setProgressPercent(20)
        }
        vb.dcv1.setValueTextColor(resources.getColor(com.design2.chili2.R.color.red_1))

        vb.dcv1.setValue("100 <u>c</u>".parseAsHtml())
        vb.dcv1.setIconUrl("https://minio.o.kg/call-center/telega.png")
        vb.testAdditionalTextCellView.apply {
            setOnClickListener {
                setAdditionalText("0")
            }
        }
        val icon = "https://tal7aouy.gallerycdn.vsassets.io/extensions/tal7aouy/icons/3.6.3/1679578385939/Microsoft.VisualStudio.Services.Icons.Default"
        vb.multiiconedCellView.setIcons(arrayListOf(icon, icon, icon))
    }

    override fun inflateViewBinging(): FragmentCellBinding {
        return FragmentCellBinding.inflate(layoutInflater)
    }

    override fun startShimmering() {
        super.startShimmering()
        vb.root.startGroupShimmering()
    }

    override fun stopShimmering() {
        super.stopShimmering()
        vb.root.stopGroupShimmering()
    }
}