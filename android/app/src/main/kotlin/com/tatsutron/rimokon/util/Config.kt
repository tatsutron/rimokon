package com.tatsutron.rimokon.util

import com.tatsutron.rimokon.model.Platform

class Config(
    var host: String = "",
    val hiddenInPlatformList: MutableList<Platform> = mutableListOf(
        Platform.ARCADIA_2001,
        Platform.ATARI_2600,
        Platform.ATARI_5200,
        Platform.ATARI_7800,
        Platform.BALLY_ASTROCADE,
        Platform.COLECOVISION,
        Platform.FAIRCHILD_CHANNEL_F,
        Platform.INTELLIVISION,
        Platform.INTERTON_VC_4000,
        Platform.ODYSSEY_2,
        Platform.SG_1000,
        Platform.SUPER_GRAFX,
        Platform.VECTREX,
    ),
    val hiddenInGlobalSearch: MutableList<Platform> = mutableListOf(),
)
