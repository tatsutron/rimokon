package com.tatsutron.rimokon.recycler

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.l4digital.fastscroll.FastScroller
import com.tatsutron.rimokon.R
import java.util.Locale

class GameListAdapter(
    private val activity: Activity,
    val itemList: MutableList<GameListItem> = mutableListOf(),
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), FastScroller.SectionIndexer {

    companion object {
        const val TYPE_FOLDER = 0
        const val TYPE_GAME = 1
        const val TYPE_SPACER = 2
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(activity)
        val itemView = layoutInflater.inflate(
            when (viewType) {
                TYPE_FOLDER -> R.layout.list_item_folder
                TYPE_SPACER -> R.layout.list_item_spacer
                else -> R.layout.list_item_game
            },
            parent,
            false,
        )
        return when (viewType) {
            TYPE_FOLDER -> FolderHolder(itemView)
            TYPE_SPACER -> SpacerHolder(itemView)
            else -> GameHolder(activity, itemView)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (getItemViewType(position)) {
            TYPE_FOLDER -> (holder as? FolderHolder)?.bind(itemList[position] as FolderItem)
            TYPE_SPACER -> (holder as? SpacerHolder)?.bind(itemList[position] as SpacerItem)
            else -> (holder as? GameHolder)?.bind(itemList[position] as GameItem)
        }
    }

    override fun getItemCount() = itemList.size

    override fun getItemViewType(position: Int) = when (itemList[position]) {
        is FolderItem -> TYPE_FOLDER
        is SpacerItem -> TYPE_SPACER
        else -> TYPE_GAME
    }

    override fun getSectionText(position: Int): String {
        val item = itemList.getOrNull(position)
        return item?.text?.first()?.toString()?.toUpperCase(Locale.getDefault()) ?: ""
    }
}
