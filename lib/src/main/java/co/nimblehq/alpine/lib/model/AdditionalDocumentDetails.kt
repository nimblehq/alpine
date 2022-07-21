package co.nimblehq.alpine.lib.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AdditionalDocumentDetails(
    val dateOfIssue: String?,
    val issuingAuthority: String?
) : Parcelable
