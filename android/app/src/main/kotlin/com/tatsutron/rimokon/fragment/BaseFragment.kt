package com.tatsutron.rimokon.fragment

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    open fun clearSearch() {}

    open fun onBackPressed() = false
}
