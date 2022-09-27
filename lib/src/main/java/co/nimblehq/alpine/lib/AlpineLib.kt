package co.nimblehq.alpine.lib

import android.content.Context
import android.nfc.Tag
import co.nimblehq.alpine.lib.model.CameraImage
import co.nimblehq.alpine.lib.model.MrzInfo
import co.nimblehq.alpine.lib.mrz.MrzProcessor
import co.nimblehq.alpine.lib.mrz.MrzProcessorImpl
import co.nimblehq.alpine.lib.mrz.MrzProcessorResultListener
import co.nimblehq.alpine.lib.nfc.ISO_DEP_TIMEOUT_IN_MILLIS
import co.nimblehq.alpine.lib.nfc.NfcReader
import co.nimblehq.alpine.lib.nfc.NfcReaderImpl
import com.google.mlkit.vision.common.InputImage

object AlpineLib {

    private val nfcReader: NfcReader = NfcReaderImpl()
    private val mrzProcessor: MrzProcessor = MrzProcessorImpl()

    fun initializeNfcReader(context: Context) = nfcReader.initialize(context)

    fun readNfc(
        tag: Tag,
        mrzInfo: MrzInfo,
        timeout: Int = ISO_DEP_TIMEOUT_IN_MILLIS
    ) = nfcReader.readNfc(
        tag = tag,
        mrzInfo = mrzInfo,
        timeout = timeout
    )

    fun processImageFile(
        filePath: String,
        mrzProcessorResultListener: MrzProcessorResultListener
    ) = mrzProcessor.processImageFile(
        filePath = filePath,
        mrzProcessorResultListener = mrzProcessorResultListener
    )

    fun processImage(
        cameraImage: CameraImage,
        mrzProcessorResultListener: MrzProcessorResultListener
    ) = mrzProcessor.processImage(
        cameraImage = cameraImage,
        mrzProcessorResultListener = mrzProcessorResultListener
    )

    fun processImage(
        inputImage: InputImage,
        mrzProcessorResultListener: MrzProcessorResultListener
    ) = mrzProcessor.processImage(
        inputImage = inputImage,
        mrzProcessorResultListener = mrzProcessorResultListener
    )
}
