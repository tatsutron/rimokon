package com.tatsutron.rimokon.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
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
import com.tatsutron.rimokon.recycler.GalleryAdapter
import com.tatsutron.rimokon.recycler.GalleryItem
import com.tatsutron.rimokon.recycler.GameItem
import com.tatsutron.rimokon.recycler.GameListAdapter
import com.tatsutron.rimokon.util.*
import java.io.File

class PlatformFragment : BaseFragment() {

    private lateinit var platform: Platform
    private lateinit var currentFolder: String
    private lateinit var recycler: FastScrollRecyclerView
    private lateinit var gameListAdapter: GameListAdapter
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var syncAction: SpeedDialActionItem
    private lateinit var randomAction: SpeedDialActionItem
    private lateinit var galleryViewAction: SpeedDialActionItem
    private lateinit var listViewAction: SpeedDialActionItem
    private var inGallery = false

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
            R.layout.fragment_platform,
            container,
            false,
        )
    }

    override fun onResume() {
        super.onResume()
        if (Persistence.getGamesByPlatform(platform).isEmpty()) {
            val context = requireContext()
            Dialog.confirmation(
                context = context,
                title = context.getString(R.string.no_games_found),
                message = context.getString(
                    R.string.would_you_like_to_sync_your_library,
                    platform.displayName,
                ),
                negativeButtonText = context.getString(R.string.not_now),
                positiveButtonText = context.getString(R.string.sync),
                callback = ::onSync
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        platform = Platform.valueOf(
            arguments?.getString(FragmentMaker.KEY_PLATFORM)!!,
        )
        currentFolder = platform.gamesPath!!
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(toolbar)
            toolbar.title = platform.displayName
        }
        gameListAdapter = GameListAdapter(activity as Activity)
        galleryAdapter = GalleryAdapter(activity as Activity)
        recycler = view.findViewById<FastScrollRecyclerView>(R.id.recycler).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@PlatformFragment.gameListAdapter
        }
        setRecycler()
        setSpeedDialActionItems()
        setSpeedDial()
    }

    override fun onBackPressed() =
        if (!inGallery && currentFolder.length > platform.gamesPath?.length!!) {
            currentFolder = File(currentFolder).parent!!
            setRecycler()
            true
        } else {
            super.onBackPressed()
        }

    @SuppressLint("NotifyDataSetChanged")
    private fun setRecycler() {
        if (inGallery) {
            recycler.adapter = galleryAdapter
            val items = Persistence.getGamesByHasArtwork(platform).map {
                GalleryItem(it)
            }
            galleryAdapter.apply {
                itemList.clear()
                itemList.addAll(items)
                notifyDataSetChanged()
            }
        } else {
            recycler.adapter = gameListAdapter
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
            Persistence.getGamesByPlatform(platform)
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
                        icon = platform.media.icon,
                        game = it,
                        subscript = platform.displayName ?: "",
                    )
                }
            val items = folderItems + gameItems
            gameListAdapter.apply {
                itemList.clear()
                itemList.addAll(items)
                notifyDataSetChanged()
            }
        }
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
        galleryViewAction = SpeedDialActionItem.Builder(R.id.gallery_view, R.drawable.ic_image)
            .setLabel(context.getString(R.string.gallery_view))
            .setLabelBackgroundColor(context.getColorCompat(R.color.button_background))
            .setLabelColor(context.getColorCompat(R.color.button_label))
            .setFabBackgroundColor(context.getColorCompat(R.color.button_background))
            .setFabImageTintColor(context.getColorCompat(R.color.button_label))
            .create()
        listViewAction = SpeedDialActionItem.Builder(R.id.list_view, R.drawable.ic_folder)
            .setLabel(context.getString(R.string.list_view))
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
            if (gameListAdapter.itemList.count() > 1) {
                if (inGallery && Persistence.getGamesByHasArtwork(platform).count() > 1) {
                    addActionItem(randomAction)
                } else if (Persistence.getGamesByPlatform(platform).count() > 1) {
                    addActionItem(randomAction)
                }
            }
            if (inGallery) {
                addActionItem(listViewAction)
            } else {
                addActionItem(galleryViewAction)
            }
            setOnActionSelectedListener(
                SpeedDialView.OnActionSelectedListener { actionItem ->
                    when (actionItem.id) {
                        R.id.gallery_view -> {
                            onGalleryView()
                            close()
                            return@OnActionSelectedListener true
                        }

                        R.id.list_view -> {
                            onListView()
                            close()
                            return@OnActionSelectedListener true
                        }

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

    private fun onGalleryView() {
        inGallery = true
        setRecycler()
        setSpeedDial()
    }

    private fun onListView() {
        inGallery = false
        setRecycler()
        setSpeedDial()
    }

    private fun onRandom() {
        Navigator.showScreen(
            activity as AppCompatActivity,
            if (inGallery) {
                FragmentMaker.game(Persistence.getGamesByHasArtwork(platform).random().path)
            } else {
                FragmentMaker.game(Persistence.getGamesByPlatform(platform).random().path)
            }
        )
    }

    private fun onSync() {
        Navigator.showLoadingScreen()
        val activity = requireActivity()
        Coroutine.launch(
            activity = activity,
            run = {
                Util.syncPlatforms(requireActivity(), listOf(platform))
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
}
