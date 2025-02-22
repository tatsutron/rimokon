package com.tatsutron.rimokon.recycler

import android.app.Activity
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tatsutron.rimokon.R
import com.tatsutron.rimokon.util.FragmentMaker
import com.tatsutron.rimokon.util.Navigator

class GalleryHolder(
    private val activity: Activity,
    itemView: View,
) : RecyclerView.ViewHolder(itemView) {

    private val image: ImageView = itemView.findViewById(R.id.image)

    fun bind(item: GalleryItem) {
        // TODO Filter blanks out from SQL?
        if (item.game.artwork?.isNotBlank() == true) {
            Picasso.get().load(item.game.artwork).into(image)
        }
        itemView.setOnClickListener {
            Navigator.showScreen(
                activity as AppCompatActivity,
                FragmentMaker.game(item.game.path),
            )
        }
    }
}
