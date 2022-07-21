package co.nimblehq.alpine.lib.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.security.PublicKey

@Parcelize
data class PassportInfo(
    val documentType: DocumentType?,
    val personDetails: PersonDetails?,
    val biometrics: Biometrics?,
    val additionalPersonDetails: AdditionalPersonDetails?,
    val additionalDocumentDetails: AdditionalDocumentDetails?,
    val documentPublicKey: PublicKey?
) : Parcelable
