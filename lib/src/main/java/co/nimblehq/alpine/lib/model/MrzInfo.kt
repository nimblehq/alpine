package co.nimblehq.alpine.lib.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MrzInfo(
    val documentNumber: String,
    val dateOfBirth: String,
    val dateOfExpiry: String
) : Parcelable {

    companion object {
        fun createFrom(
            documentNumber: String?,
            dateOfBirth: String?,
            dateOfExpiry: String?
        ): MrzInfo? {
            return if (
                listOf(
                    documentNumber,
                    dateOfBirth,
                    dateOfExpiry
                ).all { it != null }
            ) {
                MrzInfo(
                    documentNumber = documentNumber!!,
                    dateOfBirth = dateOfBirth!!,
                    dateOfExpiry = dateOfExpiry!!,
                )
            } else {
                null
            }
        }
    }
}
