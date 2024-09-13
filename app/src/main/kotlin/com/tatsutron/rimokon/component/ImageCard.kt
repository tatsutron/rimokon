package com.tatsutron.rimokon.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import com.tatsutron.rimokon.R

class ImageCard(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
) : FrameLayout(context, attrs, defStyleAttr) {

    val image: ImageView
    val editButton: ImageView

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, defStyleAttr = 0)

    constructor(context: Context) : this(context, attrs = null, defStyleAttr = 0)

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.component_image_card, this, true)
        image = findViewById(R.id.image)
        editButton = findViewById(R.id.edit_button)
    }
}