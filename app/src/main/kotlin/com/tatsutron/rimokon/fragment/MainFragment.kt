package com.tatsutron.rimokon.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jcraft.jsch.JSchException
import com.tatsutron.rimokon.R
import com.tatsutron.rimokon.model.Platform
import com.tatsutron.rimokon.util.Coroutine
import com.tatsutron.rimokon.util.Dialog
import com.tatsutron.rimokon.util.FragmentMaker
import com.tatsutron.rimokon.util.Navigator
import com.tatsutron.rimokon.util.Persistence
import com.tatsutron.rimokon.util.Util

class MainFragment : BaseFragment() {

    companion object {
        var searchItem: MenuItem? = null
        var searchTerm = ""
    }

    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_search_and_options, menu)
        searchItem = menu.findItem(R.id.search)
        (searchItem?.actionView as SearchView).apply {
            maxWidth = Integer.MAX_VALUE
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String) = true
                override fun onQueryTextChange(newText: String): Boolean {
                    searchTerm = newText
                    FragmentStateAdapter.items[viewPager.currentItem].setRecycler()
                    return true
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {

        R.id.sync_full_library -> {
            onSyncGameLibrary()
            true
        }

        R.id.favorites -> {
            Navigator.showScreen(
                activity as AppCompatActivity,
                FragmentMaker.favoriteList(),
            )
            true
        }

        R.id.scan_qr_code -> {
            Navigator.showScreen(
                activity as AppCompatActivity,
                FragmentMaker.scan(),
            )
            true
        }

        R.id.credits -> {
            Navigator.showScreen(
                activity as AppCompatActivity,
                FragmentMaker.credits(),
            )
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(toolbar)
        }
        viewPager = view.findViewById<ViewPager2>(R.id.view_pager).apply {
            adapter = FragmentStateAdapter(requireActivity())
            isUserInputEnabled = false
        }
        view.findViewById<BottomNavigationView>(R.id.bottom_navigation).apply {
            setOnItemSelectedListener { item ->
                for (i in 0 until menu.size()) {
                    if (menu.getItem(i) == item) {
                        (searchItem?.actionView as SearchView).apply {
                            onActionViewCollapsed()
                            setQuery("", false)
                            clearFocus()
                        }
                        viewPager.setCurrentItem(i, false)
                        FragmentStateAdapter.items[i].setRecycler()
                    }
                }
                true
            }
        }
    }

    private fun onSyncGameLibrary() {
        Navigator.showLoadingScreen()
        val activity = activity as Activity
        Coroutine.launch(
            activity = activity,
            run = {
                Util.syncPlatforms(requireActivity(), Platform.values().toList())
            },
            failure = { throwable ->
                when (throwable) {
                    is JSchException -> if (Persistence.host.isEmpty()) {
                        Dialog.enterIpAddress(
                            context = activity,
                            callback = ::onSyncGameLibrary,
                        )
                    } else {
                        Dialog.connectionFailed(
                            context = activity,
                            callback = ::onSyncGameLibrary,
                        )
                    }

                    else -> Dialog.error(activity, throwable)
                }
            },
            finally = {
                Navigator.hideLoadingScreen()
            },
        )
    }
}
