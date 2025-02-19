package com.tatsutron.rimokon.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.tatsutron.rimokon.MainActivity
import com.tatsutron.rimokon.R
import com.tatsutron.rimokon.util.Coroutine
import com.tatsutron.rimokon.util.Dialog
import com.tatsutron.rimokon.util.Navigator
import com.tatsutron.rimokon.util.Persistence
import com.tatsutron.rimokon.util.Util
import kotlinx.android.synthetic.main.fragment_scan.preview
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanFragment : BaseFragment() {

    private lateinit var cameraExecutor: ExecutorService
    private var lastQrCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_scan, container, false)

    override fun onDestroy() {
        cameraExecutor.shutdown()
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { permissionGranted ->
            if (permissionGranted) {
                startCamera()
            } else {
                val context = requireContext()
                MaterialDialog(context).show {
                    message(
                        text = context.getString(R.string.no_camera_permission),
                    )
                    positiveButton(
                        res = R.string.ok,
                        click = {
                            (activity as? MainActivity)?.onBackPressed()
                        },
                    )
                }
            }
        }
        val requiredPermission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                requiredPermission,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(requiredPermission)
        }
    }

    private fun startCamera() {
        val context = requireContext()
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener(
            {
                val provider: ProcessCameraProvider = future.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(preview.surfaceProvider)
                }
                val imageAnalysis = ImageAnalysis.Builder().build().also {
                    it.setAnalyzer(
                        cameraExecutor,
                        BarcodeAnalyzer(
                            context = context,
                            listener = { data ->
                                handleResult(data)
                            },
                        ),
                    )
                }
                try {
                    provider.unbindAll()
                    provider.bindToLifecycle(
                        this,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis,
                    )
                } catch (e: Exception) {
                    Dialog.error(context, e)
                }
            },
            ContextCompat.getMainExecutor(context),
        )
    }

    private fun handleResult(data: String) {
        if (data == lastQrCode) {
            return
        } else {
            lastQrCode = data
        }
        val context = requireContext()
        if (data.startsWith("sha1://")) {
            val sha1 = data.replace("sha1://", "")
            Persistence.getGameBySha1(sha1)?.let { game ->
                Dialog.confirmation(
                    context = context,
                    messageText = context.getString(
                        R.string.would_you_like_to_play,
                        game.title,
                    ),
                    negativeButtonText = context.getString(R.string.no),
                    positiveButtonText = context.getString(R.string.yes),
                    callback = {
                        Navigator.showLoadingScreen()
                        Coroutine.launch(
                            activity = requireActivity(),
                            run = {
                                // TODO Fail gracefully on no connection
                                Util.loadGame(game)
                            },
                            finally = {
                                Navigator.hideLoadingScreen()
                            },
                        )
                    }
                )
            }
        } else {
            Dialog.confirmation(
                context = context,
                messageText = context.getString(R.string.would_you_like_to_use_zaparoo),
                negativeButtonText = context.getString(R.string.no),
                positiveButtonText = context.getString(R.string.yes),
                callback = {
                    Navigator.showLoadingScreen()
                    Coroutine.launch(
                        activity = requireActivity(),
                        run = {
                            // TODO Fail gracefully on no connection
                            Util.zaparoo(data)
                        },
                        finally = {
                            Navigator.hideLoadingScreen()
                        },
                    )
                }
            )
        }
    }
}