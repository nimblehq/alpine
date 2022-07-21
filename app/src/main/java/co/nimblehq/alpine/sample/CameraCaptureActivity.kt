package co.nimblehq.alpine.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import co.nimblehq.alpine.databinding.ActivityCameraCaptureBinding
import co.nimblehq.alpine.sample.extension.getStatusBarHeight
import co.nimblehq.alpine.sample.extension.setLightStatusBar
import java.lang.Exception

class CameraCaptureActivity : ComponentActivity() {

    private val binding: ActivityCameraCaptureBinding by lazy {
        ActivityCameraCaptureBinding.inflate(layoutInflater)
    }

    private var camera: Camera? = null
    private var imageCapture: ImageCapture? = null
    private var preview: Preview? = null
    private var cameraProvider: ProcessCameraProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setLightStatusBar()
        setContentView(binding.root)
        setupView()
    }

    private fun setupView() {
        with(binding) {
            with(clCameraTopBar) {
                setPadding(
                    paddingLeft,
                    this@CameraCaptureActivity.getStatusBarHeight(),
                    paddingRight,
                    paddingBottom
                )
            }

            ivTopBarBack.setOnClickListener {
                finish()
            }

            pvCameraCaptureViewFinder.post {
                configureCamera()
            }
        }
    }

    private fun configureCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            try {
                bindCameraImageUseCases()
            } catch (e: Exception) {
                Log.e(this::class.java.canonicalName, "CameraImage UseCase binding failed >>> $e")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraImageUseCases() {
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")
        cameraProvider.unbindAll()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview = Preview.Builder()
            .build()
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        camera = cameraProvider.bindToLifecycle(
            this, cameraSelector, preview, imageCapture
        )
        // Attach the viewFinder's surface provider to Preview use case
        preview?.setSurfaceProvider(binding.pvCameraCaptureViewFinder.surfaceProvider)

    }

    companion object {

        fun start(activity: Activity) {
            val intent = Intent(activity, CameraCaptureActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
