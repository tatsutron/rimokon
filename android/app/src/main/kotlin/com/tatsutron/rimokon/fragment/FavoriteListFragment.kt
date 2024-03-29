package com.tatsutron.rimokon.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tatsutron.rimokon.R
import com.tatsutron.rimokon.recycler.GameItem
import com.tatsutron.rimokon.recycler.GameListAdapter
import com.tatsutron.rimokon.util.Dialog
import com.tatsutron.rimokon.util.Persistence

class FavoriteListFragment : BaseFragment() {

    private lateinit var adapter: GameListAdapter

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
            R.layout.fragment_favorite_list,
            container,
            false,
        )
    }

    override fun onResume() {
        super.onResume()
        if (Persistence.getGamesByFavorite().isNotEmpty()) {
            setRecycler()
        } else {
            val context = requireContext()
            Dialog.message(
                context = context,
                title = context.getString(R.string.no_favorites_found),
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.title = requireContext().getText(R.string.favorites)
        }
        adapter = GameListAdapter(activity as Activity)
        view.findViewById<RecyclerView>(R.id.recycler).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@FavoriteListFragment.adapter
        }
        setRecycler()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setRecycler() {
        adapter.itemList.clear()
        adapter.itemList.addAll(
            Persistence.getGamesByFavorite().map {
                GameItem(
                    icon = it.platform.media.icon,
                    game = it,
                    subscript = it.platform.displayName ?: "",
                )
            },
        )
        adapter.notifyDataSetChanged()
    }
}
