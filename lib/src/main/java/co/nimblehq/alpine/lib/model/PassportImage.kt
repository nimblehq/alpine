package co.nimblehq.alpine.lib.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PassportImage(
    val bitmap: Bitmap?,
    val base64: String?
) : Parcelable
