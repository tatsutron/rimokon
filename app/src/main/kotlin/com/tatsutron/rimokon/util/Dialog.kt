package com.tatsutron.rimokon.util

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputType
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.tatsutron.rimokon.R

object Dialog {

    fun confirmation(
        context: Context,
        titleText: String? = null,
        messageText: String,
        negativeButtonText: String,
        positiveButtonText: String,
        callback: () -> Unit,
    ) {
        MaterialDialog(context).show {
            titleText?.let {
                title(text = it)
            }
            message(text = messageText)
            negativeButton(
                text = negativeButtonText,
            )
            positiveButton(
                text = positiveButtonText,
                click = {
                    callback.invoke()
                },
            )
        }
    }

    @SuppressLint("CheckResult")
    fun connectionFailed(
        context: Context,
        callback: () -> Unit,
    ) {
        MaterialDialog(context).show {
            title(
                res = R.string.failed_to_connect,
            )
            message(
                res = R.string.failed_to_connect_message,
            )
            negativeButton(
                res = R.string.set_ip_address,
                click = {
                    ::enterIpAddress.invoke(context, callback)
                },
            )
            positiveButton(R.string.ok)
        }
    }

    @SuppressLint("CheckResult")
    fun enterIpAddress(
        context: Context,
        callback: () -> Unit,
    ) {
        MaterialDialog(context).show {
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
                    callback()
                },
            )
        }
    }

    fun error(
        context: Context,
        throwable: Throwable,
    ) {
        MaterialDialog(context).show {
            title(res = R.string.error)
            message(text = throwable.toString())
            positiveButton(R.string.ok)
        }
    }

    fun message(
        context: Context,
        title: String,
    ) {
        MaterialDialog(context).show {
            title(text = title)
            positiveButton(R.string.ok)
        }
    }

    @SuppressLint("CheckResult")
    fun metadata(
        context: Context,
        title: String,
        currentValue: String,
        callback: (String) -> Unit,
    ) {
        MaterialDialog(context).show {
            title(text = title)
            negativeButton(R.string.cancel)
            positiveButton(R.string.ok)
            input(
                inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS,
                prefill = currentValue,
                callback = { _, text ->
                    callback(text.toString())
                },
            )
        }
    }
}
