package com.tatsutron.rimokon.recycler

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tatsutron.rimokon.R
import com.tatsutron.rimokon.util.FragmentMaker
import com.tatsutron.rimokon.util.Navigator

class GameHolder(
    private val activity: Activity,
    itemView: View,
) : RecyclerView.ViewHolder(itemView) {

    private val icon: ImageView = itemView.findViewById(R.id.icon)
    private val label: TextView = itemView.findViewById(R.id.label)
    private val subscript: TextView = itemView.findViewById(R.id.subscript)

    fun bind(item: GameItem) {
        item.icon.let {
            icon.setImageDrawable(
                AppCompatResources.getDrawable(icon.context, it),
            )
        }
        label.text = item.game.fileName
        subscript.apply {
            text = item.game.platform.displayName
            setTextColor(
                ContextCompat.getColor(
                    context,
                    item.game.platform.subscriptColor,
                )
            )
        }
        itemView.setOnClickListener {
            Navigator.showScreen(
                activity as AppCompatActivity,
                FragmentMaker.game(item.game.path),
            )
        }
    }
}
