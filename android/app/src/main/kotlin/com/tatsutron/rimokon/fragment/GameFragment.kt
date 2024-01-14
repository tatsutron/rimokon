package com.tatsutron.rimokon.fragment

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.jcraft.jsch.JSchException
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
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
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var speedDial: SpeedDialView
    private lateinit var favoriteAction: SpeedDialActionItem
    private lateinit var unfavoriteAction: SpeedDialActionItem
    private lateinit var generateQrAction: SpeedDialActionItem
    private lateinit var copyQrAction: SpeedDialActionItem
    private lateinit var importAction: SpeedDialActionItem
    private lateinit var artworkCard: ImageCard
    private lateinit var developerCard: MetadataCard
    private lateinit var publisherCard: MetadataCard
    private lateinit var regionCard: MetadataCard
    private lateinit var releaseDateCard: MetadataCard
    private lateinit var genreCard: MetadataCard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        // Handle browse for artwork result
        val startActivityForResult = ActivityResultContracts.StartActivityForResult()
        resultLauncher = registerForActivityResult(startActivityForResult) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data.toString()
                Persistence.updateArtwork(game, imageUri)
                setArtwork(imageUri)
            }
        }
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
            supportActionBar?.title = game.name
        }
        speedDial = view.findViewById(R.id.speed_dial)
        artworkCard = view.findViewById(R.id.artwork)
        developerCard = view.findViewById(R.id.developer)
        publisherCard = view.findViewById(R.id.publisher)
        regionCard = view.findViewById(R.id.region)
        releaseDateCard = view.findViewById(R.id.release_date)
        genreCard = view.findViewById(R.id.genre)
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
            .setFabImageTintColor(context.getColorCompat(R.color.button_label))
            .create()
        unfavoriteAction = SpeedDialActionItem.Builder(R.id.unfavorite, R.drawable.ic_star_fill)
            .setLabel(context.getString(R.string.unfavorite))
            .setLabelBackgroundColor(context.getColorCompat(R.color.button_background))
            .setLabelColor(context.getColorCompat(R.color.button_label))
            .setFabBackgroundColor(context.getColorCompat(R.color.button_background))
            .setFabImageTintColor(context.getColorCompat(R.color.button_label))
            .create()
        generateQrAction = SpeedDialActionItem.Builder(R.id.generate_qr, R.drawable.ic_qr_code)
            .setLabel(context.getString(R.string.generate_qr_data))
            .setLabelBackgroundColor(context.getColorCompat(R.color.button_background))
            .setLabelColor(context.getColorCompat(R.color.button_label))
            .setFabBackgroundColor(context.getColorCompat(R.color.button_background))
            .setFabImageTintColor(context.getColorCompat(R.color.button_label))
            .create()
        copyQrAction = SpeedDialActionItem.Builder(R.id.copy_qr, R.drawable.ic_copy)
            .setLabel(context.getString(R.string.copy_qr_data))
            .setLabelBackgroundColor(context.getColorCompat(R.color.button_background))
            .setLabelColor(context.getColorCompat(R.color.button_label))
            .setFabBackgroundColor(context.getColorCompat(R.color.button_background))
            .setFabImageTintColor(context.getColorCompat(R.color.button_label))
            .create()
        importAction = SpeedDialActionItem.Builder(R.id.import_metadata, R.drawable.ic_cloud_download)
            .setLabel(context.getString(R.string.import_metadata))
            .setLabelBackgroundColor(context.getColorCompat(R.color.button_background))
            .setLabelColor(context.getColorCompat(R.color.button_label))
            .setFabBackgroundColor(context.getColorCompat(R.color.button_background))
            .setFabImageTintColor(context.getColorCompat(R.color.button_label))
            .create()
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
            }
            if (game.sha1 != null) {
                addActionItem(copyQrAction)
                addActionItem(importAction)
            }
            setOnActionSelectedListener(
                SpeedDialView.OnActionSelectedListener { actionItem ->
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
                            Navigator.showLoadingScreen()
                            Coroutine.launch(
                                activity = requireActivity(),
                                run = {
                                    Thread.sleep(3000)
                                    Persistence.getMetadataBySha1(game.sha1!!)?.let {
                                        onImportMetadata(it)
                                    }
                                },
                                finally = {
                                    Navigator.hideLoadingScreen()
                                }
                            )
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
                }
            )
        }
    }

    private fun setMetadata() {
        artworkCard.apply {
            game.artwork?.let {
                setArtwork(it)
            }
            editButton.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_GET_CONTENT,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                )
                resultLauncher.launch(intent)
            }
        }
        developerCard.apply {
            game.developer?.let {
                bodyText.text = it
            }
            editButton.setOnClickListener {
                Dialog.metadata(
                    requireContext(),
                    "Enter developer",
                    bodyText.text.toString()
                ) { result ->
                    Persistence.updateDeveloper(game, result)
                    bodyText.text = result
                }
            }
        }
        publisherCard.apply {
            game.publisher?.let {
                bodyText.text = it
            }
            editButton.setOnClickListener {
                Dialog.metadata(
                    requireContext(),
                    "Enter publisher",
                    bodyText.text.toString()
                ) { result ->
                    Persistence.updatePublisher(game, result)
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
                    requireContext(),
                    "Enter region",
                    bodyText.text.toString()
                ) { result ->
                    Persistence.updateRegion(game, result)
                    bodyText.text = result
                }
            }
        }
        releaseDateCard.apply {
            game.releaseDate?.let {
                bodyText.text = it
            }
            editButton.setOnClickListener {
                Dialog.metadata(
                    requireContext(),
                    "Enter release date",
                    bodyText.text.toString()
                ) { result ->
                    Persistence.updateReleaseDate(game, result)
                    bodyText.text = result
                }
            }
        }
        genreCard.apply {
            game.genre?.let {
                bodyText.text = it
            }
            editButton.setOnClickListener {
                Dialog.metadata(
                    requireContext(),
                    "Enter genre",
                    bodyText.text.toString()
                ) { result ->
                    Persistence.updateGenre(game, result)
                    bodyText.text = result
                }
            }
        }
    }

    private fun setArtwork(imageUri: String) =
        Glide.with(activity?.applicationContext!!)
            .load(Uri.parse(imageUri))
            .into(artworkCard.image)

    private fun onPlay() {
        Navigator.showLoadingScreen()
        Util.loadGame(
            activity = requireActivity(),
            game = game,
            callback = {
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
            },
            success = {
                game = Persistence.getGameByPath(game.path)!!
                setSpeedDial()
                setMetadata()
            },
            failure = { throwable ->
                when (throwable) {
                    is JSchException ->
                        Dialog.connectionFailed(
                            context = activity,
                            callback = ::onGenerateQr,
                        )

                    else ->
                        Dialog.error(activity, throwable)
                }
            },
            finally = {
                Navigator.hideLoadingScreen()
            }
        )
    }

    private fun onCopyQr() {
        val context = requireContext()
        val clipboard = ContextCompat.getSystemService(
            context,
            ClipboardManager::class.java,
        )
        clipboard?.setPrimaryClip(ClipData.newPlainText("QR", game.sha1))
        Toast.makeText(
            requireActivity(),
            "Copied QR Data to Clipboard",
            Toast.LENGTH_SHORT,
        ).show()
    }

    private fun onImportMetadata(metadata: Metadata) {
        metadata.artwork?.let {
            if (game.artwork.isNullOrBlank()) {
                Persistence.updateArtwork(game, it)
                setArtwork(it)
            }
        }
        metadata.developer?.let {
            if (game.developer.isNullOrBlank()) {
                Persistence.updateDeveloper(game, it)
                developerCard.bodyText.text = it
            }
        }
        metadata.publisher?.let {
            if (game.publisher.isNullOrBlank()) {
                Persistence.updatePublisher(game, it)
                publisherCard.bodyText.text = it
            }
        }
        metadata.region?.let {
            if (game.region.isNullOrBlank()) {
                Persistence.updateRegion(game, it)
                regionCard.bodyText.text = it
            }
        }
        metadata.releaseDate?.let {
            if (game.releaseDate.isNullOrBlank()) {
                Persistence.updateReleaseDate(game, it)
                releaseDateCard.bodyText.text = it
            }
        }
        metadata.genre?.let {
            if (game.genre.isNullOrBlank()) {
                Persistence.updateGenre(game, it)
                genreCard.bodyText.text = it
            }
        }
    }
}
