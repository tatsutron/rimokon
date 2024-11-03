package com.tatsutron.rimokon.model

import java.io.File

class Game(
    val artwork: String? = null,
    val developer: String? = null,
    val favorite: Boolean,
    val path: String,
    val platform: Platform,
    val region: String? = null,
    val sha1: String?,
    val year: String? = null,
) {

    val fileName: String
        get() = File(path).name
}
