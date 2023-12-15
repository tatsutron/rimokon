package com.tatsutron.rimokon.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
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
        inflater.inflate(R.menu.menu_empty, menu)
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
                    val switch = SwitchCompat(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                        )
                        val (left, top, right, bottom) = arrayOf(128, 0, 128, 0)
                        setPadding(left, top, right, bottom)
                        text = it.displayName
                        setTextColor(requireContext().getColor(R.color.white))
                        isChecked = !Persistence.isHidden(it)
                        setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                Persistence.showPlatform(it)
                            } else {
                                Persistence.hidePlatform(it)
                            }
                        }
                    }
                    addView(switch)
                }
        }
    }
}
