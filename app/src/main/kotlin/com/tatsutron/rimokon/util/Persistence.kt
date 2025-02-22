package com.tatsutron.rimokon.util

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.tatsutron.rimokon.Database
import com.tatsutron.rimokon.data.Games
import com.tatsutron.rimokon.data.SelectBySha1
import com.tatsutron.rimokon.model.*
import java.io.File
import java.io.FileOutputStream
import java.util.*

object Persistence {

    private lateinit var configFile: File
    private lateinit var config: Config
    private val gson = Gson()
    private var database: Database? = null

    fun deleteGame(path: String) {
        database?.gamesQueries?.deleteByPath(path)
    }

    private fun game(dao: Games) = Game(
        artwork = dao.artwork,
        developer = dao.developer,
        favorite = dao.favorite != 0L,
        path = dao.path,
        platform = Platform.valueOf(dao.platform),
        region = dao.region,
        sha1 = dao.sha1,
        title = dao.title,
        year = dao.year,
    )

    fun getGamesByHasArtworkByPlatform(platform: Platform) =
        database?.gamesQueries?.selectByHasArtworkByPlatform(platform.name)?.executeAsList()?.map {
            game(it)
        }?.sortedBy {
            File(it.path).name.toLowerCase(Locale.getDefault())
        } ?: listOf()

    fun getGamesByHasArtworkByFavorite() =
        database?.gamesQueries?.selectByHasArtworkByFavorite()?.executeAsList()?.map {
            game(it)
        }?.sortedBy {
            File(it.path).name.toLowerCase(Locale.getDefault())
        } ?: listOf()

    fun getGamesByPlatform(platform: Platform) =
        database?.gamesQueries?.selectByPlatform(platform.name)?.executeAsList()?.map {
            game(it)
        }?.sortedBy {
            File(it.path).name.toLowerCase(Locale.getDefault())
        } ?: listOf()

    fun getGameByPath(path: String) =
        database?.gamesQueries?.selectByPath(path)?.executeAsOneOrNull()?.let {
            game(it)
        }

    fun getGameBySha1(sha1: String) =
        database?.gamesQueries?.selectBySha1(sha1)?.executeAsOneOrNull()?.let {
            game(it)
        }

    fun getGamesByFavorite() = database?.gamesQueries?.selectByFavorite()?.executeAsList()?.map {
        game(it)
    }?.sortedBy {
        File(it.path).name.toLowerCase(Locale.getDefault())
    } ?: listOf()

    fun getGamesBySearch(searchTerm: String) =
        database?.gamesQueries?.selectBySearch(searchTerm)?.executeAsList()?.map {
            game(it)
        }?.sortedBy {
            File(it.path).name.toLowerCase(Locale.getDefault())
        } ?: listOf()

    fun getMetadataBySha1(sha1: String, releaseOffset: Int) =
        database?.metadataQueries?.selectBySha1(sha1.toUpperCase(Locale.getDefault()))
            ?.executeAsList()?.let {
                if (it.isNotEmpty()) {
                    metadata(it[releaseOffset % it.count()])
                } else {
                    null
                }
            }

    var host: String
        set(ipAddress) {
            config.host = ipAddress
            configFile.writeText(gson.toJson(config))
        }
        get() = config.host

    var startAtTab: Int
        set(startAtTab) {
            config.startAtTab = startAtTab
            configFile.writeText(gson.toJson(config))
        }
        get() = config.startAtTab

    var inGallery: Boolean
        set(inGallery) {
            config.inGallery = inGallery
            configFile.writeText(gson.toJson(config))
        }
        get() = config.inGallery

    @SuppressLint("SdCardPath")
    fun init(context: Context) {
        configFile = File(context.filesDir, "config.json")
        if (configFile.exists()) {
            config = gson.fromJson(configFile.readText(), Config::class.java)
        } else {
            config = Config()
            configFile.writeText(gson.toJson(config))
        }

        val dir = "/data/data/${context.packageName}/databases"
        val name = "app.db"
        val path = File(dir, name).path
        if (!File(path).exists()) {
            File(dir).mkdir()
            File(path).createNewFile()
            context.assets.open("openvgdb.sqlite").copyTo(FileOutputStream(path))
        }
        database = Database(
            AndroidSqliteDriver(Database.Schema, context, name)
        )
    }

    private fun metadata(dao: SelectBySha1) = Metadata(
        artwork = dao.artwork,
        developer = dao.developer,
        region = dao.region,
        title = dao.title,
        year = dao.year,
    )

    fun saveGame(path: String, platform: Platform, sha1: String?) =
        database?.gamesQueries?.save(File(path).nameWithoutExtension, path, platform.name, sha1)

    fun updateArtwork(game: Game, artwork: String) =
        database?.gamesQueries?.updateArtwork(artwork, game.path)

    fun updateDeveloper(game: Game, developer: String) =
        database?.gamesQueries?.updateDeveloper(developer, game.path)

    fun updateFavorite(game: Game, favorite: Boolean) =
        database?.gamesQueries?.updateFavorite(if (favorite) 1L else 0L, game.path)

    fun updateRegion(game: Game, region: String) =
        database?.gamesQueries?.updateRegion(region, game.path)

    fun updateSha1(game: Game, sha1: String) = database?.gamesQueries?.updateSha1(sha1, game.path)

    fun updateTitle(game: Game, title: String) =
        database?.gamesQueries?.updateTitle(title, game.path)

    fun updateYear(game: Game, year: String) =
        database?.gamesQueries?.updateYear(year, game.path)
}
