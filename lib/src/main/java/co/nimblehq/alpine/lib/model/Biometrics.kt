package co.nimblehq.alpine.lib.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Biometrics(
    val faceImage: PassportImage?,
    val portraitImage: PassportImage?,
    val signatureImage: PassportImage?,
    val fingerprints: List<Bitmap>?
) : Parcelable
