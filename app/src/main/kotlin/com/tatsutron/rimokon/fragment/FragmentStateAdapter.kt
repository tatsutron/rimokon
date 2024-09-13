package com.tatsutron.rimokon.fragment

import androidx.fragment.app.FragmentActivity
import com.tatsutron.rimokon.model.Platform
import com.tatsutron.rimokon.util.FragmentMaker

class FragmentStateAdapter(
    activity: FragmentActivity,
) : androidx.viewpager2.adapter.FragmentStateAdapter(activity) {

    companion object {
        // Needs to be kept in sync with the navigation menu
        val items = listOf(
            FragmentMaker.platformList(Platform.Category.CONSOLE),
            FragmentMaker.platformList(Platform.Category.HANDHELD),
            FragmentMaker.favoriteList(),
        )
    }

    override fun createFragment(position: Int) = items[position]

    override fun getItemCount() = items.size
}
