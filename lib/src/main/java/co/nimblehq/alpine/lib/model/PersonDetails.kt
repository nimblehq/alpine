package co.nimblehq.alpine.lib.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonDetails(
    val name: String?,
    val surname: String?,
    val personalNumber: String?,
    val gender: String?,
    val birthDate: String?,
    val expiryDate: String?,
    val documentNumber: String?,
    val nationality: String?,
    val issuingState: String?
) : Parcelable
