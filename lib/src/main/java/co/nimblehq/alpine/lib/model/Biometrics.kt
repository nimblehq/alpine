package co.nimblehq.alpine.lib.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Biometrics(
    val faceImage: Image?,
    val portraitImage: Image?,
    val signatureImage: Image?,
    val fingerprints: List<Bitmap>?
) : Parcelable
