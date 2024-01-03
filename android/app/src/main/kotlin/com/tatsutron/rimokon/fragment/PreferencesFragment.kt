package com.tatsutron.rimokon.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.tatsutron.rimokon.R
import com.tatsutron.rimokon.model.Platform
import com.tatsutron.rimokon.util.Persistence

class PreferencesFragment : BaseFragment() {

    override fun onBackPressed(): Boolean {
        FragmentStateAdapter.items.forEach {
            it.onConfigChanged()
        }
        return super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_close, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(
            R.layout.fragment_preferences,
            container,
            false,
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.close -> {
                FragmentStateAdapter.items.forEach {
                    it.onConfigChanged()
                }
                (requireActivity() as AppCompatActivity)
                    .supportFragmentManager.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.title = requireContext().getString(R.string.preferences)
        }
        view.findViewById<LinearLayout>(R.id.platforms).apply {
            Platform.values()
                .filter {
                    it != Platform.ARCADE
                }
                .forEach {
                    val switch = CheckBox(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, // width
                            LinearLayout.LayoutParams.WRAP_CONTENT, // height
                        )
                        text = it.displayName
                        setTextColor(requireContext().getColor(R.color.white))
                        isChecked = !Persistence.isHiddenFromPlatformList(it)
                        setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                Persistence.showInPlatformList(it)
                            } else {
                                Persistence.hideInPlatformList(it)
                            }
                        }
                    }
                    addView(switch)
                }
        }
        view.findViewById<LinearLayout>(R.id.global_search).apply {
            Platform.values()
                .forEach {
                    val switch = CheckBox(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, // width
                            LinearLayout.LayoutParams.WRAP_CONTENT, // height
                        )
                        text = it.displayName
                        setTextColor(requireContext().getColor(R.color.white))
                        isChecked = !Persistence.isHiddenFromGlobalSearch(it)
                        setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                Persistence.showInGlobalSearch(it)
                            } else {
                                Persistence.hideInGlobalSearch(it)
                            }
                        }
                    }
                    addView(switch)
                }
        }
    }
}
