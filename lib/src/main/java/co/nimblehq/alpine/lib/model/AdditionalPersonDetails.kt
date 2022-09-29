package co.nimblehq.alpine.lib.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AdditionalPersonDetails(
    val custodyInformation: String?,
    val fullDateOfBirth: String?,
    val nameOfHolder: String?,
    val otherNames: List<String>?,
    val otherValidTDNumbers: List<String>?,
    val permanentAddressConstituents: List<String>?,
    val personalNumber: String?,
    val personalSummary: String?,
    val placeOfBirthConstituents: List<String>?,
    val profession: String?,
    val proofOfCitizenship: ByteArray?,
    val tag: Int = 0,
    val tags: List<Int>?,
    val telephone: String?,
    val title: String?
) : Parcelable
