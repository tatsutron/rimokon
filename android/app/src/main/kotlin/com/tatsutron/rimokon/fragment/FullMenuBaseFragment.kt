package com.tatsutron.rimokon.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.jcraft.jsch.JSchException
import com.tatsutron.rimokon.R
import com.tatsutron.rimokon.model.Platform
import com.tatsutron.rimokon.util.Coroutine
import com.tatsutron.rimokon.util.Dialog
import com.tatsutron.rimokon.util.FragmentMaker
import com.tatsutron.rimokon.util.Navigator
import com.tatsutron.rimokon.util.Persistence
import com.tatsutron.rimokon.util.Util

abstract class FullMenuBaseFragment : BaseFragment() {

    @SuppressLint("CheckResult")
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {

        R.id.sync_full_library -> {
            onSyncGameLibrary()
            true
        }

        R.id.favorites -> {
            Navigator.showScreen(
                activity as AppCompatActivity,
                FragmentMaker.favoriteList(),
            )
            true
        }

        R.id.scan_qr_code -> {
            Navigator.showScreen(
                activity as AppCompatActivity,
                FragmentMaker.scan(),
            )
            true
        }

        R.id.credits -> {
            Navigator.showScreen(
                activity as AppCompatActivity,
                FragmentMaker.credits(),
            )
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    private fun onSyncGameLibrary() {
        val activity = activity as Activity
        Navigator.showLoadingScreen()
        Coroutine.launch(
            activity = activity,
            run = {
                Util.syncPlatforms(requireActivity(), Platform.values().toList())
            },
            failure = { throwable ->
                when (throwable) {
                    is JSchException -> if (Persistence.host.isEmpty()) {
                        Dialog.enterIpAddress(
                            context = activity,
                            callback = ::onSyncGameLibrary,
                        )
                    } else {
                        Dialog.connectionFailed(
                            context = activity,
                            callback = ::onSyncGameLibrary,
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
}
