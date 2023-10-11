package com.tatsutron.rimokon.fragment

import androidx.fragment.app.FragmentActivity
import com.tatsutron.rimokon.model.Platform
import com.tatsutron.rimokon.util.FragmentMaker

class FragmentStateAdapter(
    activity: FragmentActivity,
) : androidx.viewpager2.adapter.FragmentStateAdapter(activity) {

    // Needs to be kept in sync with the navigation menu
    private val items = listOf(
        FragmentMaker.platformList(Platform.Category.CONSOLE),
        FragmentMaker.platform(Platform.ARCADE),
        FragmentMaker.platformList(Platform.Category.HANDHELD),
        FragmentMaker.platformList(Platform.Category.COMPUTER),
    )

    override fun getItemCount() = items.size

    override fun createFragment(position: Int) = items[position]
}
