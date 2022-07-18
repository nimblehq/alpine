package co.nimblehq.alpine.lib.model

import android.graphics.Bitmap

data class PersonDetails(
    var name: String? = null,
    var surname: String? = null,
    var personalNumber: String? = null,
    var gender: String? = null,
    var birthDate: String? = null,
    var expiryDate: String? = null,
    var documentNumber: String? = null,
    var nationality: String? = null,
    var issuingState: String? = null,
    var faceImage: Bitmap? = null,
    var faceImageBase64: String? = null,
    var portraitImage: Bitmap? = null,
    var portraitImageBase64: String? = null,
    var signature: Bitmap? = null,
    var signatureBase64: String? = null,
    var fingerprints: List<Bitmap>? = null
)
