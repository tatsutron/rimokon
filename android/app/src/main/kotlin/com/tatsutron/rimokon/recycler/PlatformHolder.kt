package com.tatsutron.rimokon.recycler

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.tatsutron.rimokon.R
import com.tatsutron.rimokon.util.FragmentMaker
import com.tatsutron.rimokon.util.Navigator
import java.util.*

class PlatformHolder(
    private val activity: Activity,
    itemView: View,
) : RecyclerView.ViewHolder(itemView) {

    private val image: ImageView = itemView.findViewById(R.id.image)
    private val label: TextView = itemView.findViewById(R.id.label)

    fun bind(item: PlatformItem) {
        println(item.platform.name)
        image.setImageDrawable(
            AppCompatResources.getDrawable(
                image.context,
                image.resources.getIdentifier(
                    "platform_${item.platform.name.toLowerCase(Locale.getDefault())}",
                    "drawable",
                    image.context.packageName,
                )
            )
        )
        label.text = item.platform.displayName
        itemView.setOnClickListener {
            Navigator.showScreen(
                activity as AppCompatActivity,
                FragmentMaker.platform(item.platform),
            )
        }
    }
}
