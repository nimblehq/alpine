package co.nimblehq.alpine.lib.mrz

import co.nimblehq.alpine.lib.model.CameraImage
import com.google.mlkit.vision.common.InputImage

interface MrzProcessor {
    fun processImageFile(filePath: String, mrzProcessorResultListener: MrzProcessorResultListener)

    fun processImage(cameraImage: CameraImage, mrzProcessorResultListener: MrzProcessorResultListener)

    fun processImage(inputImage: InputImage, mrzProcessorResultListener: MrzProcessorResultListener)

    companion object {
        @JvmStatic
        fun newInstance(): MrzProcessor = MrzProcessorImpl()
    }
}
