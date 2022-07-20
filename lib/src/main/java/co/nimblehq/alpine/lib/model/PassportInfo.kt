package co.nimblehq.alpine.lib.model

import java.security.PublicKey

data class PassportInfo(
    val documentType: DocumentType?,
    val personDetails: PersonDetails?,
    val biometrics: Biometrics?,
    val additionalPersonDetails: AdditionalPersonDetails?,
    val additionalDocumentDetails: AdditionalDocumentDetails?,
    val documentPublicKey: PublicKey?
)
