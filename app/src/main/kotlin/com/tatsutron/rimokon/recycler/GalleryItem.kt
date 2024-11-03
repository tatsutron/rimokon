package com.tatsutron.rimokon.recycler

import com.tatsutron.rimokon.model.Game

class GalleryItem(
    val game: Game,
) : GameListItem {

    override val text: String
        get() = game.fileName
}
