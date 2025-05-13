package com.tatsutron.rimokon.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.tatsutron.rimokon.R
import com.tatsutron.rimokon.recycler.GalleryAdapter
import com.tatsutron.rimokon.recycler.GalleryItem
import com.tatsutron.rimokon.recycler.GameItem
import com.tatsutron.rimokon.recycler.GameListAdapter
import com.tatsutron.rimokon.recycler.SpacerItem
import com.tatsutron.rimokon.util.FragmentMaker
import com.tatsutron.rimokon.util.Navigator
import com.tatsutron.rimokon.util.Persistence
import com.tatsutron.rimokon.util.getColorCompat

class FavoriteListFragment : BaseFragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var gameListAdapter: GameListAdapter
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var randomAction: SpeedDialActionItem
    private lateinit var galleryViewAction: SpeedDialActionItem
    private lateinit var listViewAction: SpeedDialActionItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(
            R.layout.fragment_favorite_list,
            container,
            false,
        )
    }

    override fun onResume() {
        super.onResume()
        setRecycler()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameListAdapter = GameListAdapter(activity as Activity)
        galleryAdapter = GalleryAdapter(activity as Activity)
        recycler = view.findViewById<RecyclerView>(R.id.recycler).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@FavoriteListFragment.gameListAdapter
        }
        setRecycler()
        setSpeedDialActionItems()
        setSpeedDial()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setRecycler() {
        if (!this::recycler.isInitialized) {
            return
        }
        if (MainFragment.searchTerm.isEmpty()) {
            if (Persistence.inGallery) {
                recycler.adapter = galleryAdapter
                val galleryItems = Persistence.getGamesByHasArtworkByFavorite().map {
                    GalleryItem(it)
                }
                galleryAdapter.apply {
                    itemList.clear()
                    itemList.addAll(galleryItems)
                    notifyDataSetChanged()
                }
            } else {
                recycler.adapter = gameListAdapter
                gameListAdapter.apply {
                    itemList.clear()
                    itemList.add(SpacerItem())
                    itemList.addAll(
                        Persistence.getGamesByFavorite().map {
                            GameItem(
                                icon = it.platform.media.icon,
                                game = it,
                                subscript = it.platform.displayName ?: "",
                            )
                        },
                    )
                    itemList.add(SpacerItem())
                    notifyDataSetChanged()
                }
            }
        } else {
            recycler.adapter = gameListAdapter
            val gameItems = Persistence.getGamesBySearch(MainFragment.searchTerm).map {
                GameItem(
                    icon = it.platform.media.icon,
                    game = it,
                    subscript = it.platform.displayName ?: "",
                )
            }
            gameListAdapter.apply {
                itemList.clear()
                itemList.add(SpacerItem())
                itemList.addAll(gameItems)
                itemList.add(SpacerItem())
                notifyDataSetChanged()
            }
        }
    }

    private fun setSpeedDialActionItems() {
        val context = requireContext()
        randomAction = SpeedDialActionItem.Builder(R.id.random, R.drawable.ic_random)
            .setLabel(context.getString(R.string.random))
            .setLabelBackgroundColor(context.getColorCompat(R.color.button_background))
            .setLabelColor(context.getColorCompat(R.color.button_label))
            .setFabBackgroundColor(context.getColorCompat(R.color.button_background))
            .setFabImageTintColor(context.getColorCompat(R.color.button_label)).create()
        galleryViewAction = SpeedDialActionItem.Builder(R.id.gallery_view, R.drawable.ic_image)
            .setLabel(context.getString(R.string.gallery_view))
            .setLabelBackgroundColor(context.getColorCompat(R.color.button_background))
            .setLabelColor(context.getColorCompat(R.color.button_label))
            .setFabBackgroundColor(context.getColorCompat(R.color.button_background))
            .setFabImageTintColor(context.getColorCompat(R.color.button_label)).create()
        listViewAction = SpeedDialActionItem.Builder(R.id.list_view, R.drawable.ic_list)
            .setLabel(context.getString(R.string.list_view))
            .setLabelBackgroundColor(context.getColorCompat(R.color.button_background))
            .setLabelColor(context.getColorCompat(R.color.button_label))
            .setFabBackgroundColor(context.getColorCompat(R.color.button_background))
            .setFabImageTintColor(context.getColorCompat(R.color.button_label)).create()
    }

    private fun setSpeedDial() {
        view?.findViewById<SpeedDialView>(R.id.speed_dial)?.apply {
            clearActionItems()
            if (gameListAdapter.itemList.count() > 1) {
                if (Persistence.inGallery && Persistence.getGamesByHasArtworkByFavorite()
                        .count() > 1
                ) {
                    addActionItem(randomAction)
                } else if (Persistence.getGamesByFavorite().count() > 1) {
                    addActionItem(randomAction)
                }
            }
            if (Persistence.inGallery) {
                addActionItem(listViewAction)
            } else {
                addActionItem(galleryViewAction)
            }
            setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
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
                }
                false
            })
        }
    }

    private fun onGalleryView() {
        Persistence.inGallery = true
        setRecycler()
        setSpeedDial()
    }

    private fun onListView() {
        Persistence.inGallery = false
        setRecycler()
        setSpeedDial()
    }

    private fun onRandom() {
        Navigator.showScreen(
            activity as AppCompatActivity,
            if (Persistence.inGallery) {
                FragmentMaker.game(Persistence.getGamesByHasArtworkByFavorite().random().path)
            } else {
                FragmentMaker.game(Persistence.getGamesByFavorite().random().path)
            },
        )
    }
}
