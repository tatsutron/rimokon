package com.tatsutron.rimokon.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.jcraft.jsch.JSchException
import com.l4digital.fastscroll.FastScrollRecyclerView
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.tatsutron.rimokon.R
import com.tatsutron.rimokon.model.Game
import com.tatsutron.rimokon.model.Platform
import com.tatsutron.rimokon.recycler.FolderItem
import com.tatsutron.rimokon.recycler.GameItem
import com.tatsutron.rimokon.recycler.GameListAdapter
import com.tatsutron.rimokon.util.*
import java.io.File
import java.util.Locale

class ArcadeFragment : FullMenuBaseFragment() {

    private lateinit var currentFolder: String
    private lateinit var recycler: FastScrollRecyclerView
    private lateinit var adapter: GameListAdapter
    private lateinit var syncAction: SpeedDialActionItem
    private lateinit var randomAction: SpeedDialActionItem
    private var searchTerm = ""

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
            R.layout.fragment_platform,
            container,
            false,
        )
    }

    override fun onResume() {
        super.onResume()
        if (Persistence.getGamesByPlatform(Platform.ARCADE).isEmpty()) {
            val platform = Platform.ARCADE
                .displayName
                ?.toLowerCase(Locale.getDefault())
            val context = requireContext()
            Dialog.confirmation(
                context = context,
                title = context.getString(R.string.no_games_found),
                message = context.getString(
                    R.string.would_you_like_to_sync_your_library,
                    platform,
                ),
                negativeButtonText = context.getString(R.string.not_now),
                positiveButtonText = context.getString(R.string.sync),
                callback = ::onSync
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentFolder = Platform.ARCADE.gamesPath!!
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(toolbar)
            toolbar.title = Platform.ARCADE.displayName
        }
        adapter = GameListAdapter(activity as Activity)
        recycler = view.findViewById<FastScrollRecyclerView>(R.id.recycler).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ArcadeFragment.adapter
        }
        setRecycler()
        setSpeedDialActionItems()
        setSpeedDial()
    }

    override fun onBackPressed() =
        if (currentFolder.length > Platform.ARCADE.gamesPath?.length!!) {
            currentFolder = File(currentFolder).parent!!
            setRecycler()
            true
        } else {
            super.onBackPressed()
        }

    @SuppressLint("NotifyDataSetChanged")
    private fun setRecycler() {
        adapter.itemList.clear()
        if (searchTerm.isBlank()) {
            val subfolder: Game.() -> String? = {
                val relativePath = path
                    .removePrefix("$currentFolder${File.separator}")
                val tokens = relativePath.split(File.separator)
                if (tokens.size <= 1) {
                    null
                } else {
                    tokens[0]
                }
            }
            val games = mutableListOf<Game>()
            val folders = mutableSetOf<String>()
            Persistence.getGamesByPlatform(Platform.ARCADE)
                .filter {
                    it.path.startsWith(currentFolder)
                }
                .forEach {
                    val folder = it.subfolder()
                    if (folder != null) {
                        folders.add(folder)
                    } else {
                        games.add(it)
                    }
                }
            val folderItems = folders
                .sorted()
                .map {
                    FolderItem(
                        name = it,
                        onClick = {
                            currentFolder = File(currentFolder, it).path
                            setRecycler()
                        },
                    )
                }
            val gameItems = games
                .map {
                    GameItem(
                        icon = Platform.ARCADE.media.icon,
                        game = it,
                        subscript = Platform.ARCADE.displayName ?: "",
                    )
                }
            val items = folderItems + gameItems
            adapter.itemList.addAll(items)
        } else {
            val items = Persistence.getGamesBySearch(searchTerm)
                .map {
                    GameItem(
                        icon = it.platform.media.icon,
                        game = it,
                        subscript = it.platform.displayName ?: "",
                    )
                }
            adapter.itemList.addAll(items)
        }
        adapter.notifyDataSetChanged()
    }

    private fun setSpeedDialActionItems() {
        val context = requireContext()
        syncAction = SpeedDialActionItem.Builder(R.id.sync, R.drawable.ic_sync)
            .setLabel(context.getString(R.string.sync))
            .setLabelBackgroundColor(context.getColorCompat(R.color.button_background))
            .setLabelColor(context.getColorCompat(R.color.button_label))
            .setFabBackgroundColor(context.getColorCompat(R.color.button_background))
            .setFabImageTintColor(context.getColorCompat(R.color.button_label))
            .create()
        randomAction = SpeedDialActionItem.Builder(R.id.random, R.drawable.ic_random)
            .setLabel(context.getString(R.string.random))
            .setLabelBackgroundColor(context.getColorCompat(R.color.button_background))
            .setLabelColor(context.getColorCompat(R.color.button_label))
            .setFabBackgroundColor(context.getColorCompat(R.color.button_background))
            .setFabImageTintColor(context.getColorCompat(R.color.button_label))
            .create()
    }

    private fun setSpeedDial() {
        view?.findViewById<SpeedDialView>(R.id.speed_dial)?.apply {
            clearActionItems()
            addActionItem(syncAction)
            if (adapter.itemList.count() > 1) {
                addActionItem(randomAction)
            }
            setOnActionSelectedListener(
                SpeedDialView.OnActionSelectedListener { actionItem ->
                    when (actionItem.id) {
                        R.id.random -> {
                            onRandom()
                            close()
                            return@OnActionSelectedListener true
                        }

                        R.id.sync -> {
                            onSync()
                            close()
                            return@OnActionSelectedListener true
                        }
                    }
                    false
                }
            )
        }
    }

    private fun onSync() {
        Navigator.showLoadingScreen()
        val activity = requireActivity()
        Coroutine.launch(
            activity = activity,
            run = {
                Util.syncPlatforms(requireActivity(), listOf(Platform.ARCADE))
            },
            success = {
                setRecycler()
                setSpeedDial()
            },
            failure = { throwable ->
                when (throwable) {
                    is JSchException ->
                        if (Persistence.host.isEmpty()) {
                            Dialog.enterIpAddress(
                                context = activity,
                                callback = ::onSync,
                            )
                        } else {
                            Dialog.connectionFailed(
                                context = activity,
                                callback = ::onSync,
                            )
                        }

                    else ->
                        Dialog.error(activity, throwable)
                }
            },
            finally = {
                Navigator.hideLoadingScreen()
            },
        )
    }

    private fun onRandom() {
        Navigator.showScreen(
            activity as AppCompatActivity,
            FragmentMaker.game(Persistence.getGamesByPlatform(Platform.ARCADE).random().path)
        )
    }
}
