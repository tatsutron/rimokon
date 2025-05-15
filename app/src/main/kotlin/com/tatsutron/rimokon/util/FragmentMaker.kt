package com.tatsutron.rimokon.util

import android.os.Bundle
import com.tatsutron.rimokon.fragment.CreditsFragment
import com.tatsutron.rimokon.fragment.FavoriteListFragment
import com.tatsutron.rimokon.fragment.GameFragment
import com.tatsutron.rimokon.fragment.PlatformFragment
import com.tatsutron.rimokon.fragment.PlatformListFragment
import com.tatsutron.rimokon.fragment.RemoteFragment
import com.tatsutron.rimokon.fragment.ScanFragment
import com.tatsutron.rimokon.model.Platform

object FragmentMaker {

    const val KEY_PATH = "KEY_PATH"
    const val KEY_PLATFORM = "KEY_PLATFORM"

    fun credits() = CreditsFragment()

    fun favoriteList() = FavoriteListFragment()

    fun game(path: String) = GameFragment().apply {
        arguments = Bundle().apply {
            putString(KEY_PATH, path)
        }
    }

    fun platform(platform: Platform) = PlatformFragment().apply {
        arguments = Bundle().apply {
            putString(KEY_PLATFORM, platform.name)
        }
    }

    fun platformList() = PlatformListFragment()

    fun remote() = RemoteFragment()

    fun scan() = ScanFragment()
}
