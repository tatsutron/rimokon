package com.tatsutron.rimokon.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.l4digital.fastscroll.FastScrollRecyclerView
import com.tatsutron.rimokon.R
import com.tatsutron.rimokon.model.Platform
import com.tatsutron.rimokon.recycler.GameItem
import com.tatsutron.rimokon.recycler.GameListAdapter
import com.tatsutron.rimokon.recycler.PlatformItem
import com.tatsutron.rimokon.recycler.PlatformListAdapter
import com.tatsutron.rimokon.util.Coroutine
import com.tatsutron.rimokon.util.FragmentMaker
import com.tatsutron.rimokon.util.Navigator
import com.tatsutron.rimokon.util.Persistence
import com.tatsutron.rimokon.util.Util

class PlatformListFragment : BaseFragment() {

    private lateinit var platformCategory: Platform.Category
    private lateinit var recycler: FastScrollRecyclerView
    private lateinit var platformListAdapter: PlatformListAdapter
    private lateinit var gameListAdapter: GameListAdapter
    private var searchTerm = ""

    override fun onConfigChanged() {
        setRecycler()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_search_and_options, menu)
        (menu.findItem(R.id.search).actionView as? SearchView)?.apply {
            maxWidth = Integer.MAX_VALUE
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String) = true
                override fun onQueryTextChange(newText: String): Boolean {
                    searchTerm = newText
                    setRecycler()
                    return true
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(
            R.layout.fragment_platform_list,
            container,
            false,
        )
    }

    @SuppressLint("CheckResult")
    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.set_ip_address -> {
                MaterialDialog(requireContext()).show {
                    title(
                        res = R.string.enter_mister_ip_address,
                    )
                    negativeButton(R.string.cancel)
                    positiveButton(R.string.ok)
                    input(
                        inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS,
                        prefill = Persistence.host,
                        callback = { _, text ->
                            Persistence.host = text.toString()
                            Navigator.showLoadingScreen()
                            Util.deployAssets(
                                activity = requireActivity(),
                                callback = {
                                    Navigator.hideLoadingScreen()
                                },
                            )
                        },
                    )
                }
                true
            }

            R.id.sync_library -> {
                Navigator.showLoadingScreen()
                Coroutine.launch(
                    activity = activity as Activity,
                    run = {
                        Util.syncPlatforms(Platform.values().toList())
                    },
                    finally = {
                        Navigator.hideLoadingScreen()
                    }
                )
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

            R.id.preferences -> {
                Navigator.showScreen(
                    activity as AppCompatActivity,
                    FragmentMaker.preferences(),
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
        platformCategory = Platform.Category.valueOf(
            arguments?.getString(FragmentMaker.KEY_PLATFORM_CATEGORY)!!,
        )
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(toolbar)
            toolbar.title = platformCategory.displayName
        }
        platformListAdapter = PlatformListAdapter(activity as Activity)
        gameListAdapter = GameListAdapter(activity as Activity)
        recycler = view.findViewById<FastScrollRecyclerView>(R.id.recycler)
            .apply {
                layoutManager = LinearLayoutManager(context)
                adapter = this@PlatformListFragment.platformListAdapter
            }
        setRecycler()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setRecycler() {
        if (!this::recycler.isInitialized) {
            return
        }
        if (searchTerm.isEmpty()) {
            recycler.adapter = platformListAdapter
            val items = Platform.values()
                .filter {
                    it.category == platformCategory &&
                            !Persistence.isHiddenFromPlatformList(it)
                }
                .map {
                    PlatformItem(it)
                }
            platformListAdapter.apply {
                itemList.clear()
                itemList.addAll(items)
                notifyDataSetChanged()
            }
        } else {
            recycler.adapter = gameListAdapter
            val items = Persistence.getGamesBySearch(searchTerm)
                .filter {
                    !Persistence.isHiddenFromGlobalSearch(it.platform)
                }
                .map {
                    GameItem(
                        icon = it.platform.media.icon,
                        game = it,
                        subscript = it.platform.displayName ?: "",
                    )
                }
            gameListAdapter.apply {
                itemList.clear()
                itemList.addAll(items)
                notifyDataSetChanged()
            }
        }
    }
}
