package com.tatsutron.rimokon.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.tatsutron.rimokon.R

class MetadataCard(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
) : FrameLayout(context, attrs, defStyleAttr) {

    val bodyText: TextView
    val editButton: ImageView

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, defStyleAttr = 0)

    constructor(context: Context) : this(context, attrs = null, defStyleAttr = 0)

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.component_metadata_card, this, true)
        val attributes = context.obtainStyledAttributes(
            attrs,
            R.styleable.MetadataCard,
            defStyleAttr,
            0, // defStyleRes
        )
        findViewById<TextView>(R.id.header_text).apply {
            text = context.getString(
                attributes.getResourceId(
                    R.styleable.MetadataCard_hint,
                    0, // defValue
                ),
            )
        }
        attributes.recycle()
        bodyText = findViewById(R.id.body_text)
        editButton = findViewById(R.id.edit_button)
    }
}