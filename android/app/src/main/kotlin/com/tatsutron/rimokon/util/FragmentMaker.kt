package com.tatsutron.rimokon.util

import android.os.Bundle
import com.tatsutron.rimokon.fragment.*
import com.tatsutron.rimokon.model.Platform

object FragmentMaker {

    const val KEY_PATH = "KEY_PATH"
    const val KEY_PLATFORM = "KEY_PLATFORM"
    const val KEY_PLATFORM_CATEGORY = "KEY_PLATFORM_CATEGORY"
    const val KEY_URL = "KEY_URL"

    fun arcade() = ArcadeFragment()

    fun credits() = CreditsFragment()

    fun favoriteList() = FavoriteListFragment()

    fun game(path: String) = GameFragment().apply {
        arguments = Bundle().apply {
            putString(KEY_PATH, path)
        }
    }

    fun image(url: String?) = ImageFragment().apply {
        arguments = Bundle().apply {
            putString(KEY_URL, url)
        }
    }

    fun platform(platform: Platform) = PlatformFragment().apply {
        arguments = Bundle().apply {
            putString(KEY_PLATFORM, platform.name)
        }
    }

    fun platformList(category: Platform.Category) = PlatformListFragment().apply {
        arguments = Bundle().apply {
            putString(KEY_PLATFORM_CATEGORY, category.name)
        }
    }

    fun preferences() = PreferencesFragment()

    fun scan() = ScanFragment()
}
