package com.tatsutron.rimokon.recycler

import com.tatsutron.rimokon.model.Game

class GameItem(
    val icon: Int,
    val game: Game,
    val subscript: String,
) : GameListItem {

    override val text: String
        get() = game.name
}
