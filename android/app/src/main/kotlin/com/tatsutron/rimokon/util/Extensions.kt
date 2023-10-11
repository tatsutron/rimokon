package com.tatsutron.rimokon.util

import android.content.Context
import androidx.core.content.res.ResourcesCompat

fun Context.getColorCompat(id: Int) =
    ResourcesCompat.getColor(resources, id, theme)
