package com.tatsutron.rimokon.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.text.InputType
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {

            R.id.set_ip_address -> {
                MaterialDialog(requireContext()).show {
                    title(
                        res = R.string.enter_mister_ip_address,
                    )
                    negativeButton(R.string.cancel)
                    positiveButton(R.string.ok)
                    input(
                        inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS,
                        prefill = Persistence.host,
                        callback = { _, text ->
                            Persistence.host = text.toString()
                            Navigator.showLoadingScreen()
                            Util.deployAssets(
                                activity = requireActivity(),
                                callback = {
                                    Navigator.hideLoadingScreen()
                                },
                            )
                        },
                    )
                }
                true
            }

            R.id.sync_library -> {
                onSync()
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

            R.id.preferences -> {
                Navigator.showScreen(
                    activity as AppCompatActivity,
                    FragmentMaker.preferences(),
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

    private fun onSync() {
        val activity = activity as Activity
        Dialog.warning(activity, activity.getString(R.string.sync_library_warning))
        Navigator.showLoadingScreen()
        Coroutine.launch(
            activity = activity,
            run = {
                Util.syncPlatforms(Platform.values().toList())
            },
            failure = { throwable ->
                when (throwable) {
                    is JSchException ->
                        if (Persistence.host.isEmpty()) {
                            Dialog.enterIpAddress(
                                context = activity,
                                ipAddressSet = ::onSync,
                            )
                        } else {
                            Dialog.connectionFailed(
                                context = activity,
                                ipAddressSet = ::onSync,
                            )
                        }

                    else ->
                        Dialog.error(activity, throwable)
                }
            },
            finally = {
                Navigator.hideLoadingScreen()
            }
        )
    }
}
