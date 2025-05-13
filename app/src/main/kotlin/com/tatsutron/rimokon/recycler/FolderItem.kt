package com.tatsutron.rimokon.recycler

class FolderItem(
    val name: String,
    val onClick: () -> Unit,
) : GameListItem {

    override val text: String?
        get() = name
}
