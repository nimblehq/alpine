package co.nimblehq.alpine.sample.ui.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.nimblehq.alpine.lib.model.CameraImage
import co.nimblehq.alpine.lib.model.MrzInfo
import co.nimblehq.alpine.lib.mrz.MrzProcessor
import co.nimblehq.alpine.lib.mrz.MrzProcessorException
import co.nimblehq.alpine.lib.mrz.MrzProcessorResultListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

interface Input {

    fun processImage(
        imageProxy: ImageProxy,
        imageAnalysis: ImageAnalysis
    )

    fun processImageFile(filePath: String)
}

interface Output {

    val navigateToNfcScan: SharedFlow<MrzInfo>

    val onProcessImageFailed: SharedFlow<Throwable>
}

class CameraCaptureViewModel(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel(), Input, Output {

    private val mrzProcessor: MrzProcessor = MrzProcessor.newInstance()

    private val _navigateToNfcScan = MutableSharedFlow<MrzInfo>()
    override val navigateToNfcScan: SharedFlow<MrzInfo>
        get() = _navigateToNfcScan

    private val _onProcessImageFailed = MutableSharedFlow<Throwable>()
    override val onProcessImageFailed: SharedFlow<Throwable>
        get() = _onProcessImageFailed

    override fun processImage(
        imageProxy: ImageProxy,
        imageAnalysis: ImageAnalysis
    ) {
        mrzProcessor.processImage(imageProxy.toCameraImage(), object : MrzProcessorResultListener {
            override fun onSuccess(mrzInfo: MrzInfo) {
                viewModelScope.launch(dispatcher) {
                    imageAnalysis.clearAnalyzer()
                    imageProxy.close()
                    _navigateToNfcScan.emit(mrzInfo)
                }
            }

            override fun onError(e: MrzProcessorException) {
                viewModelScope.launch(dispatcher) {
                    imageProxy.close()
                }
            }
        })
    }

    override fun processImageFile(filePath: String) {
        mrzProcessor.processImageFile(filePath, object : MrzProcessorResultListener {
            override fun onSuccess(mrzInfo: MrzInfo) {
                viewModelScope.launch(dispatcher) {
                    _navigateToNfcScan.emit(mrzInfo)
                }
            }

            override fun onError(e: MrzProcessorException) {
                viewModelScope.launch(dispatcher) {
                    _onProcessImageFailed.emit(e)
                }
            }
        })
    }

    private fun ImageProxy.toCameraImage() = CameraImage(
        width = width,
        height = height,
        cropRect = cropRect,
        format = format,
        rotationDegrees = imageInfo.rotationDegrees,
        planes = planes.map {
            CameraImage.Plane(
                rowStride = it.rowStride,
                pixelStride = it.pixelStride,
                buffer = it.buffer
            )
        }
    )
}
