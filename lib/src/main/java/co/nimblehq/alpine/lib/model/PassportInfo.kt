package co.nimblehq.alpine.lib.model

import java.security.PublicKey

data class PassportInfo(
    var documentType: DocumentType? = null,
    var personDetails: PersonDetails? = null,
    var additionalPersonDetails: AdditionalPersonDetails? = null,
    var additionalDocumentDetails: AdditionalDocumentDetails? = null,
    var documentPublicKey: PublicKey? = null
)
