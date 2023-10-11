package com.tatsutron.rimokon.util

object Constants {
    private const val VOLUME = "/media/fat"
    const val TATSUTRON_ROOT = "${VOLUME}/tatsutron"
    const val MISTERCON_ROOT = "${TATSUTRON_ROOT}/mistercon"
    const val MREXT_ROOT = "${MISTERCON_ROOT}/mrext"

    const val ARCADE_PATH = "${VOLUME}/_Arcade"
    const val COMPUTER_PATH = "${VOLUME}/_Computer"
    const val CONSOLE_PATH = "${VOLUME}/_Console"
    const val GAMES_PATH = "${VOLUME}/games"
    const val MISTER_UTIL_PATH = "${MISTERCON_ROOT}/mister_util.py"
    const val MREXT_CONTOOL_PATH = "${MREXT_ROOT}/contool"
    const val MREXT_OUTPUT_PATH = "${MREXT_ROOT}/out"
}
