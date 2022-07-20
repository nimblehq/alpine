package co.nimblehq.alpine.lib.model

import android.graphics.Bitmap

data class Biometrics(
    val faceImage: Image?,
    val portraitImage: Image?,
    val signatureImage: Image?,
    val fingerprints: List<Bitmap>?
)
