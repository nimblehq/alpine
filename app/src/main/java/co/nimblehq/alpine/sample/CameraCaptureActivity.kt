package co.nimblehq.alpine.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import co.nimblehq.alpine.databinding.ActivityCameraCaptureBinding
import co.nimblehq.alpine.sample.extension.getStatusBarHeight
import co.nimblehq.alpine.sample.extension.setLightStatusBar

open class CameraCaptureActivity : ComponentActivity() {

    companion object {

        fun start(activity: Activity) {
            val intent = Intent(activity, CameraCaptureActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityCameraCaptureBinding
    private val bindingInflater: (LayoutInflater) -> ActivityCameraCaptureBinding
        get() = { inflater -> ActivityCameraCaptureBinding.inflate(inflater) }

    private var camera: Camera? = null
    private var imageCapture: ImageCapture? = null
    private var preview: Preview? = null
    private var cameraProvider: ProcessCameraProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setLightStatusBar()
        setContentView(bindingInflater.invoke(layoutInflater).apply {
            binding = this
        }.root)

        setupView()
    }

    private fun setupView() {
        binding.pvCameraCaptureViewFinder.post {
            setUpCamera()
        }

        with(binding.viewCameraTopBar) {
            setPadding(
                paddingLeft,
                this@CameraCaptureActivity.getStatusBarHeight(),
                paddingRight,
                paddingBottom
            )
        }
        binding.ivTopBarBack.setOnClickListener {
            finish()
        }
    }

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraImageUseCases()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraImageUseCases() {
        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview = Preview.Builder()
            .build()

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture
            )
            // Attach the viewFinder's surface provider to Preview use case
            preview?.setSurfaceProvider(binding.pvCameraCaptureViewFinder.surfaceProvider)
        } catch (exception: Exception) {
            Log.e(this::class.java.canonicalName, "$exception CameraImage UseCase binding failed")
        }
    }
}
