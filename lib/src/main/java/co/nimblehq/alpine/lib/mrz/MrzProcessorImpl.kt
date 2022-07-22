package co.nimblehq.alpine.lib.mrz

import android.graphics.BitmapFactory
import android.util.Log
import co.nimblehq.alpine.lib.model.MrzInfo
import co.nimblehq.alpine.lib.mrz.MrzProcessorException.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.*
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.util.regex.Pattern

internal class MrzProcessorImpl : MrzProcessor {

    companion object {
        private const val TAG = "MlKitWrapper"
        private const val PASSPORT_TD_3_LINE_1_REGEX = "(P[A-Z0-9<]{1})([A-Z]{3})([A-Z0-9<]{39})"
        private const val PASSPORT_TD_3_LINE_2_REGEX =
            "([A-Z0-9<]{9})([0-9]{1})([A-Z]{3})([0-9]{6})([0-9]{1})" +
                "([M|F|X|<]{1})([0-9]{6})([0-9]{1})([A-Z0-9<]{14})([0-9]{1})([0-9]{1})"
        private const val IMAGE_ORIENTATION_IN_DEGREES = 90
        private const val DOCUMENT_NUMBER_MINIMUM_LENGTH = 8
        private const val DATE_OF_BIRTH_MINIMUM_LENGTH = 6
        private const val DATE_OF_EXPIRY_MINIMUM_LENGTH = 6
    }

    private val textRecognizer: TextRecognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }
    private lateinit var mrzProcessorResultListener: MrzProcessorResultListener
    private var scannedTextBuffer: String = ""
    private var isMrzDetected: Boolean = false

    override fun processImageFile(filePath: String, mrzProcessorResultListener: MrzProcessorResultListener) {
        isMrzDetected = false
        this.mrzProcessorResultListener = mrzProcessorResultListener
        try {
            val file = File(filePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                val inputImage = InputImage.fromBitmap(bitmap, IMAGE_ORIENTATION_IN_DEGREES)
                textRecognizer.process(inputImage)
                    .addOnSuccessListener(::onSuccess)
                    .addOnFailureListener { onFailure(TextNotRecognizedMrzProcessorException) }
            } else {
                onFailure(FileNotFoundMrzProcessorException)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(DefaultMrzProcessorException)
        }
    }

    private fun onSuccess(results: Text) {
        scannedTextBuffer = ""
        val textBlocks = results.textBlocks
        if (textBlocks.isNotEmpty()) {
            textBlocks.flatMap { textBlock ->
                textBlock.lines.flatMap { it.elements }
            }.forEach {
                filterScannedText(it)
            }
            if (!isMrzDetected) {
                Log.i(TAG, "MRZ not found")
                mrzProcessorResultListener.onError(MrzInfoNotFoundMrzProcessorException)
            }
        } else {
            Log.i(TAG, "Text not found")
            mrzProcessorResultListener.onError(TextNotFoundMrzProcessorException)
        }
    }

    private fun filterScannedText(element: Text.Element) {
        scannedTextBuffer += element.text
        val patternPassportTD3Line1 = Pattern.compile(PASSPORT_TD_3_LINE_1_REGEX)
        val matcherPassportTD3Line1 = patternPassportTD3Line1.matcher(scannedTextBuffer)
        val patternPassportTD3Line2 = Pattern.compile(PASSPORT_TD_3_LINE_2_REGEX)
        val matcherPassportTD3Line2 = patternPassportTD3Line2.matcher(scannedTextBuffer)
        if (matcherPassportTD3Line1.find() && matcherPassportTD3Line2.find()) {
            val line2 = matcherPassportTD3Line2.group(0)
            var documentNumber = line2?.substring(0, 9)
            documentNumber = documentNumber?.replace("O", "0")
            val dateOfBirth = line2?.substring(13, 19)
            val dateOfExpiry = line2?.substring(21, 27)
            Log.i(
                TAG,
                "Scanned Text Buffer Passport ->>>> Doc Number: $documentNumber " +
                    "DateOfBirth: $dateOfBirth ExpiryDate: $dateOfExpiry"
            )
            val mrzInfo = MrzInfo.createFrom(documentNumber, dateOfBirth, dateOfExpiry)
            finishScanning(mrzInfo)
        }
    }

    private fun onFailure(e: MrzProcessorException) {
        Log.i(TAG, "Text detection failed.$e")
        mrzProcessorResultListener.onError(e)
    }

    private fun finishScanning(mrzInfo: MrzInfo?) {
        mrzInfo?.run {
            val isValidMrzInfo = documentNumber.length >= DOCUMENT_NUMBER_MINIMUM_LENGTH
                && dateOfBirth.length == DATE_OF_BIRTH_MINIMUM_LENGTH
                && dateOfExpiry.length == DATE_OF_EXPIRY_MINIMUM_LENGTH
            if (isValidMrzInfo) {
                isMrzDetected = true
                mrzProcessorResultListener.onSuccess(this)
            } else {
                mrzProcessorResultListener.onError(InvalidMrzInfoMrzProcessorException)
            }
        } ?: mrzProcessorResultListener.onError(InvalidMrzInfoMrzProcessorException)
    }
}
