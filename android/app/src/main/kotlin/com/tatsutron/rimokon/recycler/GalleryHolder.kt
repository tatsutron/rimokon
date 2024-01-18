package com.tatsutron.rimokon.recycler

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tatsutron.rimokon.R

class GalleryHolder(
    itemView: View,
) : RecyclerView.ViewHolder(itemView) {

    private val image: ImageView = itemView.findViewById(R.id.image)

    fun bind(item: GalleryItem) {
        Picasso.get().load(item.game.artwork).into(image)
    }
}
