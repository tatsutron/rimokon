package com.tatsutron.rimokon.model

import com.tatsutron.rimokon.R
import com.tatsutron.rimokon.util.Constants
import java.io.File

enum class Platform(
    val displayName: String? = null,
    val gamesFolder: String? = null,
    val headerSizeInBytes: Int? = null,
    val media: Media,
    val mrextId: String,
) {

    ARCADE(
        displayName = "Arcade",
        media = Media.PRINTED_CIRCUIT_BOARD,
        mrextId = "arcade",
    ),

    ATARI_LYNX(
        displayName = "Atari Lynx",
        gamesFolder = "AtariLynx",
        headerSizeInBytes = 64,
        media = Media.ROM_CARTRIDGE,
        mrextId = "atarilynx",
    ),

    FAMICOM_DISK_SYSTEM(
        displayName = "Famicom Disk System",
        gamesFolder = "NES",
        headerSizeInBytes = 16,
        media = Media.FLOPPY_DISK,
        mrextId = "fds",
    ),

    GAME_BOY(
        displayName = "Game Boy",
        gamesFolder = "GAMEBOY",
        media = Media.ROM_CARTRIDGE,
        mrextId = "gameboy",
    ),

    GAME_BOY_ADVANCE(
        displayName = "Game Boy Advance",
        gamesFolder = "GBA",
        media = Media.ROM_CARTRIDGE,
        mrextId = "gba",
    ),

    GAME_BOY_COLOR(
        displayName = "Game Boy Color",
        gamesFolder = "GBC",
        media = Media.ROM_CARTRIDGE,
        mrextId = "gameboycolor",
    ),

    GAME_GEAR(
        displayName = "Game Gear",
        gamesFolder = "GameGear",
        media = Media.ROM_CARTRIDGE,
        mrextId = "gamegear",
    ),

    MASTER_SYSTEM(
        displayName = "Master System",
        gamesFolder = "SMS",
        media = Media.ROM_CARTRIDGE,
        mrextId = "mastersystem",
    ),

    NEO_GEO(
        displayName = "Neo Geo",
        gamesFolder = "NEOGEO",
        media = Media.ROM_CARTRIDGE,
        mrextId = "neogeo",
    ),

    NINTENDO_64(
        displayName = "Nintendo 64",
        gamesFolder = "N64",
        media = Media.ROM_CARTRIDGE,
        mrextId = "nintendo64",
    ),

    NINTENDO_ENTERTAINMENT_SYSTEM(
        displayName = "Nintendo Entertainment System",
        gamesFolder = "NES",
        headerSizeInBytes = 16,
        media = Media.ROM_CARTRIDGE,
        mrextId = "nes",
    ),

    PLAYSTATION(
        displayName = "PlayStation",
        gamesFolder = "PSX",
        media = Media.OPTICAL_DISC,
        mrextId = "psx",
    ),

    SEGA_32X(
        displayName = "Sega 32X",
        gamesFolder = "S32X",
        media = Media.ROM_CARTRIDGE,
        mrextId = "sega32x",
    ),

    SEGA_CD(
        displayName = "Sega CD",
        gamesFolder = "MegaCD",
        media = Media.OPTICAL_DISC,
        mrextId = "megacd",
    ),

    SEGA_GENESIS(
        displayName = "Sega Genesis",
        gamesFolder = "Genesis",
        media = Media.ROM_CARTRIDGE,
        mrextId = "genesis",
    ),

    SEGA_SATURN(
        displayName = "Sega Saturn",
        gamesFolder = "Saturn",
        media = Media.ROM_CARTRIDGE,
        mrextId = "saturn",
    ),

    SUPER_GRAFX(
        displayName = "SuperGrafx",
        gamesFolder = "TGFX16",
        media = Media.ROM_CARTRIDGE,
        mrextId = "supergrafx",
    ),

    SUPER_NINTENDO(
        displayName = "Super Nintendo",
        gamesFolder = "SNES",
        media = Media.ROM_CARTRIDGE,
        mrextId = "snes",
    ),

    TURBO_GRAFX_16(
        displayName = "TurboGrafx-16",
        gamesFolder = "TGFX16",
        media = Media.ROM_CARTRIDGE,
        mrextId = "turbografx16",
    ),

    TURBO_GRAFX_CD(
        displayName = "TurboGrafx-CD",
        gamesFolder = "TGFX16-CD",
        media = Media.OPTICAL_DISC,
        mrextId = "turbografx16cd",
    ),

    WONDERSWAN(
        displayName = "WonderSwan",
        gamesFolder = "WonderSwan",
        media = Media.ROM_CARTRIDGE,
        mrextId = "wonderswan",
    ),

    WONDERSWAN_COLOR(
        displayName = "WonderSwan Color",
        gamesFolder = "WonderSwanColor",
        media = Media.ROM_CARTRIDGE,
        mrextId = "wonderswancolor",
    );

    val gamesPath: String?
        get() = when {
            this == ARCADE -> Constants.ARCADE_PATH

            gamesFolder != null -> File(Constants.GAMES_PATH, gamesFolder).path

            else -> null
        }

    private val subscriptColors = listOf(
        R.color.analogous_200,
        R.color.complementary_200,
        R.color.primary_200,
        R.color.triadic_200,
    )

    val subscriptColor: Int
        get() = subscriptColors[ordinal % subscriptColors.size]
}
