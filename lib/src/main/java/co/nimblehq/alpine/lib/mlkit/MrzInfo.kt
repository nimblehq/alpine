package co.nimblehq.alpine.lib.mlkit

data class MrzInfo(
    val documentNumber: String,
    val dateOfBirth: String,
    val dateOfExpiry: String
) {

    companion object {
        fun createFrom(
            documentNumber: String?,
            dateOfBirth: String?,
            dateOfExpiry: String?
        ): MrzInfo? {
            return if (
                documentNumber != null
                && dateOfBirth != null
                && dateOfExpiry != null
            ) {
                MrzInfo(
                    documentNumber,
                    dateOfBirth,
                    dateOfExpiry,
                )
            } else {
                null
            }
        }
    }
}
