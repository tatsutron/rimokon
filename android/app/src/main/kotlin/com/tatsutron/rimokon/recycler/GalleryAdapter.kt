package com.tatsutron.rimokon.recycler

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.l4digital.fastscroll.FastScroller
import com.tatsutron.rimokon.R
import java.util.Locale

class GalleryAdapter(
    private val activity: Activity,
    val itemList: MutableList<GalleryItem> = mutableListOf(),
) : RecyclerView.Adapter<GalleryHolder>(), FastScroller.SectionIndexer {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): GalleryHolder {
        val layoutInflater = LayoutInflater.from(activity)
        val itemView = layoutInflater.inflate(R.layout.list_item_gallery, parent, false)
        return GalleryHolder(activity, itemView)
    }

    override fun onBindViewHolder(holder: GalleryHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount() = itemList.size

    override fun getSectionText(position: Int): String {
        val item = itemList.getOrNull(position)
        return item?.text?.first()?.toString()?.toUpperCase(Locale.getDefault()) ?: ""
    }
}
