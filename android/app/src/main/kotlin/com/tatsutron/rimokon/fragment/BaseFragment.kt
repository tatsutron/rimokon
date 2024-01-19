package com.tatsutron.rimokon.fragment

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    open fun onBackPressed() = false

    open fun onConfigChanged() {}

    // TODO Call this closeToolbar or similar?
    open fun onGameItemClicked() {}
}
