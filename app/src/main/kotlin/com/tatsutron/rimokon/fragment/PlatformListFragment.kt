package com.tatsutron.rimokon.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.l4digital.fastscroll.FastScrollRecyclerView
import com.tatsutron.rimokon.R
import com.tatsutron.rimokon.model.Platform
import com.tatsutron.rimokon.recycler.GameItem
import com.tatsutron.rimokon.recycler.GameListAdapter
import com.tatsutron.rimokon.recycler.PlatformItem
import com.tatsutron.rimokon.recycler.PlatformListAdapter
import com.tatsutron.rimokon.util.Persistence

class PlatformListFragment : BaseFragment() {

    private lateinit var recycler: FastScrollRecyclerView
    private lateinit var platformListAdapter: PlatformListAdapter
    private lateinit var gameListAdapter: GameListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(
            R.layout.fragment_platform_list,
            container,
            false,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        platformListAdapter = PlatformListAdapter(activity as Activity)
        gameListAdapter = GameListAdapter(activity as Activity)
        recycler = view.findViewById<FastScrollRecyclerView>(R.id.recycler).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@PlatformListFragment.platformListAdapter
        }
        setRecycler()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setRecycler() {
        if (!this::recycler.isInitialized) {
            return
        }
        if (MainFragment.searchTerm.isEmpty()) {
            recycler.adapter = platformListAdapter
            val items = Platform.values().map {
                PlatformItem(it)
            }
            platformListAdapter.apply {
                itemList.clear()
                itemList.addAll(items)
                notifyDataSetChanged()
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
                itemList.addAll(gameItems)
                notifyDataSetChanged()
            }
        }
    }
}
