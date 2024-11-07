package com.tatsutron.rimokon.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.jcraft.jsch.JSchException
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.squareup.picasso.Picasso
import com.tatsutron.rimokon.R
import com.tatsutron.rimokon.component.ImageCard
import com.tatsutron.rimokon.component.MetadataCard
import com.tatsutron.rimokon.model.Game
import com.tatsutron.rimokon.model.Metadata
import com.tatsutron.rimokon.util.Coroutine
import com.tatsutron.rimokon.util.Dialog
import com.tatsutron.rimokon.util.FragmentMaker
import com.tatsutron.rimokon.util.Navigator
import com.tatsutron.rimokon.util.Persistence
import com.tatsutron.rimokon.util.Util
import com.tatsutron.rimokon.util.getColorCompat

class GameFragment : BaseFragment() {

    private lateinit var game: Game
    private lateinit var speedDial: SpeedDialView
    private lateinit var favoriteAction: SpeedDialActionItem
    private lateinit var unfavoriteAction: SpeedDialActionItem
    private lateinit var generateQrAction: SpeedDialActionItem
    private lateinit var copyQrAction: SpeedDialActionItem
    private lateinit var importAction: SpeedDialActionItem
    private lateinit var artworkCard: ImageCard
    private lateinit var titleCard: MetadataCard
    private lateinit var developerCard: MetadataCard
    private lateinit var yearCard: MetadataCard
    private lateinit var regionCard: MetadataCard
    private var releaseOffset = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_play, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.play -> {
                onPlay()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        game = Persistence.getGameByPath(
            arguments?.getString(FragmentMaker.KEY_PATH)!!
        )!!
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.title = game.fileName
        }
        speedDial = view.findViewById(R.id.speed_dial)
        artworkCard = view.findViewById(R.id.artwork)
        titleCard = view.findViewById(R.id.title)
        developerCard = view.findViewById(R.id.developer)
        yearCard = view.findViewById(R.id.year)
        regionCard = view.findViewById(R.id.region)
        setSpeedDialActionItems()
        setSpeedDial()
        setMetadata()
    }

    private fun setSpeedDialActionItems() {
        val context = requireContext()
        favoriteAction = SpeedDialActionItem.Builder(R.id.favorite, R.drawable.ic_star_outline)
            .setLabel(context.getString(R.string.favorite))
            .setLabelBackgroundColor(context.getColorCompat(R.color.button_background))
            .setLabelColor(context.getColorCompat(R.color.button_label))
            .setFabBackgroundColor(context.getColorCompat(R.color.button_background))
            .setFabImageTintColor(context.getColorCompat(R.color.button_label)).create()
        unfavoriteAction = SpeedDialActionItem.Builder(R.id.unfavorite, R.drawable.ic_star_fill)
            .setLabel(context.getString(R.string.unfavorite))
            .setLabelBackgroundColor(context.getColorCompat(R.color.button_background))
            .setLabelColor(context.getColorCompat(R.color.button_label))
            .setFabBackgroundColor(context.getColorCompat(R.color.button_background))
            .setFabImageTintColor(context.getColorCompat(R.color.button_label)).create()
        generateQrAction = SpeedDialActionItem.Builder(R.id.generate_qr, R.drawable.ic_qr_code)
            .setLabel(context.getString(R.string.generate_qr_code))
            .setLabelBackgroundColor(context.getColorCompat(R.color.button_background))
            .setLabelColor(context.getColorCompat(R.color.button_label))
            .setFabBackgroundColor(context.getColorCompat(R.color.button_background))
            .setFabImageTintColor(context.getColorCompat(R.color.button_label)).create()
        copyQrAction = SpeedDialActionItem.Builder(R.id.copy_qr, R.drawable.ic_copy)
            .setLabel(context.getString(R.string.copy_qr_data))
            .setLabelBackgroundColor(context.getColorCompat(R.color.button_background))
            .setLabelColor(context.getColorCompat(R.color.button_label))
            .setFabBackgroundColor(context.getColorCompat(R.color.button_background))
            .setFabImageTintColor(context.getColorCompat(R.color.button_label)).create()
        importAction = SpeedDialActionItem.Builder(R.id.import_metadata, R.drawable.ic_cloud_download)
            .setLabel(context.getString(R.string.import_metadata))
            .setLabelBackgroundColor(context.getColorCompat(R.color.button_background))
            .setLabelColor(context.getColorCompat(R.color.button_label))
            .setFabBackgroundColor(context.getColorCompat(R.color.button_background))
            .setFabImageTintColor(context.getColorCompat(R.color.button_label)).create()
    }

    private fun setSpeedDial() {
        speedDial.apply {
            clearActionItems()
            if (game.favorite) {
                addActionItem(unfavoriteAction)
            } else {
                addActionItem(favoriteAction)
            }
            if (game.sha1 == null) {
                addActionItem(generateQrAction)
            } else {
                addActionItem(copyQrAction)
            }
            addActionItem(importAction)
            setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
                when (actionItem.id) {
                    R.id.copy_qr -> {
                        onCopyQr()
                        close()
                        return@OnActionSelectedListener true
                    }

                    R.id.favorite -> {
                        onToggleFavorite()
                        close()
                        return@OnActionSelectedListener true
                    }

                    R.id.generate_qr -> {
                        onGenerateQr()
                        close()
                        return@OnActionSelectedListener true
                    }

                    R.id.import_metadata -> {
                        onImportMetadata()
                        close()
                        return@OnActionSelectedListener true
                    }

                    R.id.unfavorite -> {
                        onToggleFavorite()
                        close()
                        return@OnActionSelectedListener true
                    }
                }
                false
            })
        }
    }

    private fun setMetadata() {
        val context = requireContext()
        artworkCard.apply {
            game.artwork?.let {
                setArtwork(it)
            }
            editButton.setOnClickListener {
                Dialog.metadata(
                    context,
                    context.getString(R.string.enter_url),
                    game.artwork ?: "",
                ) { result ->
                    Persistence.updateArtwork(game, result)
                    setArtwork(result)
                }
            }
        }
        titleCard.apply {
            game.title?.let {
                bodyText.text = it
            }
            editButton.setOnClickListener {
                Dialog.metadata(
                    context, context.getString(R.string.enter_title), bodyText.text.toString()
                ) { result ->
                    Persistence.updateTitle(game, result)
                    bodyText.text = result
                }
            }
        }
        developerCard.apply {
            game.developer?.let {
                bodyText.text = it
            }
            editButton.setOnClickListener {
                Dialog.metadata(
                    context, context.getString(R.string.enter_developer), bodyText.text.toString()
                ) { result ->
                    Persistence.updateDeveloper(game, result)
                    bodyText.text = result
                }
            }
        }
        yearCard.apply {
            game.year?.let {
                bodyText.text = it
            }
            editButton.setOnClickListener {
                Dialog.metadata(
                    context,
                    context.getString(R.string.enter_year),
                    bodyText.text.toString()
                ) { result ->
                    Persistence.updateYear(game, result)
                    bodyText.text = result
                }
            }
        }
        regionCard.apply {
            game.region?.let {
                bodyText.text = it
            }
            editButton.setOnClickListener {
                Dialog.metadata(
                    context, context.getString(R.string.enter_region), bodyText.text.toString()
                ) { result ->
                    Persistence.updateRegion(game, result)
                    bodyText.text = result
                }
            }
        }
    }

    private fun setArtwork(url: String) {
        if (URLUtil.isValidUrl(url)) {
            Picasso.get().load(url).into(artworkCard.image)
        } else {
            artworkCard.image.setImageResource(0)
        }
    }

    private fun onPlay() {
        Navigator.showLoadingScreen()
        val activity = requireActivity()
        Coroutine.launch(
            activity = activity,
            run = {
                Util.loadGame(game)
            },
            failure = { throwable ->
                when (throwable) {
                    is JSchException -> if (Persistence.host.isEmpty()) {
                        Dialog.enterIpAddress(
                            context = activity,
                            callback = ::onPlay,
                        )
                    } else {
                        Dialog.connectionFailed(
                            context = activity,
                            callback = ::onPlay,
                        )
                    }

                    else -> Dialog.error(activity, throwable)
                }
            },
            finally = {
                Navigator.hideLoadingScreen()
            },
        )
    }

    private fun onToggleFavorite() {
        Persistence.updateFavorite(game, !game.favorite)
        game = Persistence.getGameByPath(game.path)!!
        setSpeedDial()
    }

    private fun onGenerateQr() {
        Navigator.showLoadingScreen()
        val activity = requireActivity()
        Coroutine.launch(
            activity = activity,
            run = {
                val sha1 = Util.hash(
                    path = game.path,
                    headerSizeInBytes = game.platform.headerSizeInBytes ?: 0,
                )
                Persistence.updateSha1(game, sha1)
                activity.runOnUiThread {
                    val clipboard = ContextCompat.getSystemService(
                        activity,
                        ClipboardManager::class.java,
                    )
                    clipboard?.setPrimaryClip(ClipData.newPlainText("QR", sha1))
                    Toast.makeText(
                        requireActivity(),
                        activity.getString(R.string.copied_qr_data_to_clipboard),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            },
            success = {
                game = Persistence.getGameByPath(game.path)!!
                setSpeedDial()
                setMetadata()
            },
            failure = { throwable ->
                when (throwable) {
                    is JSchException -> Dialog.connectionFailed(
                        context = activity,
                        callback = ::onGenerateQr,
                    )

                    else -> Dialog.error(activity, throwable)
                }
            },
            finally = {
                Navigator.hideLoadingScreen()
            },
        )
    }

    private fun onImportMetadata() {
        Navigator.showLoadingScreen()
        val activity = requireActivity()
        Coroutine.launch(
            activity = activity,
            run = {
                if (game.sha1 == null) {
                    val sha1 = Util.hash(
                        path = game.path,
                        headerSizeInBytes = game.platform.headerSizeInBytes ?: 0,
                    )
                    Persistence.updateSha1(game, sha1)
                    game = Persistence.getGameBySha1(sha1)!!
                }
                Thread.sleep(1500)
                requireActivity().runOnUiThread {
                    releaseOffset += 1
                    val metadata = Persistence.getMetadataBySha1(game.sha1!!, releaseOffset)
                    if (metadata != null) {
                        onMetadataImported(metadata)
                        game = Persistence.getGameByPath(game.path)!!
                    } else {
                        Dialog.message(
                            context = activity,
                            title = activity.getString(R.string.no_metadata_found),
                        )
                    }
                }
            },
            failure = { throwable ->
                when (throwable) {
                    is JSchException -> if (Persistence.host.isEmpty()) {
                        Dialog.enterIpAddress(
                            context = activity,
                            callback = ::onImportMetadata,
                        )
                    } else {
                        Dialog.connectionFailed(
                            context = activity,
                            callback = ::onImportMetadata,
                        )
                    }

                    else -> Dialog.error(activity, throwable)
                }
            },
            finally = {
                Navigator.hideLoadingScreen()
            },
        )
    }

    private fun onCopyQr() {
        val context = requireContext()
        val clipboard = ContextCompat.getSystemService(
            context,
            ClipboardManager::class.java,
        )
        clipboard?.setPrimaryClip(ClipData.newPlainText("QR", "sha1://${game.sha1}"))
        Toast.makeText(
            requireActivity(),
            context.getString(R.string.copied_qr_data_to_clipboard),
            Toast.LENGTH_SHORT,
        ).show()
    }

    private fun onMetadataImported(metadata: Metadata) {
        metadata.artwork?.let {
            Persistence.updateArtwork(game, it)
            setArtwork(it)
        }
        metadata.title?.let {
            Persistence.updateTitle(game, it)
            titleCard.bodyText.text = it
        }
        metadata.developer?.let {
            Persistence.updateDeveloper(game, it)
            developerCard.bodyText.text = it
        }
        metadata.year?.let {
            Regex("[0-9]{4}").find(it)?.let { result ->
                val year = result.value
                Persistence.updateYear(game, year)
                yearCard.bodyText.text = year
            }
        }
        metadata.region?.let {
            Persistence.updateRegion(game, it)
            regionCard.bodyText.text = it
        }
    }
}
