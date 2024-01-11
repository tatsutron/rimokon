package com.tatsutron.rimokon.model

import java.io.File

class Game(
    val artwork: String? = null,
    val developer: String? = null,
    val favorite: Boolean,
    val genre: String? = null,
    val path: String,
    val platform: Platform,
    val publisher: String? = null,
    val region: String? = null,
    val releaseDate: String? = null,
    val sha1: String?,
) {

    val name: String
        get() = File(path).nameWithoutExtension
}
