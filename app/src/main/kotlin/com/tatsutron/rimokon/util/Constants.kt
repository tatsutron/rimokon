package com.tatsutron.rimokon.util

object Constants {
    private const val VOLUME = "/media/fat"

    const val ARCADE_PATH = "${VOLUME}/_Arcade"
    const val GAMES_PATH = "${VOLUME}/games"
    const val SCRIPTS_PATH = "${VOLUME}/Scripts"

    const val TATSUTRON_ROOT = "${VOLUME}/tatsutron"
    const val RIMOKON_ROOT = "${TATSUTRON_ROOT}/rimokon"
    const val MISTER_UTIL_PATH = "${RIMOKON_ROOT}/mister_util.py"

    const val MREXT_ROOT = "${RIMOKON_ROOT}/mrext"
    const val MREXT_CONTOOL_PATH = "${MREXT_ROOT}/contool"
    const val MREXT_OUTPUT_PATH = "${MREXT_ROOT}/out"

    const val TAPTO_PATH = "${SCRIPTS_PATH}/tapto.sh"
}
