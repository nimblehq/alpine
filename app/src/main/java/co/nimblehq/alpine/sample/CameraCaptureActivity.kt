package co.nimblehq.alpine.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import co.nimblehq.alpine.R
import co.nimblehq.alpine.databinding.ActivityCameraCaptureBinding
import co.nimblehq.alpine.lib.model.MrzInfo
import co.nimblehq.alpine.lib.mrz.*
import co.nimblehq.alpine.sample.extension.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraCaptureActivity : ComponentActivity() {

    private val binding: ActivityCameraCaptureBinding by lazy {
        ActivityCameraCaptureBinding.inflate(layoutInflater)
    }
    private val loadingDialog by lazy {
        createLoadingDialog()
    }
    private val mrzProcessor: MrzProcessor by lazy {
        MrzProcessor.newInstance()
    }

    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private var preview: Preview? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setLightStatusBar()
        setContentView(binding.root)
        setupView()
        bindViewEvents()

        cameraExecutor = Executors.newSingleThreadExecutor()
        outputDirectory = getMediaOutputDirectory()
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

    private fun bindViewEvents() {
        binding.ibCameraCapture.setOnClickListener {
            loadingDialog.show()
            imageCapture?.let {
                val photoFile = createFile(outputDirectory)
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                    .build()

                // Setup image capture listener which is triggered after photo has been taken
                it.takePicture(
                    outputOptions,
                    cameraExecutor,
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onError(exc: ImageCaptureException) {
                            showErrorMessage()
                        }

                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            processImage(photoFile)
                        }
                    })
            }
        }
    }

    private fun processImage(photoFile: File) {
        mrzProcessor.processImageFile(photoFile.absolutePath, object : MrzProcessorResultListener {
            override fun onSuccess(mrzInfo: MrzInfo) {
                loadingDialog.dismiss()
                NfcScanningActivity.start(this@CameraCaptureActivity, mrzInfo)
            }

            override fun onError(e: MrzProcessorException) {
                loadingDialog.dismiss()
                showErrorMessage()
                Log.e(
                    this@CameraCaptureActivity::class.java.canonicalName,
                    "MRZ Processor process failed >>> $e"
                )
            }
        })
    }

    private fun showErrorMessage() {
        displayAlertDialog(
            message = getString(R.string.camera_capture_error_message),
            negativeButtonTitle = getString(R.string.camera_capture_enter_manually),
            negativeCallback = {
                MrzInfoActivity.start(this)
            }
        )
    }

    private fun createFile(
        baseFolder: File,
        format: String = DATE_FORMAT_EXPORT_FILE_NAME,
        extension: String = PHOTO_EXTENSION
    ): File {
        val child = SimpleDateFormat(format, Locale.getDefault())
            .format(System.currentTimeMillis()) + extension
        return File(baseFolder, child)
    }

    companion object {

        private const val DATE_FORMAT_EXPORT_FILE_NAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"

        fun start(activity: Activity) {
            val intent = Intent(activity, CameraCaptureActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
