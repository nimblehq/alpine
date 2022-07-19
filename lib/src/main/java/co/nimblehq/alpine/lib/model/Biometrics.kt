package co.nimblehq.alpine.lib.model

import android.graphics.Bitmap

data class Biometrics(
    val faceImageBitmap: Bitmap?,
    val faceImageBase64: String?,
    val portraitImageBitmap: Bitmap?,
    val portraitImageBase64: String?,
    val signatureBitmap: Bitmap?,
    val signatureBase64: String?,
    val fingerprints: List<Bitmap>?
)
